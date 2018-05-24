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
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //加上下面的这段代码会闪退，可能是因为跟Todo的数据库操作文件有关系，所以我先注释掉了
        name.setText(todo.getTaskName());
        priority.setText(todo.getPriority());
        memo.setText(todo.getMemo());
        time.setText(todo.getDateTime() == null ? "" : DateUtils.toDateString(todo.getDateTime()));


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
                Calendar cale1 = Calendar.getInstance();
                new DatePickerDialog(TodoEditActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        StringBuffer stringBuilder = new StringBuffer("");
                        //这里获取到的月份需要加上1哦~
                        stringBuilder.append(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        time.setText(stringBuilder.toString());
                    }
                }
                        ,cale1.get(Calendar.YEAR)
                        ,cale1.get(Calendar.MONTH)
                        ,cale1.get(Calendar.DAY_OF_MONTH))
                        .show();
            }break;
            case 2:
            {
                Calendar cale1 = Calendar.getInstance();
                new DatePickerDialog(TodoEditActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                       final StringBuffer stringBuilder=new StringBuffer("");
                        //这里获取到的月份需要加上1哦~
                        stringBuilder.append(year + "-" + (monthOfYear+1) +"-"+dayOfMonth);
                        Calendar time=Calendar.getInstance();
                        TimePickerDialog timeDialog=new TimePickerDialog(TodoEditActivity.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // TODO Auto-generated method stub
                                stringBuilder.append(hourOfDay+":"+minute);
                                reminder.setText(" "+ stringBuilder);
                            }
                        }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
                        timeDialog.setTitle("请选择时间");
                        timeDialog.show();
                    }
                }
                        ,cale1.get(Calendar.YEAR)
                        ,cale1.get(Calendar.MONTH)
                        ,cale1.get(Calendar.DAY_OF_MONTH)).show();
            }break;
        }
    }



    /**
     * 合成{@link Todo}并检查是否已经填写完毕. TODO 部分保存
     * @return true=填写完毕;false=未完毕
     */
    private boolean composeTodo() {
        if (name.getText().length() == 0 || time.getText().length() == 0 ||
                priority.getText().length() == 0 || reminder.getText().length() == 0 ||
                memo.getText().length() == 0) {
            return false;
        }
        todo.setTaskName(name.getText().toString());
        todo.setDateTime(DateUtils.fromDateString(time.getText().toString()));
        todo.setPriority(Integer.valueOf(priority.getText().toString()));
        todo.setMemo(memo.getText().toString());
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        composeTodo();
        savedInstanceState.putSerializable(KEY, todo);
    }
}
