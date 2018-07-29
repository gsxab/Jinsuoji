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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.goyourfly.multiple.adapter.MultipleAdapter;
import com.goyourfly.multiple.adapter.MultipleSelect;

import org.jinsuoji.jinsuoji.ListRefreshable;
import org.jinsuoji.jinsuoji.R;
import org.jinsuoji.jinsuoji.data_access.ZhongcaoDAO;
import org.jinsuoji.jinsuoji.model.Zhongcao;
import org.jinsuoji.jinsuoji.model.ZhongcaoCategory;

import java.util.ArrayList;
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
        pictureList.setLayoutManager(new GridLayoutManager(this,
                itemCount == 0 ? 1 : itemCount < 3 ? itemCount : 3));
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
    public boolean action(int actionId, final List<Integer> indices) {
        if (indices.size() == 0) throw new AssertionError();
        switch (actionId) {
            case R.id.action_share: {
                // FIXME 分享不能使用
                Intent shareIntent = new Intent();
                List<Zhongcao> list = zhongcaoPicturesAdapter.getList();
                final ArrayList<String> shared = new ArrayList<>();
                for (Integer index : indices) {
                    shared.add(list.get(index).getPicture());
                }
                String subject = list.get(indices.get(0)).getMemo();
                if (subject != null && subject.isEmpty()) {
                    subject = category.getName();
                }
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.setType("*/*");
                //shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putStringArrayListExtra(Intent.EXTRA_TEXT, shared);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                return true;
            }
            case R.id.action_delete: {
                final List<Zhongcao> list = zhongcaoPicturesAdapter.getList();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_warning)
                        .setMessage(getResources().getString(R.string.multi_delete_warning_message, indices.size()))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ZhongcaoDAO zhongcaoDAO = new ZhongcaoDAO(ZhongcaoPicturesActivity.this);
                                for (int index : indices) {
                                    zhongcaoDAO.deleteZhongcao(list.get(index).getId());
                                }
                                zhongcaoPicturesAdapter.refresh(ZhongcaoPicturesActivity.this);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 取消时无操作
                            }
                        })
                        .show();
                return true;
            }
            case R.id.action_move: {
                List<Zhongcao> list = zhongcaoPicturesAdapter.getList();
                final List<Integer> ids = new ArrayList<>();
                for (Integer index : indices) {
                    ids.add(list.get(index).getId());
                }
                final ZhongcaoDAO zhongcaoDAO = new ZhongcaoDAO(this);
                final List<ZhongcaoCategory> categories = zhongcaoDAO.getAllCategories();
                String[] categoryNames = new String[categories.size() + 1];
                categoryNames[0] = getString(R.string.create_category);
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames[i + 1] = categories.get(i).getName();
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.category)
                        .setItems(categoryNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    final EditText editText = new EditText(ZhongcaoPicturesActivity.this);
                                    new AlertDialog.Builder(ZhongcaoPicturesActivity.this)
                                            .setTitle(R.string.create_category)
                                            .setView(editText)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String categoryName = editText.getText().toString();
                                                    if (categoryName.isEmpty()) return;
                                                    ZhongcaoCategory category =
                                                            zhongcaoDAO.getOrCreateCategory(categoryName);
                                                    zhongcaoDAO.editZhongcaoCategory(ids, category.getId());
                                                    zhongcaoPicturesAdapter.refresh(ZhongcaoPicturesActivity.this);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                } else {
                                    zhongcaoDAO.editZhongcaoCategory(ids,
                                            categories.get(which - 1).getId());
                                    zhongcaoPicturesAdapter.refresh(ZhongcaoPicturesActivity.this);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .show();
                return true;
            }
            case R.id.action_edit: {
                final List<Zhongcao> list = zhongcaoPicturesAdapter.getList();
                final EditText editText = new EditText(this);
                if (indices.size() == 1) {
                    editText.setText(list.get(indices.get(0)).getMemo());
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.label_edit)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (new ZhongcaoDAO(ZhongcaoPicturesActivity.this)
                                        .editMemo(list, indices, editText.getText().toString())) {
                                    zhongcaoPicturesAdapter.refresh(ZhongcaoPicturesActivity.this);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 取消时无操作
                            }
                        })
                        .show();
                return true;
            }
            case R.id.action_set_cover: {
                if (indices.size() == 1) {
                    new ZhongcaoDAO(this).setCategoryCover(category.getId(),
                            zhongcaoPicturesAdapter.getList().get(indices.get(0)).getPicture());
                    return true;
                } else {
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public void refreshList() {
        zhongcaoPicturesAdapter.refresh(this);
        adapter.notifyDataSetChanged();
        int itemCount = zhongcaoPicturesAdapter.getItemCount();
        pictureList.setLayoutManager(new GridLayoutManager(this,
                itemCount == 0 ? 1 : itemCount < 3 ? itemCount : 3));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ShowPictureActivity.class);
        intent.putExtra(ShowPictureActivity.ZHONGCAO_RECORD, ((Zhongcao) v.getTag()));
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshList();
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
                    if (data.getClipData() != null) {
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
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
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
                String[] projection = {MediaStore.Images.Media.DATA};
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
        //noinspection unused
        Zhongcao zhongcao = new ZhongcaoDAO(this).createZhongcao(path, category.getId());
        refreshList();
    }
}
