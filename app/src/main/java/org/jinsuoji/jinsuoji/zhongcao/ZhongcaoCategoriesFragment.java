package org.jinsuoji.jinsuoji.zhongcao;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goyourfly.multiple.adapter.MultipleSelect;
import com.goyourfly.multiple.adapter.menu.SimpleDeleteSelectAllMenuBar;
import com.goyourfly.multiple.adapter.viewholder.color.ColorFactory;

import org.jinsuoji.jinsuoji.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZhongcaoCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZhongcaoCategoriesFragment extends Fragment {
    private RecyclerView zhongcaoCategories;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

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
            adapter = MultipleSelect.with(getActivity())
                    .adapter(new ZhongcaoCategoriesAdapter(view.getContext()))
                    .decorateFactory(new ColorFactory())
                    .customMenu(new SimpleDeleteSelectAllMenuBar(getActivity(),
                            getResources().getColor(R.color.colorAccent), Gravity.BOTTOM))
                    .build();
            zhongcaoCategories.setAdapter(adapter);
        }
    }
}
