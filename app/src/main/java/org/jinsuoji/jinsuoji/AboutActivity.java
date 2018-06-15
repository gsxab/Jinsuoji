package org.jinsuoji.jinsuoji;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于我们");

        TextView aboutContent = findViewById(R.id.about_content);
        aboutContent.setText(getString(R.string.about_content, getString(R.string.app_name),
                Preference.getVersionName(this)));
    }
}
