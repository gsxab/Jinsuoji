package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.data_access.DateUtils;
import org.jinsuoji.jinsuoji.data_access.TodoDAO;
import org.jinsuoji.jinsuoji.model.Todo;

public class RemindActivity extends AppCompatActivity {
    public static final String TODO_TO_BE_REMINDED = "org.jinsuoji.jinsuoji.TodoToBeReminded";
    private static final int EDIT_TODO = 10;

    TextView textView, textView2;
    Button button,button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        textView.setText("今琐记提醒！！");
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) throw new AssertionError();
        final Todo task = new TodoDAO(this).getById(bundle.getInt(TODO_TO_BE_REMINDED));
        textView2.setText(getString(R.string.reminder_todo_abstract, task.getTaskName(),
                DateUtils.toDateTimeString(task.getReminderTime())));
        final Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
        if (vibrator != null) {
            vibrator.vibrate(new long[]{300, 500}, 0);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindActivity.this, TodoEditActivity.class);
                intent.putExtra(TodoEditActivity.LAST_TODO, task);
                startActivityForResult(intent, EDIT_TODO);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator != null) {
                    vibrator.cancel();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TODO && resultCode == RESULT_OK) {
            new TodoDAO(this).editTodo((Todo) data.getSerializableExtra(TodoEditActivity.LAST_TODO));
        }
    }
}
