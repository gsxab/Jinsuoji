package org.jinsuoji.jinsuoji;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.model.Todo;

import java.util.Calendar;
import java.util.Date;

/**
 * 编辑任务的Activity.
 */
public class TodoEditActivity extends AppCompatActivity {
    public static final String KEY = "exp_ToDo";
    public static final String LAST_TODO = "org.jinsuoji.jinsuoji.LastToDo";
    public static final String TIME = "org.jinsuoji.jinsuoji.Time";
    public static final String INDEX = "org.jinsuoji.jinsuoji.Index";
    public static final String TAG = "o.j.j.TEAct";

    Handler handler;
    Todo todo;
    EditText name,priority, memo;
    TextView time,reminder;
    ImageButton cancel, ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        handler = new Handler();
        name = findViewById(R.id.name_editor);
        priority = findViewById(R.id.priority_editor);
        reminder = findViewById(R.id.remind_editor);
        memo = findViewById(R.id.memo_editor);
        time = findViewById(R.id.time_editor);

        if(savedInstanceState != null){
           todo = (Todo)savedInstanceState.get(KEY);
        }else{
            Todo todo = (Todo)getIntent().getSerializableExtra(LAST_TODO);
            if(todo == null){
                this.todo = new Todo(-1,null,"",0,"",false);
                if(getIntent() != null){
                    this.todo.setDateTime((Date) getIntent().getSerializableExtra(TIME));
                }
            }else{
                this.todo = todo;
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
                if (!composeTodo()) {
                    Toast.makeText(TodoEditActivity.this, R.string.missing_args,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(LAST_TODO, todo);
                intent.putExtra(INDEX, getIntent().getIntExtra(INDEX, -1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        name.setText(todo.getTaskName());
        priority.setText(String.valueOf(todo.getPriority()));
        memo.setText(todo.getMemo());
        time.setText(todo.getDateTime() == null ? "" : DateUtils.toDateTimeString(todo.getDateTime()));


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(1);
            }
        });
        reminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showTimeDialog(2);
            }
        });
    }

    private  void showTimeDialog(int id){
        switch (id){
            case 1:
            {
                final Calendar current = Calendar.getInstance();
                new DatePickerDialog(TodoEditActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear,
                                          final int dayOfMonth) {
                        TimePickerDialog timeDialog=new TimePickerDialog(TodoEditActivity.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Date date = DateUtils.makeDate(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                time.setText(DateUtils.toDateTimeString(date));
                            }
                        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true);
                        timeDialog.setTitle("请选择时间");
                        timeDialog.show();
                    }
                }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH))
                        .show();
            } break;
            case 2:
            {
                final Calendar current = Calendar.getInstance();
                Date timeSelected = DateUtils.fromDateTimeString(time.getText().toString());
                if (timeSelected != null) {
                    current.setTimeInMillis(timeSelected.getTime());
                }
                new DatePickerDialog(TodoEditActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear,
                                          final int dayOfMonth) {
                        TimePickerDialog timeDialog=new TimePickerDialog(TodoEditActivity.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Date date = DateUtils.makeDate(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                reminder.setText(DateUtils.toDateTimeString(date));
                            }
                        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true);
                        timeDialog.setTitle("请选择时间");
                        timeDialog.show();
                    }
                }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH))
                        .show();
            } break;
        }
    }



    /**
     * 合成{@link Todo}并检查是否已经填写完毕.
     * @return true=填写完毕;false=未完毕
     */
    private boolean composeTodo() {
        boolean flag = true;
        if (name.getText().length() == 0) {
            todo.setTaskName(name.getText().toString());
            flag = false;
        }
        if (time.getText().length() == 0) {
            todo.setDateTime(DateUtils.fromDateTimeString(time.getText().toString()));
            flag = false;
        }
        if (priority.getText().length() == 0) {
            todo.setPriority(Integer.valueOf(priority.getText().toString()));
            flag = false;
        }
        if (reminder.getText().length() == 0) {
            // TODO 加进任务里
            flag = false;
        }
        if (memo.getText().length() == 0) {
            todo.setMemo(memo.getText().toString());
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        composeTodo();
        savedInstanceState.putSerializable(KEY, todo);
    }
}
