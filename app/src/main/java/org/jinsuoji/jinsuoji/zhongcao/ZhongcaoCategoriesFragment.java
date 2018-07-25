package org.jinsuoji.jinsuoji.zhongcao;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.goyourfly.multiple.adapter.MultipleSelect;
import com.goyourfly.multiple.adapter.viewholder.color.ColorFactory;

import org.jinsuoji.jinsuoji.ListRefreshable;
import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.ZhongcaoCategory;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZhongcaoCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZhongcaoCategoriesFragment extends Fragment
        implements ListRefreshable, View.OnKeyListener, MenuBarCallback, View.OnClickListener {
    private RecyclerView zhongcaoCategories;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private ZhongcaoCategoriesAdapter zhongcaoCategoriesAdapter;
    private MyCustomMenuBar menuBar;

    public static final String ZHONGCAO_CATEGORY = "org.jinsuoji.jinsuoji.ZhongcaoCategory";

    public ZhongcaoCategoriesFragment() {
        // Required empty public constructor
    }

    public static ZhongcaoCategoriesFragment newInstance() {
        return new ZhongcaoCategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zhongcao_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapter == null) {
            if (zhongcaoCategories == null) {
                zhongcaoCategories = view.findViewById(R.id.zhongcao_categories);
                zhongcaoCategories.setLayoutManager(new LinearLayoutManager(view.getContext()));
            }
            if (menuBar == null) {
                menuBar = new MyCustomMenuBar(getActivity(),
                        getResources().getColor(R.color.colorAccent), Gravity.BOTTOM, this);
            }
            zhongcaoCategoriesAdapter = new ZhongcaoCategoriesAdapter(view.getContext(), this);
            adapter = MultipleSelect.with(getActivity())
                    .adapter(zhongcaoCategoriesAdapter)
                    .decorateFactory(new ColorFactory())
                    .customMenu(menuBar)
                    .ignoreViewType(new Integer[]{ZhongcaoCategoriesAdapter.VH_EMPTY})
                    .build();
            zhongcaoCategories.setAdapter(adapter);
        }
    }

    @Override
    public void refreshList() {
        menuBar.cancel();
        zhongcaoCategoriesAdapter.refresh(getContext());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(this);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK &&
                menuBar.cancel();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean action(int actionId, final List<Integer> indices) {
        switch (actionId) {
            case R.id.action_delete: {
                final ZhongcaoDAO zhongcaoDAO = new ZhongcaoDAO(getContext());
                final List<ZhongcaoCategory> list = zhongcaoCategoriesAdapter.getList();
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.delete_warning)
                        .setMessage(getResources().getString(R.string.multi_delete_warning_message, indices.size()))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int index : indices) {
                                    zhongcaoDAO.deleteCategory(list.get(index).getId());
                                }
                                zhongcaoCategoriesAdapter.refresh(getContext());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 取消时无操作
                            }
                        })
                        .show();
                return true;
            }
            case R.id.action_rename: {
                if (indices.size() == 1) {
                    final ZhongcaoCategory category = zhongcaoCategoriesAdapter.getList().get(indices.get(0));
                    final EditText editText = new EditText(getContext());
                    editText.setText(category.getName());
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.rename)
                            .setView(editText)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (editText.getText().length() == 0) {
                                        Toast.makeText(getContext(), R.string.err_empty_name, Toast.LENGTH_LONG).show();
                                    } else if (new ZhongcaoDAO(getContext()).renameCategory(category.getId(), editText.getText().toString())) {
                                        zhongcaoCategoriesAdapter.refresh(getContext());
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 取消时无操作
                                }
                            })
                            .show();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ZhongcaoPicturesActivity.class);
        intent.putExtra(ZHONGCAO_CATEGORY, ((ZhongcaoCategory) v.getTag()));
        startActivity(intent);
    }
}
