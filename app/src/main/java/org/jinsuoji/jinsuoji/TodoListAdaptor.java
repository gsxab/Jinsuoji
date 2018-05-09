package org.jinsuoji.jinsuoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.ArrayList;
import java.util.List;

//import android.widget.CheckedTextView

/**
 * 任务列表使用的适配器，目前可用于首页和任务页，但是两页的项布局可能不一样所以可能要改
 */
public class TodoListAdaptor extends RecyclerView.Adapter<TodoListAdaptor.ViewHolder> {
    private List<Todo> todoList = new ArrayList<>();

    public TodoListAdaptor(Context context, int year, int month, int day, @Nullable Boolean finished) {
        super();

        // todoList = getTodoListByDate(date, finished)
        ArrayList<Todo> list = new ArrayList<>();
        list.add(new Todo(-1, DateUtils.makeDate(2018, 5, 8), "跑步", 1, "2km哦", true));
        list.add(new Todo(-1, DateUtils.makeDate(2018, 5, 7), "编译原理", 1, "期末大作业", false));
        list.add(new Todo(-1, DateUtils.makeDate(2018, 5, 6), "移动平台", 1, "", false));
        list.add(new Todo(-1, DateUtils.makeDate(2018, 5, 6), "面向对象", 1, "考试", false));
        for (int i = 0; i < list.size(); i++) {
            Todo todo = list.get(i);
            if (finished == null || finished == todo.isFinished()) {
                todoList.add(todo);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
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
        holder.finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO("Update data.")
            }
        });
        holder.memo.setText(todo.getMemo());
    }
}
