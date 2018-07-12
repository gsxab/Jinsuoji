package org.jinsuoji.jinsuoji.zhongcao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.goyourfly.multiple.adapter.MultipleAdapter;
import com.goyourfly.multiple.adapter.MultipleSelect;

import org.jinsuoji.jinsuoji.ListRefreshable;
import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;

import java.util.List;

public class ZhongcaoPicturesActivity extends AppCompatActivity
        implements MenuBarCallback, ListRefreshable, View.OnClickListener {
    private RecyclerView pictureList;
    private MultipleAdapter adapter;
    private ZhongcaoPicturesAdapter zhongcaoPicturesAdapter;
    private MyCustomMenuBar menuBar;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhongcao_pictures);

        categoryId = getIntent().getIntExtra(ZhongcaoCategoriesFragment.ZHONGCAO_CATEGORY_ID, 0);

        pictureList = findViewById(R.id.picture_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(new ZhongcaoDAO(this).getCategoryById(categoryId).getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryFinish(v);
            }
        });

        if (adapter == null) {
            if (zhongcaoPicturesAdapter == null) {
                zhongcaoPicturesAdapter = new ZhongcaoPicturesAdapter(this, this, categoryId);
            }
            if (menuBar == null) {
                menuBar = new MyCustomMenuBar(this,
                        getResources().getColor(R.color.colorAccent), Gravity.TOP, this);
            }
            adapter = MultipleSelect.with(this)
                    .adapter(zhongcaoPicturesAdapter)
                    .ignoreViewType(new Integer[]{ZhongcaoPicturesAdapter.VH_EMPTY})
                    .customMenu(menuBar)
                    .build();
        }

        int itemCount = zhongcaoPicturesAdapter.getItemCount();
        pictureList.setLayoutManager(new GridLayoutManager(this,
                itemCount < 3 ? itemCount : 3));
        pictureList.setAdapter(adapter);
    }

    public void tryFinish(View view) {
        if (!menuBar.cancel()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        tryFinish(null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean action(int actionId, List<Integer> indices) {
        return false;
    }

    @Override
    public void refreshList() {
        zhongcaoPicturesAdapter.refresh(this);
        adapter.notifyDataSetChanged();
        int itemCount = zhongcaoPicturesAdapter.getItemCount();
        pictureList.setLayoutManager(new GridLayoutManager(this,
                itemCount < 3 ? itemCount : 3));
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, R.string.placeholder, Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, ZhongcaoDetailActivity.class);
    }

    public void onToolbarAddClicked(View view) {
        // TODO 添加新的图片进来
    }
}
