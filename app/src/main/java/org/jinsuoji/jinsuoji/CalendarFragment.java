package org.jinsuoji.jinsuoji;

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

import java.util.Calendar;

/**
 * 日历页{@link Fragment}.
 * 日历+当日列表.
 */
public class CalendarFragment extends Fragment {
    private static final String TAG = "jsj.CalendarFragment";

    /**
     * 实例化方法.
     * @return 自己
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
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
                // ((TodoListAdaptor) dailyTodoList.getAdapter());
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
                current.get(Calendar.MONTH) + 1, current.get(Calendar.DATE), null));
        dailyTodoList.addItemDecoration(new SpaceItemDecoration(16));
        //dailyTodoList.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        dailyExpenseList = view.findViewById(R.id.daily_expense_list);
        dailyExpenseList.setLayoutManager(new LinearLayoutManager(getContext()));
        dailyExpenseList.setAdapter(new ExpenseListAdapter(getContext(), current.get(Calendar.YEAR),
                current.get(Calendar.MONTH) + 1, current.get(Calendar.DATE), false));
        dailyExpenseList.addItemDecoration(new SpaceItemDecoration(16));
        //dailyExpenseList.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
    }
}
