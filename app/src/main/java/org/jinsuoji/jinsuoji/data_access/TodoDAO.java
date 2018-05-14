package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jinsuoji.jinsuoji.model.EntryNode;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jinsuoji.jinsuoji.data_access.DBWrapper.query;

/**
 * 记账的数据访问对象.
 */
public class TodoDAO {
    private static final String TAG = "o.j.j.d.TodoDAO";
    private final Context context;
    private final DBWrapper wrapper;

    private static QueryOperation<List<Todo>> operation =new QueryAdapter<List<Todo>>() {
        @Override
        public List<Todo> beforeLoop(Cursor cursor) {
            if (!cursor.moveToLast()) {
                return Collections.emptyList();
            }
            int size = cursor.getPosition() + 1;
            cursor.moveToPosition(-1);
            return new ArrayList<>(size);
        }

        @Override
        public void inLoop(Cursor cursor, List<Todo> entryNodes) {
            Todo todo = new Todo(
                    cursor.getInt(0),
                    DateUtils.fromDateString(cursor.getString(4)),
                    cursor.getString(1),
                    cursor.getInt(3),
                    cursor.getString(2),
                    cursor.getInt(4) == 1);
            entryNodes.add(todo);
        }
    };

    public TodoDAO(Context context) {
        wrapper = new DBWrapper(context);
        this.context = context;
    }

    /**
     * 查询任务标题.debug用.
     *
     * @return 所有任务标题.
     */
    public List<String> getAllTodoNames() {
        return wrapper.read(new Operation<List<String>>() {
            @Override
            public List<String> operate(SQLiteDatabase db) {
                return query(db, "SELECT name FROM " + DBHelper.TODO, null, new QueryAdapter<List<String>>() {
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
     * 查询指定日数据,按时间倒序排列.返回的每个都是{@link EntryNode.ExpenseItem}.
     *
     * @return 当日所有任务数据.
     */
    public List<Todo> getDaily(final int year, final int month, final int dateInMonth) {
        return wrapper.read(new Operation<List<Todo>>() {
            @Override
            public List<Todo> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, name, memo, priority, time, finished " +
                                "FROM " + DBHelper.TODO + " " +
                                "WHERE date(time) = ? " +
                                "ORDER BY finished ASC, time DESC",
                        new String[]{DateUtils.toDateString(DateUtils.makeDate(year, month, dateInMonth))},
                        operation);
            }
        });
    }

    /**
     * 按状态查询列表.
     *
     * @return 所有指定状态的任务.
     */
    public List<Todo> getTodoListByFinished(final boolean finished) {
        return wrapper.read(new Operation<List<Todo>>() {
            @Override
            public List<Todo> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, name, memo, priority, time, finished " +
                                "FROM " + DBHelper.TODO + " " +
                                "WHERE finished = ? " +
                                "ORDER BY time DESC",
                        new String[]{finished ? "1" : "0"},
                        operation);
            }
        });
    }

    /**
     * 改变任务状态.
     *
     * @param id 任务id
     * @param finished 新的完成度.
     */
    public void changeStateById(final int id, final boolean finished) {
        wrapper.write(new Operation<Void>() {
            @Override
            public Void operate(SQLiteDatabase db) {
                ContentValues values = new ContentValues();
                values.put("finished", finished ? "1" : "0");
                db.update(DBHelper.TODO, values, "id = ?",
                        new String[]{String.valueOf(id)});
                return null;
            }
        });
    }

    /**
     * 向数据库插入新的任务记录.传入的记录的id字段会被忽略，执行后设置为新建的id.
     *
     * @param todoItem 要插入的新的任务记录.id会被设置.
     */
    public void addTodo(final Todo todoItem) {
        // TODO
    }

    /**
     * 编辑任务记录，传入的记录id字段为先前的id.此id将决定更新操作中被更新的记录.
     *
     * @param todoItem 修改过的记录
     */
    public void editTodo(final Todo todoItem) {
        // TODO
    }
}
