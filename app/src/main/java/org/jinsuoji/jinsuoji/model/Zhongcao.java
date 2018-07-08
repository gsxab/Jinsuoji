package org.jinsuoji.jinsuoji.model;

import android.content.Context;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;

@SuppressWarnings("unused")
public class Zhongcao implements ContextualStringConvertible {
    public Zhongcao() {}

    private int id;
    private String picture;
    private String memo;
    private int categoryId;

    public Zhongcao(int id, String picture, String memo, int categoryId) {
        this.id = id;
        this.picture = picture;
        this.memo = memo;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toContextualString(Context context) {
        ZhongcaoCategory category = new ZhongcaoDAO(context).getCategoryById(id);
        if (category == null) throw new AssertionError();
        return context.getString(R.string.zhongcao_format,
                memo.isEmpty() ? context.getString(R.string.zhongcao_no_memo) : memo,
                category.getName());
    }
}
