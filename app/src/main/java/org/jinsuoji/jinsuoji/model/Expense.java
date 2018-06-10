package org.jinsuoji.jinsuoji.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 记账类.
 * 保存一次记账条目的基本信息.
 * <p/>
 * 注意：各条目取值。
 * <table>
 *     <tr><th>字段名</th><th>类型</th><th>有效取值</th><th>代表未填写的取值</th><th>被存为未填写的取值</th></tr>
 *     <tr><td>id</td><td>int</td><td>正整数</td><td>-1</td><td>不存在</td></tr>
 *     <tr><td>item</td><td>String</td><td>非空字符串</td><td>null</td><td>""</td></tr>
 *     <tr><td>datetime</td><td>Date</td><td>有效时间</td><td>不存在</td><td>不存在</td></tr>
 *     <tr><td>money</td><td>int*100</td><td>整数</td><td>0</td><td>0</td></tr>
 *     <tr><td>category</td><td>String</td><td>非空字符串</td><td>null</td><td>""</td></tr>
 * </table>
 */
public class Expense implements Serializable {
    public Expense() {
    }

    private int id;
    private String item;
    private Date datetime;
    private int money;
    private String category;

    public Expense(int id, String item, Date datetime, int money, String category) {
        this.id = id;
        this.item = item;
        this.datetime = datetime;
        this.money = money;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
