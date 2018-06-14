package org.jinsuoji.jinsuoji;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.data_access.ExpenseDAO;
import org.jinsuoji.jinsuoji.model.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 编辑记账的Activity.
 */
public class ExpenseEditActivity extends AppCompatActivity {
    public static final String KEY = "exp_expense";
    public static final String TIME = "org.jinsuoji.jinsuoji.Time";
    public static final String INDEX = "org.jinsuoji.jinsuoji.Index";
    public static final String LAST_EXPENSE = "org.jinsuoji.jinsuoji.LastExpense";
    public static final String TAG = "o.j.j.EEAct";
    Handler handler;
    Expense expense;
    EditText item, money;
    TextView time, category;
    ImageButton cancel, ok;

    public void showDateTimeDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        //noinspection ConstantConditions
        calendar.setTimeInMillis(DateUtils.fromDateString(time.getText().toString()).getTime());
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                time.setText(DateUtils.toDateString(DateUtils.makeDate(year, month + 1, dayOfMonth)));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
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
                    this.expense.setDatetime((Date) getIntent().getSerializableExtra(TIME));
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
                intent.putExtra(INDEX, getIntent().getIntExtra(INDEX, -1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        item.setText(expense.getItem());
        time.setText(expense.getDatetime() == null ? DateUtils.toDateString(Calendar.getInstance().getTime())
                : DateUtils.toDateString(expense.getDatetime()));
        money.setText(expense.getMoney() == 0 ? "" : String.format(Locale.getDefault(),
                "%1$.2f", expense.getMoney() / 100f));
        category.setText(expense.getCategory() == null ? "" : expense.getCategory());
        //category.setAdapter(new ArrayAdapter<>(this,
        //        android.R.layout.simple_dropdown_item_1line/*layout_id*/,
        //        new ExpenseDAO(this).getAllCategories()));
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

    //expenditure-edit categoryDialog
    public void showCategoryDialog(View v){
        List<String> categories = new ArrayList<>();
        categories.add(0, getString(R.string.create_category));
        categories.add(1, getString(R.string.uncategorized));
        categories.addAll(new ExpenseDAO(this).getAllCategories());
        final String[] items = categories.toArray(new String[0]);
        AlertDialog.Builder categoryDialog = new AlertDialog.Builder(ExpenseEditActivity.this);
        categoryDialog.setTitle(R.string.category);
        categoryDialog.setItems(items,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                switch(which){
                    case 0:
                        showAddCategoryDialog();
                        break;
                    default:
                        category.setText(items[which]);
                        break;
                }
            }
        });
        categoryDialog.show();
    }

    //addCategoryDialog
    private void showAddCategoryDialog(){
        final EditText editText = new EditText(ExpenseEditActivity.this);
        AlertDialog.Builder addCategoryDialog = new AlertDialog.Builder(ExpenseEditActivity.this);
        addCategoryDialog.setTitle(R.string.create_category).setView(editText);
        addCategoryDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                String categoryName = editText.getText().toString();
                if (categoryName.isEmpty()) return;
                new ExpenseDAO(ExpenseEditActivity.this).createOrGetCategory(categoryName);
                Toast.makeText(ExpenseEditActivity.this, R.string.category_created,
                        Toast.LENGTH_SHORT).show();
                category.setText(categoryName);
                dialog.dismiss();
            }
        });
        addCategoryDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        addCategoryDialog.show();
    }
}
