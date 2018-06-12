package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Todo;

public class TodoTabContentFragment extends Fragment
        implements ItemTouchListener.RecyclerViewOperator<Todo>, ListRefreshable {
    private static final String KEY_REQUEST_CODE = "request_code";
    private static final String KEY_FINISHED = "finished";
    private int requestCode;
    private RecyclerView listView;
    private boolean finished;
    private ListRefreshable refreshable;

    public TodoTabContentFragment() {

    }

    public static TodoTabContentFragment getInstance(int requestCode, boolean finished) {
        TodoTabContentFragment fragment = new TodoTabContentFragment();
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
        if (savedInstanceState != null) {
            finished = savedInstanceState.getBoolean(KEY_FINISHED);
            requestCode = savedInstanceState.getInt(KEY_REQUEST_CODE);
        }

        listView = view.findViewById(R.id.todo_list);
        listView.addOnItemTouchListener(new ItemTouchListener<>(this, listView, true));
        Fragment parentFragment = getParentFragment();
        if (!(parentFragment instanceof TodoListFragment)) { throw new AssertionError(); }
        refreshable = (ListRefreshable) parentFragment;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == this.requestCode) {
            Todo todo = (Todo) data.getSerializableExtra(TodoEditActivity.LAST_TODO);
            new TodoDAO(getContext()).editTodo(todo);
            refreshable.refreshList();
        }
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_REQUEST_CODE, requestCode);
        outState.putBoolean(KEY_FINISHED, finished);
    }
}
