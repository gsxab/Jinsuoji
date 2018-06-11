package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Vibrator;

public class RemindActivity extends AppCompatActivity {
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
        String task = bundle.getString("task");
        String time = bundle.getString("time");
        textView2.setText(task + "   " + time);
        final Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
        vibrator.vibrate(new long[]{300, 500}, 0);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemindActivity.this, TodoEditActivity.class);
                startActivity(intent);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.cancel();
            }
        });
    }

}
