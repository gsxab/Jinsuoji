package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.model.EntryNode;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link EntryNode}.
 */
public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder> {
    private List<EntryNode> nodes;

    ListRefreshable toRefresh;

    private void pChangeDate(Context context, int year, int month, int date, boolean byDate) {
        if (date == 0) {
            if (byDate) {
                nodes = new ExpenseDAO(context).getMonthlyByDate(year, month);
            } else {
                nodes = new ExpenseDAO(context).getMonthlyByCategory(year, month);
            }
        } else {
            nodes = new ExpenseDAO(context).getDaily(year, month, date);
        }
    }

    /**
     * 给定的年月，按时间或分类排列(date=0)，或者，给定的年月日，按时间排列.
     *
     * @param year 指定的年份
     * @param month 指定的月份
     * @param date 指定的日期
     * @param byDate 按时间(true)、按分类(false)
     */
    public ExpenseListAdapter(Context context, int year, int month, int date, boolean byDate) {
        pChangeDate(context, year, month, date, byDate);
    }

    public void setNewDate(Context context, int year, int month, int date, boolean byDate) {
        pChangeDate(context, year, month, date, byDate);
        notifyDataSetChanged();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (EntryNode.ItemType.fromIndex(viewType)) {
            case EXPENSE_ITEM: {
                return new ExpenseItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expenditure_item, parent, false));
            }
            case CATEGORY: {
                return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expenditure_category, parent, false));
            }
            default:
                return new ExpenseItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expenditure_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        EntryNode node = nodes.get(position);
        holder.mView.setTag(node);
        node.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return nodes.get(position).getType().ordinal();
    }


    private int getPos(EntryNode.ExpenseItem data) {
        for (int i = 0; i < nodes.size(); i++) {
            EntryNode node = nodes.get(i);
            if (node.getType() == EntryNode.ItemType.EXPENSE_ITEM &&
                    ((EntryNode.ExpenseItem) node).getExpense().getId() ==
                            data.getExpense().getId()) {
                return i;
            }
        }
        return -1;
    }

    public void change(int pos, EntryNode data) {
        if (pos == -1) {
            pos = getPos((EntryNode.ExpenseItem) data);
            if (pos == -1) return;
        }
        notifyItemChanged(pos, data);
        nodes.set(pos, data);
        if (toRefresh != null) {
            toRefresh.refreshList();
        }
    }

    public void remove(int pos, EntryNode data) {
        if (pos == -1) {
            pos = getPos(((EntryNode.ExpenseItem) data));
            if (pos == -1) return;
        }
        if (pos != 0 && nodes.get(pos - 1).getType() == EntryNode.ItemType.CATEGORY &&
                (pos + 1 == nodes.size() ||
                        nodes.get(pos + 1).getType() == EntryNode.ItemType.CATEGORY)) {
            notifyItemRangeRemoved(pos - 1, 2);
            nodes.remove(pos - 1);
            nodes.remove(pos - 1);
        } else {
            notifyItemRemoved(pos);
            nodes.remove(pos);
        }
        if (toRefresh != null) {
            toRefresh.refreshList();
        }
    }

    /**
     * 抽象ViewHolder，hold一个项目或分类的View.
     */
    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mView.getTag() + "'";
        }
    }

    public class ExpenseItemViewHolder extends ViewHolder {
        public final TextView item;
        public final TextView category;
        public final TextView money;

        public ExpenseItemViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item_name);
            category = view.findViewById(R.id.item_category);
            money = view.findViewById(R.id.item_money);
        }
    }

    public class CategoryViewHolder extends ViewHolder {
        public final TextView categoryName;

        public CategoryViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.category_name);
        }
    }
}
