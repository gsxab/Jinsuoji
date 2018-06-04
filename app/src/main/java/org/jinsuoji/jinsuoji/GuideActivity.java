package org.jinsuoji.jinsuoji;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.account.AccountManager;
import org.jinsuoji.jinsuoji.net.RestfulAsyncTask;
import org.jinsuoji.jinsuoji.net.ToastOnFailure;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "o.j.j.GuideActivity";
    public static final String HAS_USER_NAME = "org.jinsuoji.jinsuoji.UserName";
    TextView skip;
    Button okLogin, okRegister;
    EditText username, password;
    boolean status;

    @Override
    public void onBackPressed() {
        if (status) {
            status = false;
            findViewById(R.id.edit_wrapper).setVisibility(View.GONE);
            findViewById(R.id.button_wrapper).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = false;
        setContentView(R.layout.activity_guide);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(this);
        TextView signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(this);

        skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity(false);
            }
        });

        okLogin = findViewById(R.id.ok_login);
        okRegister = findViewById(R.id.ok_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        okLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = GuideActivity.this.username.getText().toString();
                String password = GuideActivity.this.password.getText().toString();
                if (username.isEmpty() || password.isEmpty()) return;
                AccountManager.getInstance()
                        .setInfo(username, password)
                        .login(new RestfulAsyncTask.SuccessOperation<String>() {
                            @Override
                            public void onSuccess(String result) {
                                startMainActivity(true);
                            }
                        }, new ToastOnFailure(GuideActivity.this),
                                RestfulAsyncTask.MessageOperation.ignore);
            }
        });
        okRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = GuideActivity.this.username.getText().toString();
                String password = GuideActivity.this.password.getText().toString();
                if (username.isEmpty() || password.isEmpty()) return;
                AccountManager.getInstance()
                        .register(username, password, new RestfulAsyncTask.SuccessOperation<String>() {
                            @Override
                            public void onSuccess(String result) {
                                startMainActivity(true);
                            }
                        }, new ToastOnFailure(GuideActivity.this),
                                RestfulAsyncTask.MessageOperation.ignore);
            }
        });
    }

    private void startMainActivity(boolean withAccount) {
        Preference.setGuided(GuideActivity.this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(HAS_USER_NAME, withAccount);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        status = true;
        findViewById(R.id.button_wrapper).setVisibility(View.GONE);
        findViewById(R.id.edit_wrapper).setVisibility(View.VISIBLE);
    }
}
