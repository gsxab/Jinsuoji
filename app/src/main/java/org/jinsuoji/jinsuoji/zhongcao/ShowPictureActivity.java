package org.jinsuoji.jinsuoji.zhongcao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.Zhongcao;

public class ShowPictureActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    public static final String ZHONGCAO_RECORD = "org.jinsuoji.jinsuoji.ZhongcaoRecord";
    private ImageView picture;
    private EditText caption;
    private Zhongcao zhongcao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_picture);

        zhongcao = (Zhongcao) getIntent().getSerializableExtra(ZHONGCAO_RECORD);

        picture = findViewById(R.id.picture);
        caption = findViewById(R.id.caption);

        caption.setOnEditorActionListener(this);

        ImageButton toolbarReturn = findViewById(R.id.toolbar_return);
        toolbarReturn.setOnClickListener(this);

        setData();
    }

    private void refresh() {
        zhongcao = new ZhongcaoDAO(this).getRecordById(zhongcao.getId());
        setData();
    }

    private void setData() {
        new LoadPictureTask(zhongcao.getPicture(), new LoadPictureTask.OnLoadSuccess() {
            @Override
            public void onSuccess(Drawable drawable) {
                picture.setImageDrawable(drawable);
            }
        }, new LoadPictureTask.OnLoadFailure() {
            @Override
            public void onFailure() {
                picture.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
            }
        }).start();

        caption.setText(zhongcao.getMemo());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String newMemo = caption.getText().toString();
            new ZhongcaoDAO(ShowPictureActivity.this)
                    .editMemo(zhongcao.getId(), newMemo);
            InputMethodManager imm = (InputMethodManager) getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(caption.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            caption.clearFocus();
            refresh();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        tryFinish();
    }

    @Override
    public void onBackPressed() {
        tryFinish();
    }

    private void tryFinish() {
        if (caption.hasFocus()) {
            onEditorAction(caption, EditorInfo.IME_ACTION_DONE, null);
        } else {
            finish();
        }
    }
}
