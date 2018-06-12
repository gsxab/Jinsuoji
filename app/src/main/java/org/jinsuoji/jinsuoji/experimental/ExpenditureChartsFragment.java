package org.jinsuoji.jinsuoji.experimental;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenditureChartsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenditureChartsFragment extends Fragment {
    private static final String KEY_YEAR = "key_year";
    private static final String KEY_MONTH = "key_month";
    private static final String TAG = "o.j.j.e.ECF";

    private int year, month;

    ColumnChartView columnChart;
    PieChartView expensePieChart;

    public ExpenditureChartsFragment() {
        // Required empty public constructor
    }

    public static ExpenditureChartsFragment newInstance(int year, int month) {
        ExpenditureChartsFragment fragment = new ExpenditureChartsFragment();
        fragment.year = year;
        fragment.month = month;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenditure_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        columnChart = view.findViewById(R.id.column_by_date);
        expensePieChart = view.findViewById(R.id.expense_pie_by_category);
        if (savedInstanceState != null) {
            year = savedInstanceState.getInt(KEY_YEAR);
            month = savedInstanceState.getInt(KEY_MONTH);
        }
        refresh(getContext());
    }

    public void setSelector(Context context, int year, int month) {
        this.year = year;
        this.month = month;
        refresh(context);
    }

    public void refresh(Context context) {
        if (columnChart == null || expensePieChart == null) {
            return;
        }
        ExpenseDAO expenseDAO = new ExpenseDAO(context);

        {
            int[] dailyExpenses = expenseDAO.groupByDate(year, month);
            List<Column> columns = new ArrayList<>();
            int color = ChartUtils.nextColor();
            for (int i = 0; i < dailyExpenses.length; i++) {
                int dailyExpense = dailyExpenses[i];
                columns.add(new Column()
                        .setHasLabelsOnlyForSelected(true)
                        .setValues(Collections.singletonList(new SubcolumnValue()
                            .setColor(color)
                            .setLabel(context.getString(R.string.chart_expense_label, i + 1, dailyExpense / 100f))
                            .setValue(dailyExpense / 100f))));
            }
            ColumnChartData columnChartData = new ColumnChartData()
                    .setColumns(columns);
            columnChartData.setAxisXBottom(new Axis().setAutoGenerated(true));
            columnChartData.setAxisYLeft(new Axis().setAutoGenerated(true).setHasLines(true));

            columnChart.setColumnChartData(columnChartData);
            columnChart.setZoomType(ZoomType.HORIZONTAL);
        }

        {
            List<SliceValue> expenseValues = new ArrayList<>();
            Map<String, Integer> groupByCategory = expenseDAO.groupByCategory(year, month);
            for (Map.Entry<String, Integer> entry : groupByCategory.entrySet()) {
                expenseValues.add(new SliceValue()
                        .setLabel(context.getString(R.string.pie_expense_label,
                        entry.getKey(), entry.getValue() / 100f))
                        .setColor(ChartUtils.nextColor())
                        .setValue(entry.getValue() / 100f));
            }
            expensePieChart.setPieChartData(new PieChartData()
                    .setValues(expenseValues)
                    .setHasLabelsOnlyForSelected(true));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_YEAR, year);
        outState.putInt(KEY_MONTH, month);
    }
}