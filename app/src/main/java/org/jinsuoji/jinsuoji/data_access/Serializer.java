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
@SuppressWarnings({"unused", "WeakerAccess"})
public class Serializer {
    /**
     * 对应数据库的表.model包的Expense对应的是数据库的视图,
     */
    static class ExpenseBean {
        public ExpenseBean(int id, String item, Date datetime, int money, int categoryId) {
            this.id = id;
            this.item = item;
            this.datetime = datetime;
            this.money = money;
            this.categoryId = categoryId;
        }

        private int id;
        private String item;
        private Date datetime;
        private int money;
        private int categoryId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }
    }

    /**
     * 数据库的分类表.
     */
    static class ExpenseCategoryBean {
        ExpenseCategoryBean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class DBMirror {
        public DBMirror() {}

        private List<Todo> todoList;
        private List<ExpenseBean> expenseList;
        private List<ExpenseCategoryBean> expenseCategoryList;

        public List<Todo> getTodoList() {
            return todoList;
        }

        public void setTodoList(List<Todo> todoList) {
            this.todoList = todoList;
        }

        public List<ExpenseBean> getExpenseList() {
            return expenseList;
        }

        public void setExpenseList(List<ExpenseBean> expenseList) {
            this.expenseList = expenseList;
        }

        public List<ExpenseCategoryBean> getExpenseCategoryList() {
            return expenseCategoryList;
        }

        public void setExpenseCategoryList(List<ExpenseCategoryBean> expenseCategoryList) {
            this.expenseCategoryList = expenseCategoryList;
        }

        private static DBMirror loadExample() {
            DBMirror mirror = new DBMirror();
            mirror.todoList = new ArrayList<>();
            mirror.todoList.add(new Todo(1, DateUtils.fromDateTimeString("2018-05-21 18:14"),
                    "跑步", "一千米", null, false));
            mirror.todoList.add(new Todo(2, DateUtils.fromDateTimeString("2018-05-21 18:20"),
                    "跑步", "两千米", null,true));
            mirror.todoList.add(new Todo(3, DateUtils.fromDateTimeString("2018-05-21 18:21"),
                    "跑步", "三千米", null, false));
            mirror.todoList.add(new Todo(4, DateUtils.fromDateTimeString("2018-05-21 18:33"),
                    "遛狗", "四千米", null,true));
            mirror.todoList.add(new Todo(5, DateUtils.fromDateTimeString("2018-05-20 18:20"),
                    "跑步", "两千米", null,true));
            mirror.expenseCategoryList = new ArrayList<>();
            mirror.expenseCategoryList.add(new ExpenseCategoryBean(1, "食品"));
            mirror.expenseCategoryList.add(new ExpenseCategoryBean(2, "逛街"));
            mirror.expenseCategoryList.add(new ExpenseCategoryBean(3, "生活费"));
            mirror.expenseList = new ArrayList<>();
            mirror.expenseList.add(new ExpenseBean(1, "牛奶",
                    DateUtils.fromDateString("2018-05-11"), -500, 1));
            mirror.expenseList.add(new ExpenseBean(2, "牛奶",
                    DateUtils.fromDateString("2018-05-15"), -500, 1));
            mirror.expenseList.add(new ExpenseBean(3, "牛奶",
                    DateUtils.fromDateString("2018-05-19"), -500, 1));
            mirror.expenseList.add(new ExpenseBean(4, "早饭",
                    DateUtils.fromDateString("2018-05-11"), -500, 1));
            mirror.expenseList.add(new ExpenseBean(5, "大衣",
                    DateUtils.fromDateString("2018-05-19"), -500, 2));
            mirror.expenseList.add(new ExpenseBean(6, "牛奶",
                    DateUtils.fromDateString("2018-05-20"), -500, 1));
            mirror.expenseList.add(new ExpenseBean(1, "生活费",
                    DateUtils.fromDateString("2018-05-18"), 100000, 3));
            return mirror;
        }

        private void backup(DBWrapper wrapper) {
            wrapper.read(new Operation<Void>() {
                @Override
                public Void operate(SQLiteDatabase db) {
                    query(db, "SELECT id, time, name, memo, finished, reminderTime FROM "
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
                                    cursor.getString(3),
                                    DateUtils.fromDateTimeString(cursor.getString(5)),
                                    cursor.getInt(4) == 1
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
                            expenseList.add(new ExpenseBean(
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
                            expenseCategoryList.add(new ExpenseCategoryBean(
                                    cursor.getInt(0),
                                    cursor.getString(1)
                            ));
                        }
                    });

                    return null;
                }
            });
        }
        private void patch(DBWrapper wrapper) {
            wrapper.write(new Operation<Void>() {
                @Override
                public Void operate(SQLiteDatabase db) {
                    for (Todo todo : todoList) {
                        TodoDAO.replaceTodo(todo, db);
                    }
                    for (ExpenseCategoryBean category : expenseCategoryList) {
                        ContentValues values = new ContentValues();
                        values.put("id", category.id);
                        values.put("name", category.name);
                        db.replace(DBHelper.EXPENSE_CATE, null, values);
                    }
                    for (ExpenseBean expense : expenseList) {
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
     * 加载默认数据.(debug)
     */
    public void loadExample() {
        mWrapper.recreateTables();
        DBMirror.loadExample().patch(mWrapper);
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
