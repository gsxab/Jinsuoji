package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jinsuoji.jinsuoji.model.Todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.jinsuoji.jinsuoji.data_access.DBWrapper.query;

/**
 * 将数据库导出为Json写到指定输出流，或将Json输入流导入数据库.
 */
public class Serializer {
    /**
     * 对应数据库的表.model包的Expense对应的是数据库的视图,
     */
    static class Expense {
        public Expense(int id, String item, Date datetime, int money, int categoryId) {
            this.id = id;
            this.item = item;
            this.datetime = datetime;
            this.money = money;
            this.categoryId = categoryId;
        }
        int id;
        String item;
        Date datetime;
        int money;
        int categoryId;
    }

    /**
     * 数据库的分类表.
     */
    static class ExpenseCategory {
        ExpenseCategory(int id, String name) {
            this.id = id;
            this.name = name;
        }
        int id;
        String name;
    }

    static class DBMirror {
        DBMirror() {}
        List<Todo> todoList;
        List<Expense> expenseList;
        List<ExpenseCategory> expenseCategoryList;
        void backup(DBWrapper wrapper) {
            wrapper.read(new Operation<Void>() {
                @Override
                public Void operate(SQLiteDatabase db) {
                    query(db, "SELECT id, time, name, priorty, memo, finished FROM "
                            + DBHelper.TODO, null, new QueryAdapter<Void>() {
                        @Override
                        public Void beforeLoop(Cursor cursor) {
                            if (!cursor.moveToLast()) todoList = Collections.emptyList();
                            todoList = new ArrayList<>(cursor.getPosition() + 1);
                            cursor.moveToPosition(-1);
                            return null;
                        }

                        @Override
                        public void inLoop(Cursor cursor, Void aVoid) {
                            todoList.add(new Todo(
                                    cursor.getInt(0),
                                    DateUtils.fromDateTimeString(cursor.getString(1)),
                                    cursor.getString(2),
                                    cursor.getInt(3),
                                    cursor.getString(4),
                                    cursor.getInt(5) == 1
                            ));
                        }
                    });
                    query(db, "SELECT id, item, time, money, category_id FROM "
                            + DBHelper.EXPENSE, null, new QueryAdapter<Void>() {
                        @Override
                        public Void beforeLoop(Cursor cursor) {
                            if (!cursor.moveToLast()) expenseList = Collections.emptyList();
                            expenseList = new ArrayList<>(cursor.getPosition() + 1);
                            cursor.moveToPosition(-1);
                            return null;
                        }

                        @Override
                        public void inLoop(Cursor cursor, Void aVoid) {
                            expenseList.add(new Expense(
                                    cursor.getInt(0),
                                    cursor.getString(1),
                                    DateUtils.fromDateTimeString(cursor.getString(2)),
                                    cursor.getInt(3),
                                    cursor.getInt(4)
                            ));
                        }
                    });
                    query(db, "SELECT id, name FROM "
                            + DBHelper.EXPENSE_CATE, null, new QueryAdapter<Void>() {
                        @Override
                        public Void beforeLoop(Cursor cursor) {
                            if (!cursor.moveToLast()) expenseCategoryList = Collections.emptyList();
                            expenseCategoryList = new ArrayList<>(cursor.getPosition() + 1);
                            cursor.moveToPosition(-1);
                            return null;
                        }

                        @Override
                        public void inLoop(Cursor cursor, Void aVoid) {
                            expenseCategoryList.add(new ExpenseCategory(
                                    cursor.getInt(0),
                                    cursor.getString(1)
                            ));
                        }
                    });

                    return null;
                }
            });
        }
        void patch(DBWrapper wrapper) {
            wrapper.write(new Operation<Void>() {
                @Override
                public Void operate(SQLiteDatabase db) {
                    for (Todo todo : todoList) {
                        TodoDAO.replaceTodo(todo, db);
                    }
                    for (ExpenseCategory category : expenseCategoryList) {
                        ContentValues values = new ContentValues();
                        values.put("id", category.id);
                        values.put("name", category.name);
                        db.replace(DBHelper.EXPENSE_CATE, null, values);
                    }
                    for (Expense expense : expenseList) {
                        ContentValues values = new ContentValues();
                        values.put("id", expense.id);
                        values.put("item", expense.item);
                        values.put("time", DateUtils.toDateString(expense.datetime));
                        values.put("money", expense.money);
                        values.put("category_id", expense.categoryId);
                        db.replace(DBHelper.EXPENSE, null, values);
                    }
                    return null;
                }
            });
        }
    }

    DBWrapper mWrapper;
    static JsonFactory factory = new JsonFactory();

    public Serializer(Context context) {
        mWrapper = new DBWrapper(context);
    }

    /**
     * 将数据库导出到输出流.
     * @param stream 输出流
     * @throws IOException 输出出现异常
     */
    public void export(OutputStream stream) throws IOException {
        JsonGenerator generator = factory.createGenerator(stream);
        ObjectMapper mapper = new ObjectMapper();
        DBMirror mirror = new DBMirror();
        mirror.backup(mWrapper);
        mapper.writeValue(generator, mirror);
        stream.flush();
    }

    /**
     * 将输入流内容作为数据库补丁导入.
     * 注意如果不是同一个数据库的应该会冲突.不建议使用.
     * @param stream 输入流
     * @throws IOException 输入出现异常
     */
    public void patchImport(InputStream stream) throws IOException {
        JsonParser parser = factory.createParser(stream);
        ObjectMapper mapper = new ObjectMapper();
        DBMirror mirror = mapper.readValue(parser, DBMirror.class);
        mirror.patch(mWrapper);
    }

    /**
     * 将输入流内容作为数据库新内容导入.
     * @param stream 输入流
     * @throws IOException 输入出现异常
     */
    public void replaceImport(InputStream stream) throws IOException {
        mWrapper.recreateTables();
        patchImport(stream);
    }
}
