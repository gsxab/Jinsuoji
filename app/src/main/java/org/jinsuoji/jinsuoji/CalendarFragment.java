package org.jinsuoji.jinsuoji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;

import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.EntryNode;
import org.jinsuoji.jinsuoji.model.Expense;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.Calendar;

/**
 * 日历页{@link Fragment}.
 * 日历+当日列表.
 */
public class CalendarFragment extends Fragment implements ListRefreshable {
    private static final String TAG = "jsj.CalendarFragment";
    private static final int EDIT_EXPENSE = 4;
    private static final int EDIT_TODO = 5;

    /**
     * 实例化方法.
     * @return 自己
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    public void refreshList() {
        if (dailyTodoList != null)
            dailyTodoList.getAdapter().notifyDataSetChanged();
        if (dailyExpenseList != null)
            dailyExpenseList.getAdapter().notifyDataSetChanged();
    }

    interface OnFragmentInteractionListener {}

    private Calendar current = Calendar.getInstance();
    private OnFragmentInteractionListener listener = null;
    private static final String ARG_CUR_DATE = "cal_SelectedDate";

    private CalendarView calendar;
    private ImageButton calendarCollapse;
    private RecyclerView dailyTodoList;
    private RecyclerView dailyExpenseList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            current.setTimeInMillis(savedInstanceState.getLong(ARG_CUR_DATE));
        }
    }

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: view==null");
        calendar = view.findViewById(R.id.calendar);
        calendar.setDate(current.getTimeInMillis());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.clear();
                tempCalendar.set(year, month, dayOfMonth);
                current = tempCalendar;
                ((TodoListAdaptor) dailyTodoList.getAdapter()).setNewDate(getActivity(),
                        year, month + 1, dayOfMonth);
                ((ExpenseListAdapter) dailyExpenseList.getAdapter()).setNewDate(getActivity(),
                        year, month + 1, dayOfMonth, true);
            }
        });
        calendarCollapse = view.findViewById(R.id.calendar_collapse);
        calendarCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (calendar.getVisibility()) {
                    case View.VISIBLE:
                        calendar.setVisibility(View.GONE);
                        // TODO it.animate() 动画可能还要看要不要换日历
                        break;
                    case View.INVISIBLE:
                    case View.GONE:
                        calendar.setVisibility(View.VISIBLE);
                        // it.animate()
                        break;
                    default:
                        break;
                }
                //calendarCollapse.setAnimation();
                //calendarCollapse.animate();
            }
        });

        dailyTodoList = view.findViewById(R.id.daily_todo_list);
        dailyTodoList.setLayoutManager(new LinearLayoutManager(getContext()));
        dailyTodoList.setAdapter(new TodoListAdaptor(getContext(), current.get(Calendar.YEAR),
                current.get(Calendar.MONTH) + 1, current.get(Calendar.DATE)));
        dailyTodoList.addItemDecoration(new SpaceItemDecoration(16));
        //dailyTodoList.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        dailyTodoList.addOnItemTouchListener(new ItemTouchListener<>(
                new ItemTouchListener.RecyclerViewOperator<Todo>() {
                    @Override
                    public Context getContext() {
                        return CalendarFragment.this.getContext();
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
                        startActivityForResult(intent, EDIT_TODO);
                        ((TodoListAdaptor) dailyTodoList.getAdapter()).change(pos, data);
                    }

                    @Override
                    public void performRemove(View view, int pos, Todo data) {
                        new TodoDAO(getContext()).delTodo(data.getId());
                        ((TodoListAdaptor) dailyTodoList.getAdapter()).remove(pos);
                    }
                }, dailyTodoList, true));

        dailyExpenseList = view.findViewById(R.id.daily_expense_list);
        dailyExpenseList.setLayoutManager(new LinearLayoutManager(getContext()));
        ExpenseListAdapter adapter = new ExpenseListAdapter(getContext(), current.get(Calendar.YEAR),
                current.get(Calendar.MONTH) + 1, current.get(Calendar.DATE), false);
        //adapter.toRefresh = this;
        dailyExpenseList.setAdapter(adapter);
        dailyExpenseList.addItemDecoration(new SpaceItemDecoration(16));

        dailyExpenseList.addOnItemTouchListener(new ItemTouchListener<>(
                new ItemTouchListener.RecyclerViewOperator<EntryNode>() {
            @Override
            public Context getContext() {
                return CalendarFragment.this.getContext();
            }

            @Override
            public boolean isTouchable(EntryNode data) {
                return true;
            }

            @Override
            public void performEdit(View view, int pos, EntryNode data) {
                Intent intent = new Intent(getActivity(), ExpenseEditActivity.class);
                intent.putExtra(ExpenseEditActivity.LAST_EXPENSE,
                        ((EntryNode.ExpenseItem) data).getExpense());
                intent.putExtra(ExpenseEditActivity.INDEX, pos);
                startActivityForResult(intent, EDIT_EXPENSE);
            }

            @Override
            public void performRemove(View view, int pos, EntryNode data) {
                new ExpenseDAO(getContext()).delExpense(((EntryNode.ExpenseItem) data).getExpense().getId());
                ((ExpenseListAdapter) dailyExpenseList.getAdapter()).remove(pos, data);
            }
        }, dailyExpenseList, false));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case EDIT_EXPENSE: {
                int index = data.getIntExtra(ExpenseEditActivity.INDEX, -1);
                Expense newExpense = (Expense) data.getSerializableExtra(ExpenseEditActivity.LAST_EXPENSE);
                new ExpenseDAO(getContext()).editExpense(newExpense);
                ((ExpenseListAdapter) dailyExpenseList.getAdapter()).change(index, new EntryNode.ExpenseItem(newExpense));
            } break;
            case EDIT_TODO: {
                int index = data.getIntExtra(TodoEditActivity.INDEX, -1);
                Todo newTodo = (Todo) data.getSerializableExtra(TodoEditActivity.LAST_TODO);
                new TodoDAO(getContext()).editTodo(newTodo);
                ((TodoListAdaptor) dailyTodoList.getAdapter()).change(index, newTodo);
            } break;
        }
    }
}
