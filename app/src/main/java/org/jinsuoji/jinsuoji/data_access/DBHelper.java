package org.jinsuoji.jinsuoji.data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 直接与数据库交互的类，上层(DAL)应当使用Wrapper.
 * <p/>
 * 原理参看{@link SQLiteOpenHelper}的文档.
 * 使用方法：需要的地方应该使用{@link DBWrapper}，要传入{@link Context}。
 * <ul>
 * <li>{@link #getReadableDatabase()}获得只读数据库对象.</li>
 * <li>{@link #getWritableDatabase()}获得可读写数据库对象.</li>
 * <li>{@link #recreateTables(SQLiteDatabase)}重建数据表（用于改动数据表结构后刷新）.</li>
 * </ul>
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "jinsuoji.db";
    static final String EXPENSE_CATE = "expense_category", EXPENSE = "expense", TODO = "TODO";
    private static final String[] TABLE_NAMES = new String[]{
            EXPENSE_CATE, EXPENSE, TODO,
    };

    /**
     * @param context Context对象.
     */
    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 发生在访问数据库且数据库尚未存在时.
     * <p/>
     * 由系统调用.
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + EXPENSE_CATE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name NTEXT UNIQUE" +
                ");");
        db.execSQL("CREATE TABLE " + EXPENSE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item TEXT, " +
                "time DATE, " +
                "money INTEGER," +
                "category_id INTEGER," +
                "FOREIGN KEY (category_id) REFERENCES " + EXPENSE_CATE + "(id)" +
                ");");
        db.execSQL("CREATE TABLE " + TODO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name NTEXT, " +
                "memo NTEXT, " +
                "priority INTEGER, " +
                "time DATETIME, " +
                "finished BOOLEAN" +
                ");");
    }

    /**
     * {@inheritDoc}
     * 由系统调用.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateTables(db);
    }

    void recreateTables(SQLiteDatabase db) {
        for (String table : TABLE_NAMES) {
            String sql = "DROP TABLE IF EXISTS " + table + ";";
            db.execSQL(sql);
        }
        onCreate(db);
    }
}
