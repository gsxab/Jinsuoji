package org.jinsuoji.jinsuoji.model;

import android.content.Context;
import android.support.annotation.NonNull;

import org.jinsuoji.jinsuoji.ExpenseListAdapter;
import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.DateUtils;

import java.util.Locale;

/**
 * RecyclerView中的项，是项结点，即可以是内容项或分类项.
 *
 * {@link ItemType}是区分项种类的.不同的项目使用不同的绑定ViewHolder的bind.
 */
public abstract class EntryNode implements ContextualStringConvertible {
    public enum ItemType {
        EXPENSE_ITEM,
        CATEGORY,
        ;
        public static ItemType fromIndex(int index) {
            return values()[index];
        }
    }

    public abstract ItemType getType();
    public abstract void bind(@NonNull ExpenseListAdapter.ViewHolder holder, int position);
    public abstract String toContextualString(Context context);

    /**
     * 分类项.显示为分类标签.
     */
    public static class ExpenseCategory extends EntryNode {
        private String categoryName;

        public ExpenseCategory(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public ItemType getType() {
            return ItemType.CATEGORY;
        }

        @Override
        public void bind(@NonNull ExpenseListAdapter.ViewHolder holder, int position) {
            if (holder instanceof ExpenseListAdapter.CategoryViewHolder) {
                ExpenseListAdapter.CategoryViewHolder categoryViewHolder =
                        ((ExpenseListAdapter.CategoryViewHolder) holder);
                categoryViewHolder.categoryName.setText(this.categoryName);
            }
        }

        @Override
        public String toContextualString(Context context) {
            return context.getString(R.string.category_format, categoryName);
        }
    }

    /**
     * 内容项.显示为一项内容.
     */
    public static class ExpenseItem extends EntryNode {
        private Expense expense;

        public ExpenseItem(Expense expense) {
            this.expense = expense;
        }

        @Override
        public ItemType getType() {
            return ItemType.EXPENSE_ITEM;
        }

        @Override
        public void bind(@NonNull ExpenseListAdapter.ViewHolder holder, int position) {
            if (holder instanceof ExpenseListAdapter.ExpenseItemViewHolder) {
                ExpenseListAdapter.ExpenseItemViewHolder expenseItemViewHolder =
                        ((ExpenseListAdapter.ExpenseItemViewHolder) holder);
                expenseItemViewHolder.item.setText(this.expense.getItem());
                expenseItemViewHolder.category.setText(this.expense.getCategory());
                expenseItemViewHolder.money.setText(String.format(Locale.getDefault()/*Locale.US*/,
                        "%1$.2f", this.expense.getMoney() / 100d));
            }
        }

        @Override
        public String toContextualString(Context context) {
            return context.getString(R.string.expense_format, expense.getItem(),
                    DateUtils.toDateString(expense.getDatetime()), expense.getMoney() / 100f);
        }
    }
}
