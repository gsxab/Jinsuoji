package org.jinsuoji.jinsuoji.data_access;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jinsuoji.jinsuoji.model.Zhongcao;
import org.jinsuoji.jinsuoji.model.ZhongcaoCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jinsuoji.jinsuoji.data_access.DBWrapper.query;

public class ZhongcaoDAO {
    private static final String TAG = "o.j.j.d.TodoDAO";
    private final DBWrapper wrapper;

    public ZhongcaoDAO(Context context) {
        wrapper = new DBWrapper(context);
    }

    private class ZhongcaoQueryOperation extends QueryAdapter<List<Zhongcao>> {
        @Override
        public List<Zhongcao> beforeLoop(Cursor cursor) {
            if (!cursor.moveToLast()) {
                return Collections.emptyList();
            }
            int size = cursor.getPosition() + 1;
            cursor.moveToPosition(-1);
            return new ArrayList<>(size);
        }

        @Override
        public void inLoop(Cursor cursor, List<Zhongcao> entryNodes) {
            Zhongcao zhongcao = new Zhongcao(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3));
            entryNodes.add(zhongcao);
        }
    }

    private class ZhongcaoCategoryQueryOperation extends QueryAdapter<List<ZhongcaoCategory>> {
        @Override
        public List<ZhongcaoCategory> beforeLoop(Cursor cursor) {
            if (!cursor.moveToLast()) {
                return Collections.emptyList();
            }
            int size = cursor.getPosition() + 1;
            cursor.moveToPosition(-1);
            return new ArrayList<>(size);
        }

        @Override
        public void inLoop(Cursor cursor, List<ZhongcaoCategory> entryNodes) {
            ZhongcaoCategory zhongcaoCategory = new ZhongcaoCategory(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
            entryNodes.add(zhongcaoCategory);
        }
    }

    public List<Zhongcao> getAllRecords() {
        return wrapper.read(new Operation<List<Zhongcao>>() {
            @Override
            public List<Zhongcao> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, picture, memo, category_id FROM " + DBHelper.ZHONGCAO,
                        null,
                        new ZhongcaoQueryOperation());
            }
        });
    }

    public List<Zhongcao> getRecordsByCategoryId(final int id) {
        return wrapper.read(new Operation<List<Zhongcao>>() {
            @Override
            public List<Zhongcao> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, picture, memo, category_id " +
                                "FROM " + DBHelper.ZHONGCAO + " " +
                                "WHERE category_id = ?",
                        new String[]{String.valueOf(id)},
                        new ZhongcaoQueryOperation());
            }
        });
    }

    public Zhongcao getRecordById(final int id) {
        return wrapper.read(new Operation<Zhongcao>() {
            @Override
            public Zhongcao operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, picture, memo, category_id " +
                                "FROM " + DBHelper.ZHONGCAO + " " +
                                "WHERE id = ?",
                        new String[]{String.valueOf(id)},
                        new QueryOperation<Zhongcao>() {
                            @Override
                            public Zhongcao operate(Cursor cursor) {
                                if (!cursor.moveToFirst()) return null;
                                return new Zhongcao(cursor.getInt(0),
                                        cursor.getString(1),
                                        cursor.getString(2),
                                        cursor.getInt(3));
                            }
                        });
            }
        });
    }

    public void createZhongcao(Zhongcao zhongcao) {
        // TODO
    }

    public void editZhongcao(Zhongcao zhongcao) {
        // TODO
    }

    public void deleteZhongcao(final int id) {
        // TODO
    }

    public ZhongcaoCategory getOrCreateCategory(String name) {
        // TODO
        return null;
    }

    public List<ZhongcaoCategory> getAllCategories() {
        return wrapper.read(new Operation<List<ZhongcaoCategory>>() {
            @Override
            public List<ZhongcaoCategory> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, picture, memo, category_id " +
                                "FROM " + DBHelper.ZHONGCAO_CATE,
                        null,
                        new ZhongcaoCategoryQueryOperation());
            }
        });
    }

    public ZhongcaoCategory getCategoryById(final int id) {
        // TODO
        return null;
    }

    public void editCategory(ZhongcaoCategory zhongcaoCategory) {
        // TODO 注意重名检查
    }

    public void deleteCategory(final int id) {
        // TODO
    }
}
