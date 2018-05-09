package org.jinsuoji.jinsuoji.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 记账类.
 * 保存一次记账条目的基本信息.
 * TODO 将时间的时分去除（这个、ExpenseDAO）
 */
public class Expense implements Serializable {
    private int id;
    private String item;
    private Date datetime;
    private int money;
    //private int categoryID;
    private String category;

    public Expense(int id, String title, Date datetime, int money, String category) {
        this.id = id;
        this.item = title;
        this.datetime = datetime;
        this.money = money;
    //    this.categoryID = categoryID;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String title) {
        this.item = title;
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

    //public int getCategoryID() {
    //    return categoryID;
    //}

    //public void setCategoryID(int categoryID) {
    //    this.categoryID = categoryID;
    //}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
