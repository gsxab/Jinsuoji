package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.List;

//import android.widget.CheckedTextView

/**
 * 任务列表使用的适配器，目前可用于首页和任务页，但是两页的项布局可能不一样所以可能要改
 */
public class TodoListAdaptor extends RecyclerView.Adapter<TodoListAdaptor.ViewHolder> {
    private List<Todo> todoList;
    private final ListRefreshable refreshable;

    TodoListAdaptor(Context context, ListRefreshable refreshable, int year, int month, int day) {
        super();
        todoList = new TodoDAO(context).getDaily(year, month, day);
        this.refreshable = refreshable;
    }

    TodoListAdaptor(Context context, ListRefreshable refreshable, boolean finished) {
        super();
        todoList = new TodoDAO(context).getTodoListByFinished(finished);
        this.refreshable = refreshable;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.todo_name);
            memo = view.findViewById(R.id.todo_memo);
            finished = view.findViewById(R.id.todo_finished);
        }
        TextView name;
        TextView memo;
        CheckBox finished;
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.name.setText(todo.getTaskName());
        holder.finished.setChecked(todo.isFinished());
        holder.finished.setTag(position);
        holder.finished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) buttonView.getTag();
                Todo todo = todoList.get(position);
                todo.setFinished(isChecked);
                new TodoDAO(buttonView.getContext()).editTodo(todo);
                refreshable.refreshList();
            }
        });
        holder.memo.setText(todo.getMemo());
        holder.mView.setTag(todo);
    }

    public void refresh(Context context, int year, int month, int day) {
        todoList = new TodoDAO(context).getDaily(year, month, day);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void refresh(Context context, boolean finished) {
        todoList = new TodoDAO(context).getTodoListByFinished(finished);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void change(int pos, Todo data) {
        notifyItemChanged(pos, data);
        todoList.set(pos, data);
    }

    public void remove(int pos) {
        notifyItemRemoved(pos);
        todoList.remove(pos);
    }
}
