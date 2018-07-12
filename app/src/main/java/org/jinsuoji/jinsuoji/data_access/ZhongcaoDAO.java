package org.jinsuoji.jinsuoji.data_access;

import android.content.ContentValues;
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

    private static class RecordQueryOperation implements QueryOperation<Zhongcao> {
        @Override
        public Zhongcao operate(Cursor cursor) {
            if (!cursor.moveToFirst()) return null;
            return new Zhongcao(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3));
        }
    }

    private static class ListQueryOperation extends QueryAdapter<List<Zhongcao>> {
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

    private static class CategoryQueryOperation implements QueryOperation<ZhongcaoCategory> {
        @Override
        public ZhongcaoCategory operate(Cursor cursor) {
            if (!cursor.moveToFirst()) return null;
            return new ZhongcaoCategory(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
        }
    }

    private static class CategoryListQueryOperation extends QueryAdapter<List<ZhongcaoCategory>> {
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
                        new ListQueryOperation());
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
                        new ListQueryOperation());
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
                        new RecordQueryOperation());
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

    private ZhongcaoCategory getOrCreateCategory(String name, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict(DBHelper.ZHONGCAO_CATE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        return query(db,
                "SELECT id, cover, name " +
                        "FROM " + DBHelper.ZHONGCAO_CATE + " " +
                        "WHERE name = ?",
                new String[]{name},
                new CategoryQueryOperation());
    }

    public ZhongcaoCategory getOrCreateCategory(final String name) {
        return wrapper.write(new Operation<ZhongcaoCategory>() {
            @Override
            public ZhongcaoCategory operate(SQLiteDatabase db) {
                return getOrCreateCategory(name, db);
            }
        });
    }

    public List<ZhongcaoCategory> getAllCategories() {
        return wrapper.read(new Operation<List<ZhongcaoCategory>>() {
            @Override
            public List<ZhongcaoCategory> operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, cover, name " +
                                "FROM " + DBHelper.ZHONGCAO_CATE,
                        null,
                        new CategoryListQueryOperation());
            }
        });
    }

    public ZhongcaoCategory getCategoryById(final int id) {
        return wrapper.read(new Operation<ZhongcaoCategory>() {
            @Override
            public ZhongcaoCategory operate(SQLiteDatabase db) {
                return query(db,
                        "SELECT id, cover, name " +
                                "FROM" + DBHelper.ZHONGCAO_CATE + " " +
                                "WHERE id = ?",
                        new String[]{String.valueOf(id)},
                        new CategoryQueryOperation());
            }
        });
    }

    public boolean renameCategory(final int id, final String newName) {
        return wrapper.write(new Operation<Boolean>() {
            @Override
            public Boolean operate(SQLiteDatabase db) {
                if (query(db,
                        "SELECT count(*) " +
                                "FROM " + DBHelper.ZHONGCAO_CATE + " " +
                                "WHERE name = ?",
                        new String[]{newName},
                        new QueryOperation<Integer>() {
                            @Override
                            public Integer operate(Cursor cursor) {
                                cursor.moveToFirst();
                                return cursor.getInt(0);
                            }
                        }) > 0) return false;
                ContentValues values = new ContentValues();
                values.put("name", newName);
                return db.update(DBHelper.ZHONGCAO_CATE, values, "id = ?",
                        new String[]{String.valueOf(id)}) > 0;
            }
        });
    }

    public boolean setCategoryCover(final int id, final String cover) {
        return wrapper.write(new Operation<Boolean>() {
            @Override
            public Boolean operate(SQLiteDatabase db) {
                ContentValues values = new ContentValues();
                values.put("cover", cover);
                return db.update(DBHelper.ZHONGCAO_CATE, values, "id = ?",
                        new String[]{String.valueOf(id)}) > 0;
            }
        });
    }

    public void deleteCategory(final int id) {
        wrapper.write(new Operation<Void>() {
            @Override
            public Void operate(SQLiteDatabase db) {
                db.delete(DBHelper.ZHONGCAO_CATE, "id = ?", new String[]{String.valueOf(id)});
                return null;
            }
        });
    }
}
