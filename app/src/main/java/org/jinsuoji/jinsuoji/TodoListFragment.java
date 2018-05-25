package org.jinsuoji.jinsuoji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Todo;

/**
 * 任务页的{@link Fragment}.
 */
public class TodoListFragment extends Fragment implements
        CompoundButton.OnCheckedChangeListener {
    private static final int EDIT_TODO_UNFINISHED = 4;
    private static final int EDIT_TODO_FINISHED = 6;
    private OnFragmentInteractionListener listener = null;
    private RecyclerView finishedListView, unfinishedListView;
    private TextView finishedListTitle;
    private RecyclerView.ItemDecoration decoration = new SpaceItemDecoration(16);

    public TodoListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // params
        }
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Preference.setShowFinished(buttonView.getContext(), isChecked);
        if (isChecked) {
            onShowFinished();
        } else {
            onHideFinished();
        }
    }

    private void onShowFinished() {
        finishedListView.setVisibility(View.VISIBLE);
        finishedListTitle.setVisibility(View.VISIBLE);
        finishedListView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishedListView.setAdapter(new TodoListAdaptor(getContext(), true));
        finishedListView.addItemDecoration(decoration);
    }

    private void onHideFinished() {
        finishedListView.removeItemDecoration(decoration);
        finishedListView.setVisibility(View.GONE);
        finishedListTitle.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        unfinishedListView = view.findViewById(R.id.unfinished_list);
        unfinishedListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        unfinishedListView.setAdapter(new TodoListAdaptor(getContext(), false));
        unfinishedListView.addOnItemTouchListener(new ItemTouchListener<>(
                new ItemTouchListener.RecyclerViewOperator<Todo>() {
                    @Override
                    public Context getContext() {
                        return TodoListFragment.this.getContext();
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
                        startActivityForResult(intent, EDIT_TODO_UNFINISHED);
                    }

                    @Override
                    public void performRemove(View view, int pos, Todo data) {
                        new TodoDAO(getContext()).delTodo(data.getId());
                        ((TodoListAdaptor) unfinishedListView.getAdapter()).remove(pos);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    refreshList();
                                } catch (NullPointerException | ClassCastException ignored) {}
                            }
                        }, 1000);
                    }
        }, unfinishedListView));
        unfinishedListView.addItemDecoration(decoration);

        finishedListView = view.findViewById(R.id.finished_list);
        finishedListTitle = view.findViewById(R.id.finished_list_title);
        finishedListView.addOnItemTouchListener(new ItemTouchListener<>(
                new ItemTouchListener.RecyclerViewOperator<Todo>() {
                    @Override
                    public Context getContext() {
                        return TodoListFragment.this.getContext();
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
                        startActivityForResult(intent, EDIT_TODO_FINISHED);
                    }

                    @Override
                    public void performRemove(View view, int pos, Todo data) {
                        new TodoDAO(getContext()).delTodo(data.getId());
                        ((TodoListAdaptor) finishedListView.getAdapter()).remove(pos);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    refreshList();
                                } catch (NullPointerException | ClassCastException ignored) {}
                            }
                        }, 1000);
                    }
                }, finishedListView));

        if (Preference.getShowFinished(view.getContext())) {
            onShowFinished();
        } else {
            onHideFinished();
        }

        Switch aSwitch = view.findViewById(R.id.show_finished_switch);
        aSwitch.setChecked(Preference.getShowFinished(view.getContext()));
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static TodoListFragment newInstance() {
        return new TodoListFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK ||
                (requestCode != EDIT_TODO_FINISHED && requestCode != EDIT_TODO_UNFINISHED)) return;
        int index = data.getIntExtra(ExpenseEditActivity.INDEX, -1);
        Todo todo = (Todo) data.getSerializableExtra(TodoEditActivity.LAST_TODO);
        new TodoDAO(getContext()).editTodo(todo);
        RecyclerView recyclerView = requestCode == EDIT_TODO_FINISHED ? finishedListView : unfinishedListView;
        ((TodoListAdaptor) recyclerView.getAdapter()).change(index, todo);
    }

    public void refreshList() {
        unfinishedListView.getAdapter().notifyDataSetChanged();
        if (Preference.getShowFinished(getContext())) {
            finishedListView.getAdapter().notifyDataSetChanged();
        }
    }
}
