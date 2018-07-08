package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.net.Uri;
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

    ZhongcaoCategoriesAdapter(Context context) {
        super();
        categoryList = new ZhongcaoDAO(context).getAllCategories();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zhongcao_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZhongcaoCategory category = categoryList.get(position);
        holder.cover.setImageURI(Uri.parse(category.getCover()));
        holder.name.setText(category.getName());
        holder.entrance.setTag(category);
        holder.entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 加载此类图片列表
            }
        });
        holder.mView.setTag(category);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView cover;
        private TextView name;
        private ImageButton entrance;

        ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.cover = view.findViewById(R.id.cover);
            this.name = view.findViewById(R.id.name);
            this.entrance = view.findViewById(R.id.entrance);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
