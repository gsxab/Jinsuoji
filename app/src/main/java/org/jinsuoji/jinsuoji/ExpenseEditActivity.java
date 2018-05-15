package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.model.Expense;

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
    EditText item, money, category;
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
            Expense expense = (Expense) getIntent().getSerializableExtra(LAST_EXPENSE);
            if (expense == null) {
                this.expense = new Expense(-1, "", null, 0, "");
                if (getIntent() != null) {
                    this.expense.setDatetime((Date) getIntent().getSerializableExtra("org.jinsuoji.jinsuoji.Time"));
                }
            } else {
                // TODO 检查编辑情况：这种情况下参数是一个Expense
                this.expense = expense;
            }
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

        // TODO 初始值，在一些情况下可能不是这个.
        item.setText(expense.getItem());
        time.setText(expense.getDatetime() == null ? "" : DateUtils.toDateString(expense.getDatetime()));
        money.setText(expense.getMoney() == 0 ? "" : String.valueOf(expense.getMoney()));
        category.setText(expense.getCategory() == null ? "" : expense.getCategory());
    }

    /**
     * 合成{@link Expense}并检查是否已经填写完毕.
     * @return true=填写完毕;false=未完毕
     */
    private boolean composeExpense() {
        if (item.getText().length() == 0 || time.getText().length() == 0 ||
                category.getText().length() == 0 || money.getText().length() == 0) {
            return false;
        }
        expense.setItem(item.getText().toString());
        expense.setDatetime(DateUtils.fromDateString(time.getText().toString()));
        expense.setCategory(category.getText().toString());
        expense.setMoney(Math.round(Float.valueOf(money.getText().toString()) * 100));
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        composeExpense();
        savedInstanceState.putSerializable(KEY, expense);
    }
}
