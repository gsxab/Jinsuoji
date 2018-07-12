package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    static final int VH_EMPTY = 0;
    static final int VH_ITEM = 1;

    ZhongcaoCategoriesAdapter(Context context) {
        super();
        fetchData(context);
    }

    public List<ZhongcaoCategory> getList() {
        return categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VH_ITEM: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zhongcao_category_item, parent, false);
                return new ItemViewHolder(view);
            }
            case VH_EMPTY: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zhongcao_category_empty, parent, false);
                return new EmptyViewHolder(view);
            }
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
                public void onSuccess(Bitmap bitmap) {
                    cover.setImageBitmap(bitmap);
                }
            }, new LoadPictureTask.OnLoadFailure() {
                @Override
                public void onFailure() {
                    cover.setImageBitmap(BitmapFactory.decodeResource(
                            itemView.getResources(), R.drawable.welcome_page));
                }
            }).start();
            name.setText(category.getName());
            entrance.setTag(category);
            entrance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 加载此类图片列表
                }
            });
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
