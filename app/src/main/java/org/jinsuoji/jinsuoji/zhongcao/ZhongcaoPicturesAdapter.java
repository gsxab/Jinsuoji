package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.Zhongcao;

import java.util.List;

public class ZhongcaoPicturesAdapter extends RecyclerView.Adapter<ZhongcaoPicturesAdapter.ViewHolder> {
    private List<Zhongcao> zhongcaoList;
    private View.OnClickListener onClickListener;

    static final int VH_EMPTY = 0;
    private static final int VH_ITEM = 1;
    private int categoryId;

    ZhongcaoPicturesAdapter(Context context, View.OnClickListener onClickListener, int categoryId) {
        super();
        this.categoryId = categoryId;
        this.onClickListener = onClickListener;
        fetchData(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
        case VH_ITEM:
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.zhongcao_picture_item, parent, false));
        case VH_EMPTY:
            return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.zhongcao_picture_empty, parent, false));
        default:
            throw new AssertionError();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this, position);
    }

    @Override
    public int getItemCount() {
        int size = zhongcaoList.size();
        return size > 0 ? size : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return zhongcaoList.isEmpty() ? VH_EMPTY : VH_ITEM;
    }

    public void refresh(Context context) {
        fetchData(context);
        notifyDataSetChanged();
    }

    private void fetchData(Context context) {
        zhongcaoList = new ZhongcaoDAO(context).getRecordsByCategoryId(categoryId);
    }

    abstract static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(ZhongcaoPicturesAdapter zhongcaoPicturesAdapter, int position);
    }

    static class ItemViewHolder extends ViewHolder {
        private final ImageView picture;
        private final TextView caption;

        ItemViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture);
            caption = itemView.findViewById(R.id.caption);
        }

        @Override
        public void bind(ZhongcaoPicturesAdapter adapter, int position) {
            Zhongcao zhongcao = adapter.zhongcaoList.get(position);
            picture.setImageDrawable(Drawable.createFromPath(zhongcao.getPicture()));
            String memo = zhongcao.getMemo();
            caption.setText(memo == null ? "" : memo);
            itemView.setTag(zhongcao);
            itemView.setOnClickListener(adapter.onClickListener);
        }
    }

    static class EmptyViewHolder extends ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(ZhongcaoPicturesAdapter zhongcaoPicturesAdapter, int position) {}
    }
}
