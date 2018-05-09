package org.jinsuoji.jinsuoji.data_access;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 用作数据库层的接口.
 */
public class DBWrapper {
    static private DBHelper helper;

    public DBWrapper(Context context) {
        if (helper == null) {
            helper = new DBHelper(context);
        }
    }

    /**
     * 重建数据表.
     */
    public void recreateTables() {
        helper.recreateTables(helper.getWritableDatabase());
    }

    /**
     * 读操作，不互斥，传入一个操作.
     * @param operation 读操作
     * @param <Ret> 返回值类型
     * @return 返回值
     */
    public <Ret> Ret read(Operation<Ret> operation) {
        return read(operation, new NullSupplier<Ret>());
    }

    /**
     * 读操作，不互斥，传入一个操作.
     *
     * @param operation 读操作
     * @param retSupplierOnRollBack 出错时默认返回
     * @param <Ret> 返回值类型
     * @return 返回值
     */
    public <Ret> Ret read(Operation<Ret> operation, SupplierOnRollBack<Ret> retSupplierOnRollBack) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Ret ret;
        db.beginTransactionNonExclusive();
        try {
            ret = operation.operate(db);

            db.setTransactionSuccessful();
        } catch (AbortException e) {
            ret = retSupplierOnRollBack.get(e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    /**
     * 读写操作，互斥，传入一个操作.
     *
     * @param operation 读操作或写操作
     * @param <Ret> 返回值类型
     * @return 返回值
     */
    public <Ret> Ret write(Operation<Ret> operation) {
        return write(operation, new NullSupplier<Ret>());
    }

    /**
     * 写操作，互斥，传入一个操作.
     * @param operation 读操作或读写操作
     * @param retSupplierOnRollBack 出错时默认返回
     * @param <Ret> 返回值类型
     * @return 返回值
     */
    public <Ret> Ret write(Operation<Ret> operation, SupplierOnRollBack<Ret> retSupplierOnRollBack) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Ret ret;
        db.beginTransaction();
        try {
            ret = operation.operate(db);

            db.setTransactionSuccessful();
        } catch (AbortException e) {
            ret = retSupplierOnRollBack.get(e);
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    /**
     * 用于读操作传入的functor中.
     * @param db 数据库对象，{@link #read(Operation)}的参数
     * @param sql sql语句，查询内容
     * @param args sql语句的参数
     * @param queryOperation 查询时对游标的操作
     * @param <Ret> 返回值类型
     * @return 返回值
     */
    public static <Ret> Ret query(@NonNull SQLiteDatabase db, @NonNull String sql, String[] args,
                                  QueryOperation<Ret> queryOperation) {
        Cursor cursor = db.rawQuery(sql, args);
        try {
            return queryOperation.operate(cursor);
        } finally {
            cursor.close();
        }
    }
}

/**
 * 数据库操作回调.
 * @param <Ret> 返回值类型，若没有使用Void.
 */
@FunctionalInterface
interface Operation<Ret> {
    /**
     * 数据库操作回调.
     * @param db 数据库对象，由wrapper提供
     * @return 数据库操作返回值
     * @throws AbortException 需要回滚时抛出
     */
    Ret operate(SQLiteDatabase db) throws AbortException; // 也会抛出SQL相关的RuntimeException
}

/**
 * 当出现回滚时生成返回值.
 * @param <Ret> 返回值类型
 */
@FunctionalInterface
interface SupplierOnRollBack<Ret> {
    Ret get(AbortException exception);
}

/**
 * {@inheritDoc}
 * <p/>
 * 默认返回null.
 * @param <Ret>
 */
class NullSupplier<Ret> implements SupplierOnRollBack<Ret> {
    @Override
    public Ret get(AbortException exception) {
        return null;
    }
}

/**
 * 抛出这个异常代表将要回滚，用于提取返回值的信息可以存放在这个异常中.
 */
class AbortException extends Exception {
    private Object[] info;

    public AbortException(Object... info) {
        this.info = info;
    }

    public Object[] getInfo() {
        return info;
    }
}

/**
 * 查询操作回调.
 * @param <Ret> 返回值类型.
 */
interface QueryOperation<Ret> {
    /**
     * 数据库操作回调.
     * @param cursor 游标，由{@link DBWrapper#read(Operation)}提供
     * @return 数据库操作返回值
     */
    Ret operate(Cursor cursor);
}

/**
 * 带循环的查询操作回调.
 * @param <Ret> 返回值类型
 */
abstract class QueryAdapter<Ret> implements QueryOperation<Ret> {
    /**
     * {@inheritDoc}
     */
    public final Ret operate(Cursor cursor) {
        Ret ret = null;
        try {
            ret = beforeLoop(cursor);
            while (cursor.moveToNext()) {
                this.inLoop(cursor, ret);
            }
        } catch (AbortException e) {
            return onAbortException(e, ret);
        }
        return afterLoop(ret);
    }
    /**
     * 循环前需要的初始化操作.如初始化返回值.注意若操作过游标要将其放回-1位置.
     * @param cursor 游标
     * @return 返回值的初始化值
     * @throws AbortException 需要结束并返回时
     */
    public abstract Ret beforeLoop(Cursor cursor) throws AbortException;
    /**
     * 循环体内操作.可修改返回值.
     * @param cursor 游标
     * @param ret 返回值的中间值
     * @throws AbortException 需要结束并返回时
     */
    public void inLoop(Cursor cursor, Ret ret) throws AbortException {
        throw new AbortException();
    }
    /**
     * 循环结束后操作.
     * @param ret 返回值的中间值
     * @return 返回值
     */
    public Ret afterLoop(@Nullable Ret ret) {
        return ret;
    }
    /**
     * 循环被{@link AbortException}打断时调用.
     * @param e 返回信息
     * @param ret 返回值的中间值
     * @return 返回值
     */
    public Ret onAbortException(AbortException e, Ret ret) {
        return ret;
    }
}
