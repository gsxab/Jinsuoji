package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jinsuoji.jinsuoji.model.EntryNode;
import org.jinsuoji.jinsuoji.model.Expense;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 记账的数据访问对象.
 */
public class ExpenseDAO {
    private static final String TAG = "o.j.j.d.ExpenseDAO";
    private final Context context;
    private final DBHelper helper;

    public ExpenseDAO(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    /**
     * 查询记账标题.debug用.
     *
     * @return 所有记账标题.
     */
    public List<String> getAllExpenseNames() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> expenses = new ArrayList<>();
        db.beginTransactionNonExclusive();
        try {
            Cursor cursor = db.query(DBHelper.EXPENSE, new String[]{"item"}, null, null,
                    null, null, null, null);
            try {
                while (cursor.moveToNext()) {
                    expenses.add(cursor.getString(0));
                }
            } finally {
                cursor.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return expenses;
    }

    /**
     * 查询指定月数据,按日期归类并排列.
     *
     * @see EntryNode
     * @return 所有记账结点数据.
     */
    public List<EntryNode> getMonthlyByDate(int year, int month) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<EntryNode> entryNodes;
        db.beginTransactionNonExclusive();
        try {
            Cursor cursor = db.rawQuery("SELECT expense.id, item, time, date(time), money, name " +
                            "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                            " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                            "WHERE time BETWEEN ? AND ? " +
                            "ORDER BY time DESC",
                    DateUtils.makeDateInterval(year, month)
            );
            try {
                if (!cursor.moveToLast())
                    return Collections.emptyList();
                int size = cursor.getPosition() + 1;
                entryNodes = new ArrayList<>(size + 31);
                String lastDate = null;
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    if (!cursor.getString(3).equals(lastDate)) {
                        lastDate = cursor.getString(3);
                        entryNodes.add(new EntryNode.ExpenseCategory(lastDate));
                    }
                    Date date;
                    try {
                        date = DateUtils.fromDateTimeString(cursor.getString(2));
                    } catch (ParseException e) {
                        // e.printStackTrace();
                        // TODO date format error
                        date = null;
                    }
                    Expense expense = new Expense(
                            cursor.getInt(0),
                            cursor.getString(1),
                            date,
                            cursor.getInt(4),
                            cursor.getString(5));
                    entryNodes.add(new EntryNode.ExpenseItem(expense));
                }
                db.setTransactionSuccessful();
            } finally {
                cursor.close();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return entryNodes;
    }

    /**
     * 查询指定月数据,按分类归类并排列.
     *
     * @see EntryNode
     * @return 所有记账结点数据.
     */
    public List<EntryNode> getMonthlyByCategory(int year, int month) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<EntryNode> entryNodes;
        db.beginTransactionNonExclusive();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT " + DBHelper.EXPENSE + ".id, item, time, money, name AS cate_name " +
                            "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                            " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                            "WHERE time BETWEEN ? AND ? " +
                            "ORDER BY cate_name DESC",
                    DateUtils.makeDateInterval(year, month)
            );
            try {
                if (!cursor.moveToLast()) return Collections.emptyList();
                int size = cursor.getPosition() + 1;
                entryNodes = new ArrayList<>(size + 31);
                String lastCategory = null;
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    if (!cursor.getString(4).equals(lastCategory)) {
                        lastCategory = cursor.getString(4);
                        entryNodes.add(new EntryNode.ExpenseCategory(lastCategory));
                    }
                    Date date;
                    try {
                        date = DateUtils.fromDateTimeString(cursor.getString(2));
                    } catch (ParseException e) {
                        // e.printStackTrace();
                        // TODO date format error
                        date = null;
                    }
                    Expense expense = new Expense(
                            cursor.getInt(0),
                            cursor.getString(1),
                            date,
                            cursor.getInt(3),
                            cursor.getString(4));
                    entryNodes.add(new EntryNode.ExpenseItem(expense));
                }

                db.setTransactionSuccessful();
            } finally {
                cursor.close();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return entryNodes;
    }

    /**
     * 查询指定日数据,按时间倒序排列.返回的每个都是{@link EntryNode.ExpenseItem}.
     *
     * @return 所有记账结点数据.
     */
    public List<EntryNode> getDaily(int year, int month, int dateInMonth) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<EntryNode> entryNodes;
        db.beginTransactionNonExclusive();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT " + DBHelper.EXPENSE + ".id, item, time, money, name AS cate_name " +
                            "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                            " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                            "WHERE date(time) = ? " +
                            "ORDER BY time DESC",
                    new String[]{DateUtils.toDateString(DateUtils.makeDate(year, month, dateInMonth))}
            );
            try {
                if (!cursor.moveToLast()) return Collections.emptyList();
                int size = cursor.getPosition() + 1;
                entryNodes = new ArrayList<>(size + 31);
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    Date date;
                    try {
                        date = DateUtils.fromDateTimeString(cursor.getString(2));
                    } catch (ParseException e) {
                        // e.printStackTrace();
                        // TODO date format error
                        date = null;
                    }
                    Expense expense = new Expense(
                            cursor.getInt(0),
                            cursor.getString(1),
                            date,
                            cursor.getInt(3),
                            cursor.getString(4));
                    entryNodes.add(new EntryNode.ExpenseItem(expense));
                }

                db.setTransactionSuccessful();
            } finally {
                cursor.close();
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return entryNodes;
    }

    private int createOrGetCategory(String cateName, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", cateName);
        db.insertWithOnConflict(
                DBHelper.EXPENSE_CATE,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE);

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + DBHelper.EXPENSE_CATE + " WHERE name = ?",
                new String[]{ cateName }
        );
        try {
            cursor.moveToNext(); // 必然存在
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * 查询一个分类的类别id，若不存在则插入并返回.
     *
     * @param cateName 分类名
     * @return 分类id
     */
    public int createOrGetCategory(String cateName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int id;
        db.beginTransaction();
        try {
            id = createOrGetCategory(cateName, db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    private void addExpense(Expense expense, int cateId, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("item", expense.getItem());
        values.put("time", DateUtils.toDateTimeString(expense.getDatetime()));
        values.put("money", expense.getMoney());
        values.put("category_id", cateId);
        long ret = db.insert(
                DBHelper.EXPENSE,
                null,
                values
        );
        Log.d(TAG, "addExpense: " + ret);
        Cursor cursor = db.rawQuery("select last_insert_rowid()",null);
        try {
            int id;
            cursor.moveToFirst();
            id = cursor.getInt(0);
            expense.setId(id);
        } finally {
            cursor.close();
        }
    }

    /**
     * 向数据库插入新的记账记录.传入的记录的id字段会被忽略，执行后设置为新建的id.
     *
     * @param expense 要插入的新的记账记录.id会被设置.
     */
    public void addExpense(Expense expense) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            int id = createOrGetCategory(expense.getCategory());
            addExpense(expense, id, db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
