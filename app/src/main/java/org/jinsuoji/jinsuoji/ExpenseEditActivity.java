package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.model.Expense;

import java.util.Calendar;
import java.util.Date;

/**
 * 编辑记账的Activity.
 */
public class ExpenseEditActivity extends AppCompatActivity {
    public static final String KEY = "exp_expense";
    public static final String LAST_EXPENSE = "org.jinsuoji.jinsuoji.LastExpense";
    public static final String TAG = "o.j.j.EEAct";
    Handler handler;
    Expense expense;
    EditText item, money;
    AutoCompleteTextView category;
    TextView time;
    ImageButton cancel, ok;

    public void showDateTimeDialog(View v) {
        TimePickerDialog dialog = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, final long millseconds) {
                        time.setText(DateUtils.toDateTimeString(new Date(millseconds)));
                        Log.d(TAG, "after setText: " + time.getText());
                    }
                })
                .setCancelStringId(getString(R.string.cancel))
                .setSureStringId(getString(R.string.ok))
                .setTitleStringId(getString(R.string.time))
                .setYearText(getString(R.string.year))
                .setMonthText(getString(R.string.month))
                .setDayText(getString(R.string.date_of_month))
                .setType(Type.YEAR_MONTH_DAY)
                .setCyclic(false)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextNormalColor(R.color.colorPrimaryDark)
                .setWheelItemTextSelectorColor(R.color.colorAccent)
                .setWheelItemTextSize(14)
                .build();
//        以下代码不能用.这个库的布局的工具栏字体大小竟然是写死的.
//        View dialogView = dialog.getView();
//        if (dialogView != null) {
//            ((TextView) dialogView.findViewById(R.id.tv_cancel)).setTextSize(18);
//            ((TextView) dialogView.findViewById(R.id.tv_title)).setTextSize(18);
//            ((TextView) dialogView.findViewById(R.id.tv_sure)).setTextSize(18);
//        }
        dialog.show(getSupportFragmentManager(), TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_edit);

        handler = new Handler();

        item = findViewById(R.id.item_editor);
        time = findViewById(R.id.time_editor);
        money = findViewById(R.id.money_editor);
        category = findViewById(R.id.category_editor);

        if (savedInstanceState != null) {
            expense = ((Expense) savedInstanceState.get(KEY));
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                Expense expense = (Expense) getIntent().getSerializableExtra(LAST_EXPENSE);
                if (expense == null) {
                    this.expense = new Expense(-1, "", null, 0, null);
                    this.expense.setDatetime((Date) getIntent().getSerializableExtra("org.jinsuoji.jinsuoji.Time"));
                } else {
                    this.expense = expense;
                }
            }
        }
        if (expense == null) {
            expense = new Expense(-1, "", Calendar.getInstance().getTime(), 0, null);
        }

        cancel = findViewById(R.id.toolbar_return);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        ok = findViewById(R.id.toolbar_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (!composeExpense()) {
                    Toast.makeText(ExpenseEditActivity.this, R.string.missing_args,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(LAST_EXPENSE, expense);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        item.setText(expense.getItem());
        time.setText(/*expense.getDatetime() == null ? "" : */DateUtils.toDateString(expense.getDatetime()));
        money.setText(expense.getMoney() == 0 ? "" : String.valueOf(expense.getMoney()));
        category.setText(expense.getCategory() == null ? "" : expense.getCategory());

        category.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line/*layout_id*/,
                new ExpenseDAO(this).getAllCategories()));
    }

    /**
     * 合成{@link Expense}并检查是否已经填写完毕.
     * @return true=填写完毕;false=未完毕
     */
    private boolean composeExpense() {
        boolean flag = true;
        if (item.getText().length() == 0) {
            flag = false;
        } else {
            expense.setItem(item.getText().toString());
        }
        expense.setDatetime(DateUtils.fromDateString(time.getText().toString()));
        if (category.getText().length() == 0){
            flag = false;
            expense.setCategory(null);
        } else {
            expense.setCategory(category.getText().toString());
        }
        if(money.getText().length() == 0) {
            expense.setMoney(0);
            flag = false;
        } else {
            int tmp = Math.round(Float.valueOf(money.getText().toString()) * 100);
            if (tmp == 0) {
                flag = false;
            }
            expense.setMoney(tmp);
        }
        return flag;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        composeExpense();
        savedInstanceState.putSerializable(KEY, expense);
    }
}
