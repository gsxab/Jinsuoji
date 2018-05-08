package org.jinsuoji.jinsuoji.model;

import android.support.annotation.NonNull;

import org.jinsuoji.jinsuoji.ExpenseListAdapter;

import java.util.Locale;

public abstract class EntryNode {
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
            {
                if (holder instanceof ExpenseListAdapter.CategoryViewHolder) {
                    ExpenseListAdapter.CategoryViewHolder categoryViewHolder = ((ExpenseListAdapter.CategoryViewHolder) holder);
                    categoryViewHolder.categoryName.setText(this.categoryName);
                }
            }
        }
    }

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
            {
                if (holder instanceof ExpenseListAdapter.ExpenseItemViewHolder) {
                    ExpenseListAdapter.ExpenseItemViewHolder expenseItemViewHolder = ((ExpenseListAdapter.ExpenseItemViewHolder) holder);
                    expenseItemViewHolder.item.setText(this.expense.getItem());
                    expenseItemViewHolder.category.setText(this.expense.getCategory());
                    expenseItemViewHolder.money.setText(String.format(Locale.US, "%1$.2f",
                            this.expense.getMoney() / 100d));
                }
            }
        }
    }
}
