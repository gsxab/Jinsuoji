package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jinsuoji.jinsuoji.model.EntryNode;
import org.jinsuoji.jinsuoji.model.Expense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jinsuoji.jinsuoji.data_access.DBWrapper.query;

/**
 * 记账的数据访问对象.
 */
public class ExpenseDAO {
    private static final String TAG = "o.j.j.d.ExpenseDAO";
    private final Context context;
    private final DBWrapper wrapper;

    public ExpenseDAO(Context context) {
        wrapper = new DBWrapper(context);
        this.context = context;
    }

    /**
     * 查询记账标题.debug用.
     *
     * @return 所有记账标题.
     */
    public List<String> getAllExpenseNames() {
        return wrapper.read(new Operation<List<String>>() {
            @Override
            public List<String> operate(SQLiteDatabase db) {
                return query(db, "SELECT item FROM " + DBHelper.EXPENSE, null, new QueryAdapter<List<String>>() {
                            @Override
                            public List<String> beforeLoop(Cursor cursor) {
                                return new ArrayList<>();
                            }

                            @Override
                            public void inLoop(Cursor cursor, List<String> strings) {
                                strings.add(cursor.getString(0));
                            }
                        });
            }
        });
    }

    /**
     * 查询指定月数据,按日期归类并排列.
     *
     * @return 所有记账结点数据.
     * @see EntryNode
     */
    public List<EntryNode> getMonthlyByDate(final int year, final int month) {
        return wrapper.read(new Operation<List<EntryNode>>() {
            @Override
            public List<EntryNode> operate(SQLiteDatabase db) {
                return query(db, "SELECT expense.id, item, time, money, name " +
                                "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                                " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                                "WHERE time >= ? AND time < ? " +
                                "ORDER BY time DESC",
                        DateUtils.makeDateInterval(year, month), new QueryAdapter<List<EntryNode>>() {
                    String lastDate = null;

                    @Override
                    public List<EntryNode> beforeLoop(Cursor cursor) {
                        if (!cursor.moveToLast()) {
                            return Collections.emptyList() ;
                        }
                        int size = cursor.getPosition() + 1;
                        cursor.moveToPosition(-1);
                        return new ArrayList<>(size + 31);
                    }

                    @Override
                    public void inLoop(Cursor cursor, List<EntryNode> entryNodes) {
                        if (!cursor.getString(2).equals(lastDate)) {
                            lastDate = cursor.getString(2);
                            entryNodes.add(new EntryNode.ExpenseCategory(lastDate));
                        }
                        Expense expense = new Expense(
                                cursor.getInt(0),
                                cursor.getString(1),
                                DateUtils.fromDateString(cursor.getString(2)),
                                cursor.getInt(3),
                                cursor.getString(4));
                        entryNodes.add(new EntryNode.ExpenseItem(expense));
                    }

                    @Override
                    public List<EntryNode> onAbortException(AbortException e, List<EntryNode> entryNodes) {
                        return Collections.emptyList();
                    }
                });
            }
        });
    }

    /**
     * 查询指定月数据,按分类归类并排列.
     *
     * @return 所有记账结点数据.
     * @see EntryNode
     */
    public List<EntryNode> getMonthlyByCategory(final int year, final int month) {
        return wrapper.read(new Operation<List<EntryNode>>() {
            @Override
            public List<EntryNode> operate(SQLiteDatabase db) {
                return query(db, "SELECT " + DBHelper.EXPENSE + ".id, item, time, money, name AS cate_name " +
                                "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                                " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                                "WHERE time BETWEEN ? AND ? " +
                                "ORDER BY cate_name DESC", DateUtils.makeDateInterval(year, month),
                        new QueryAdapter<List<EntryNode>>() {
                    String lastCategory = null;

                    @Override
                    public List<EntryNode> beforeLoop(Cursor cursor) {
                        if (!cursor.moveToLast()) {
                            return Collections.emptyList();
                        }
                        int size = cursor.getPosition() + 1;
                        cursor.moveToPosition(-1);
                        return new ArrayList<>(size + 31);
                    }

                    @Override
                    public void inLoop(Cursor cursor, List<EntryNode> entryNodes) {
                        if (!cursor.getString(4).equals(lastCategory)) {
                            lastCategory = cursor.getString(4);
                            entryNodes.add(new EntryNode.ExpenseCategory(lastCategory));
                        }
                        Expense expense = new Expense(
                                cursor.getInt(0),
                                cursor.getString(1),
                                DateUtils.fromDateString(cursor.getString(2)),
                                cursor.getInt(3),
                                cursor.getString(4));
                        entryNodes.add(new EntryNode.ExpenseItem(expense));
                    }
                });
            }
        });
    }

