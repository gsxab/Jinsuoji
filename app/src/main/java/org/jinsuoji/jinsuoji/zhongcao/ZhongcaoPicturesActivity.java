package org.jinsuoji.jinsuoji.zhongcao;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.goyourfly.multiple.adapter.MultipleAdapter;
import com.goyourfly.multiple.adapter.MultipleSelect;

import org.jinsuoji.jinsuoji.ListRefreshable;
import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.Zhongcao;
import org.jinsuoji.jinsuoji.model.ZhongcaoCategory;

import java.util.List;

public class ZhongcaoPicturesActivity extends AppCompatActivity
        implements MenuBarCallback, ListRefreshable, View.OnClickListener {
    private RecyclerView pictureList;
    private MultipleAdapter adapter;
    private ZhongcaoPicturesAdapter zhongcaoPicturesAdapter;
    private PicturesMenuBar menuBar;
    private ZhongcaoCategory category;
    private final int CHOOSE_PHOTOS = 14;
    private static final String TAG = "o.j.j.z.ZhongcaoPicAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhongcao_pictures);

        category = (ZhongcaoCategory) getIntent().getSerializableExtra(ZhongcaoCategoriesFragment.ZHONGCAO_CATEGORY);

        pictureList = findViewById(R.id.picture_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category.getName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryFinish(v);
            }
        });

        ImageButton toolbarAdd = findViewById(R.id.toolbar_add);
        toolbarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarAddClicked(v);
            }
        });

        if (adapter == null) {
            if (zhongcaoPicturesAdapter == null) {
                zhongcaoPicturesAdapter = new ZhongcaoPicturesAdapter(this, this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onToolbarAddClicked(v);
                            }
                        }, category.getId());
            }
            if (menuBar == null) {
                menuBar = new PicturesMenuBar(this,
                        getResources().getColor(R.color.colorAccent), Gravity.TOP, this);
            }
            adapter = MultipleSelect.with(this)
                    .adapter(zhongcaoPicturesAdapter)
                    .ignoreViewType(new Integer[]{ZhongcaoPicturesAdapter.VH_EMPTY})
                    .customMenu(menuBar)
                    .build();
        }

        int itemCount = zhongcaoPicturesAdapter.getItemCount();
        pictureList.setLayoutManager(itemCount == 0 ? new LinearLayoutManager(this) :
                new GridLayoutManager(this, 3));
        pictureList.setAdapter(adapter);
    }

    public void tryFinish(View view) {
        if (!menuBar.cancel()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        tryFinish(null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean action(int actionId, List<Integer> indices) {
        return false;
    }

    @Override
    public void refreshList() {
        zhongcaoPicturesAdapter.refresh(this);
        adapter.notifyDataSetChanged();
        int itemCount = zhongcaoPicturesAdapter.getItemCount();
        pictureList.setLayoutManager(new GridLayoutManager(this,
                itemCount < 3 ? itemCount : 3));
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, R.string.placeholder, Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, ZhongcaoDetailActivity.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            onToolbarAddClicked(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onToolbarAddClicked(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_requesting)
                    .setMessage(R.string.permission_request_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(ZhongcaoPicturesActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), CHOOSE_PHOTOS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PHOTOS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if(data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            handleInsertImage(imageUri);
                        }
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        handleInsertImage(imageUri);
                    }
                }
            }
        }
    }

    private void handleInsertImage(Uri imageUri) {
        Log.d(TAG, "onActivityResult: " + imageUri.toString());
        String path;
        if (!TextUtils.isEmpty(imageUri.getAuthority())) { //使用 getAuthority 做判断条件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 4.4及以上，返回content://com.android.providers.media.documents/document/image%3A12345
                String wholeID = DocumentsContract.getDocumentId(imageUri);
                String id = wholeID.split(":")[1];
                String[] column = { MediaStore.Images.Media.DATA };
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[] { id }, null);
                if (cursor == null) throw new AssertionError();
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(columnIndex);
                } else {
                    path = null;
                }
                cursor.close();
            } else {
                // 4.4以下，返回content://media/external/images/media/188222
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
                if (cursor == null) throw new AssertionError();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
            }
        } else {
            path = imageUri.getPath(); //小米选择照片返回 data="file:///..." uri.getAuthority()==""
        }
        if (path == null) {
            Toast.makeText(this, R.string.add_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Zhongcao zhongcao = new ZhongcaoDAO(this).createZhongcao(path, category.getId());
        refreshList();
    }
}
