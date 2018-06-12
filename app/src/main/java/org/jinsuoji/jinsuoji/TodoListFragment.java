package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * 任务页的{@link Fragment}.
 */
public class TodoListFragment extends Fragment implements ListRefreshable {
    private static final int EDIT_TODO_UNFINISHED = 4;
    private static final int EDIT_TODO_FINISHED = 6;
    private OnFragmentInteractionListener listener = null;
    private List<Fragment> fragments;
    private PagerAdapter adapter;

    public TodoListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = ((OnFragmentInteractionListener) context);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        if (adapter == null) {
            if (fragments == null) {
                fragments = Arrays.asList(
                        (Fragment) TodoTabContentFragment.getInstance(EDIT_TODO_UNFINISHED, false),
                        TodoTabContentFragment.getInstance(EDIT_TODO_FINISHED, true)
                );
            }
            adapter = new PagerAdapter(getChildFragmentManager(), Arrays.asList("未完成", "已完成"), fragments);
        }
        TabLayout tabLayout = view.findViewById(R.id.todo_tab);
        ViewPager pager = view.findViewById(R.id.todo_viewpager);
        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(adapter);

        refreshList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static TodoListFragment newInstance() {
        return new TodoListFragment();
    }

    public void refreshList() {
        if (fragments != null) {
            ((TodoTabContentFragment) fragments.get(0)).refreshList();
            ((TodoTabContentFragment) fragments.get(1)).refreshList();
        }
    }
}