    /**
     * 查询指定日数据,按时间倒序排列.返回的每个都是{@link EntryNode.ExpenseItem}.
     *
     * @return 所有记账结点数据.
     */
    public List<EntryNode> getDaily(final int year, final int month, final int dateInMonth) {
        return wrapper.read(new Operation<List<EntryNode>>() {
            @Override
            public List<EntryNode> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT " + DBHelper.EXPENSE + ".id, item, time, money, name AS cate_name " +
                                "FROM " + DBHelper.EXPENSE + " JOIN " + DBHelper.EXPENSE_CATE +
                                " ON " + DBHelper.EXPENSE + ".category_id = " + DBHelper.EXPENSE_CATE + ".id " +
                                "WHERE date(time) = ? " +
                                "ORDER BY time DESC",
                        new String[]{DateUtils.toDateString(DateUtils.makeDate(year, month, dateInMonth))},
                        new QueryAdapter<List<EntryNode>>() {
                    @Override
                    public List<EntryNode> beforeLoop(Cursor cursor) {
                        if (!cursor.moveToLast()) {
                            return Collections.emptyList();
                        }
                        int size = cursor.getPosition() + 1;
                        cursor.moveToPosition(-1);
                        return new ArrayList<>(size);
                    }

                    @Override
                    public void inLoop(Cursor cursor, List<EntryNode> entryNodes) {
                        Expense expense = new Expense(
                                cursor.getInt(0),
                                cursor.getString(1),
                                DateUtils.fromDateString(cursor.getString(2)),
                                cursor.getInt(3),
                                cursor.getString(4));
                        entryNodes.add(new EntryNode.ExpenseItem(expense));
                    }
                });
            }
        });
    }

    private int createOrGetCategory(String cateName, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", cateName);
        db.insertWithOnConflict(DBHelper.EXPENSE_CATE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        return query(db, "SELECT id FROM " + DBHelper.EXPENSE_CATE + " WHERE name = ?",
                new String[]{cateName}, new QueryOperation<Integer>() {
            @Override
            public Integer operate(Cursor cursor) {
                cursor.moveToNext(); // 必然存在
                return cursor.getInt(0);
            }
        });
    }

    /**
     * 查询一个分类的类别id，若不存在则插入并返回.
     *
     * @param cateName 分类名
     * @return 分类id
     */
    public int createOrGetCategory(final String cateName) {
        return wrapper.write(new Operation<Integer>() {
            @Override
            public Integer operate(SQLiteDatabase db) {
                return createOrGetCategory(cateName, db);
            }
        });
    }

    private void addExpense(Expense expense, int cateId, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("item", expense.getItem());
        values.put("time", DateUtils.toDateString(expense.getDatetime()));
        values.put("money", expense.getMoney());
        values.put("category_id", cateId);
        db.insert(DBHelper.EXPENSE, null, values);
        expense.setId(query(db, "SELECT last_insert_rowid()", null, new QueryOperation<Integer>() {
            @Override
            public Integer operate(Cursor cursor) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
        }));
    }

    /**
     * 向数据库插入新的记账记录.传入的记录的id字段会被忽略，执行后设置为新建的id.
     *
     * @param expense 要插入的新的记账记录.这个对象的id会被设置.
     */
    public void addExpense(final Expense expense) {
        wrapper.write(new Operation<Void>() {
            @Override
            public Void operate(SQLiteDatabase db) {
                int id = createOrGetCategory(expense.getCategory(), db);
                addExpense(expense, id, db);
                return null;
            }
        });
    }

    private int getMonthlyTotal(final int year, final int month, final String condition) {
        return wrapper.read(new Operation<Integer>() {
            @Override
            public Integer operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT sum(money) FROM " + DBHelper.EXPENSE + " " +
                                "WHERE time >= ? AND time < ? AND " + condition,
                        DateUtils.makeDateInterval(year, month),
                        new QueryOperation<Integer>() {
                            @Override
                            public Integer operate(Cursor cursor) {
                                cursor.moveToNext();
                                return cursor.getInt(0);
                            }
                        });
            }
        });
    }

    /**
     * 统计月收入.
     * @param year 指定的年
     * @param month 指定的月
     * @return 月收入
     */
    public int getMonthlyIncome(int year, int month) {
        return getMonthlyTotal(year, month,"money > 0");
    }

    /**
     * 统计月支出.
     * @param year 指定的年
     * @param month 指定的月
     * @return 月支出
     */
    public int getMonthlyExpense(int year, int month) {
        return getMonthlyTotal(year, month, "money < 0");
    }

    /**
     * 编辑记账记录，传入的记录id字段为先前的id.此id将决定更新操作中被更新的记录.
     *
     * @param expense 修改过的记录
     */
    public void editExpense(final Expense expense) {
        wrapper.write(new Operation<Void>() {
            @Override
            public Void operate(SQLiteDatabase db) {
                int cateId = createOrGetCategory(expense.getCategory(), db);
                ContentValues values = new ContentValues();
                values.put("item", expense.getItem());
                values.put("time", DateUtils.toDateString(expense.getDatetime()));
                values.put("money", expense.getMoney());
                values.put("category_id", cateId);
                db.update(DBHelper.EXPENSE, values, "id = ?",
                        new String[]{String.valueOf(expense.getId())});
                return null;
            }
        });
    }

    /**
     * 删除记账记录.传入的是要删除的记录的id.
     *
     * @param id 要删除的id
     */
    public void delExpense(final int id) {
        wrapper.write(new Operation<Void>() {
            @Override
            public Void operate(SQLiteDatabase db) {
                db.delete(DBHelper.EXPENSE, "id = ?", new String[]{String.valueOf(id)});
                return null;
            }
        });
    }

    /**
     * 返回全部分类名.
     * @return 全部分类名
     */
    public List<String> getAllCategories() {
        return wrapper.read(new Operation<List<String>>() {
            @Override
            public List<String> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT name FROM " + DBHelper.EXPENSE_CATE,
                        null,
                        new QueryAdapter<List<String>>() {
                            @Override
                            public List<String> beforeLoop(Cursor cursor) {
                                if (!cursor.moveToLast()) {
                                    return Collections.emptyList();
                                }
                                int size = cursor.getPosition() + 1;
                                cursor.moveToPosition(-1);
                                return new ArrayList<>(size);
                            }

                            @Override
                            public void inLoop(Cursor cursor, List<String> strings) {
                                strings.add(cursor.getString(0));
                            }
                        });
            }
        });
    }
}
