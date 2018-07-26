package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.ZhongcaoCategory;

import java.util.List;

public class ZhongcaoCategoriesAdapter extends RecyclerView.Adapter<ZhongcaoCategoriesAdapter.ViewHolder> {
    private List<ZhongcaoCategory> categoryList;
    private View.OnClickListener onClickListener;

    static final int VH_EMPTY = 0;
    static final int VH_ITEM = 1;

    ZhongcaoCategoriesAdapter(Context context, View.OnClickListener onClickListener) {
        super();
        fetchData(context);
        this.onClickListener = onClickListener;
    }

    public List<ZhongcaoCategory> getList() {
        return categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
        case VH_ITEM:
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.zhongcao_category_item, parent, false));
        case VH_EMPTY:
            return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.zhongcao_category_empty, parent, false));
        default:
            throw new AssertionError();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return categoryList.isEmpty() ? VH_EMPTY : VH_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.isEmpty() ? 1 : categoryList.size();
    }

    public void refresh(Context context) {
        fetchData(context);
        notifyDataSetChanged();
    }

    private void fetchData(Context context) {
        categoryList = new ZhongcaoDAO(context).getAllCategories();
    }

    abstract static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }

        abstract int getViewType();

        abstract void bind(@NonNull ZhongcaoCategoriesAdapter adapter, int position);
    }

    static class ItemViewHolder extends ViewHolder {
        private final ImageView cover;
        private final TextView name;
        private final ImageButton entrance;

        ItemViewHolder(View view) {
            super(view);
            this.cover = view.findViewById(R.id.cover);
            this.name = view.findViewById(R.id.name);
            this.entrance = view.findViewById(R.id.entrance);
        }

        @Override
        int getViewType() {
            return VH_ITEM;
        }

        @Override
        void bind(@NonNull ZhongcaoCategoriesAdapter adapter, int position) {
            ZhongcaoCategory category = adapter.categoryList.get(position);
            new LoadPictureTask(category.getCover(), new LoadPictureTask.OnLoadSuccess() {
                @Override
                public void onSuccess(Drawable drawable) {
                    cover.setImageDrawable(drawable);
                }
            }, new LoadPictureTask.OnLoadFailure() {
                @Override
                public void onFailure() {
                    cover.setImageDrawable(itemView.getResources().getDrawable(R.drawable.welcome_page));
                }
            }).start();
            name.setText(category.getName());
            entrance.setTag(category);
            entrance.setOnClickListener(adapter.onClickListener);
            itemView.setTag(category);
        }
    }

    static class EmptyViewHolder extends ViewHolder {
        EmptyViewHolder(View view) {
            super(view);
        }

        @Override
        int getViewType() {
            return VH_EMPTY;
        }

        @Override
        void bind(@NonNull ZhongcaoCategoriesAdapter adapter, int position) {
        }
    }
}
