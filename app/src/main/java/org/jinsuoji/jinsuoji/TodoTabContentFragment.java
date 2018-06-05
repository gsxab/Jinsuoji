package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Todo;

public class TodoTabContentFragment extends Fragment
        implements ItemTouchListener.RecyclerViewOperator<Todo>, ListRefreshable {
    private int requestCode;
    private RecyclerView listView;
    private boolean finished;
    private ListRefreshable refreshable;

    public TodoTabContentFragment() {

    }

    public static TodoTabContentFragment getInstance(ListRefreshable refreshable, int requestCode, boolean finished) {
        TodoTabContentFragment fragment = new TodoTabContentFragment();
        fragment.refreshable = refreshable;
        fragment.requestCode = requestCode;
        fragment.finished = finished;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_tab_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.todo_list);
        listView.addOnItemTouchListener(new ItemTouchListener<>(this, listView, true));
        listView.setAdapter(new TodoListAdaptor(getContext(), refreshable, finished));

        refreshList();
    }

    @Override
    public boolean isTouchable(Todo data) {
        return true;
    }

    @Override
    public void performEdit(View view, int pos, Todo data) {
        Intent intent = new Intent(getActivity(), TodoEditActivity.class);
        intent.putExtra(TodoEditActivity.LAST_TODO, data);
        intent.putExtra(TodoEditActivity.INDEX, pos);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void performRemove(View view, int pos, Todo data) {
        new TodoDAO(getContext()).delTodo(data.getId());
        ((TodoListAdaptor) listView.getAdapter()).remove(pos);
    }

    @Override
    public void refreshList() {
        if (listView != null) {
            ((TodoListAdaptor) listView.getAdapter()).refresh(getContext(), finished);
        }
    }
}
