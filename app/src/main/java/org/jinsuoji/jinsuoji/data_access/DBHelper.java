package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jinsuoji.jinsuoji.R;

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
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "jinsuoji.db";

    private static final int FIRST_STABLE_VERSION = 3;
    private static final int ZHONGCAO_FIRST_VERSION = 4;
    static final String EXPENSE_CATE = "expense_category", EXPENSE = "expense", TODO = "TODO",
            ZHONGCAO = "zhongcao", ZHONGCAO_CATE = "zhongcao_category";
    private final Context context;
    private static final String[] TABLE_NAMES = new String[]{
            EXPENSE_CATE, EXPENSE, TODO, ZHONGCAO_CATE, ZHONGCAO
    };
    private static final String[] TABLE_NAMES_BEFORE_4 = new String[]{
            EXPENSE_CATE, EXPENSE, TODO,
    };

    /**
     * @param context Context对象.
     */
    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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
                "time DATETIME, " +
                "reminderTime DATETIME, " +
                "finished BOOLEAN" +
                ");");
        createZhongcaoTables(db);
    }

    private void createZhongcaoTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ZHONGCAO_CATE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cover NTEXT, " +
                "name NTEXT" +
                ");");
        db.execSQL("CREATE TABLE " + ZHONGCAO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "picture NTEXT, " +
                "memo NTEXT, " +
                "category_id INTEGER REFERENCES " + ZHONGCAO_CATE + "(id)" +
                ");");
    }

    /**
     * {@inheritDoc}
     * 由系统调用.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < FIRST_STABLE_VERSION) { // 史前版本，不向下兼容这些
            recreateTables(db, TABLE_NAMES_BEFORE_4);
        } else if (oldVersion < ZHONGCAO_FIRST_VERSION) { // 无种草模块的版本
            createZhongcaoTables(db);
        }
    }

    void recreateTables(SQLiteDatabase db) {
        recreateTables(db, TABLE_NAMES);
    }

    private void recreateTables(SQLiteDatabase db, String[] table_names) {
        for (String table : table_names) {
            String sql = "DROP TABLE IF EXISTS " + table + ";";
            db.execSQL(sql);
        }
        onCreate(db);
        ContentValues values = new ContentValues();
        values.put("id", 0);
        values.put("name", context.getString(R.string.uncategorized));
        db.insert(EXPENSE_CATE, null, values);
    }
}
