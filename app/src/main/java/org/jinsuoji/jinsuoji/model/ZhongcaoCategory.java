package org.jinsuoji.jinsuoji.model;

import android.content.Context;

import org.jinsuoji.jinsuoji.R;

@SuppressWarnings("unused")
public class ZhongcaoCategory implements ContextualStringConvertible {
    public ZhongcaoCategory() {}

    private int id;
    private String cover;
    private String name;

    public ZhongcaoCategory(int id, String cover, String name) {
        this.id = id;
        this.cover = cover;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toContextualString(Context context) {
        return context.getString(R.string.zhongcao_category_format,
                name.isEmpty() ? context.getString(R.string.zhongcao_no_memo) : name);
    }
}
