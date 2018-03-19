package com.market.secondhandmarket;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.market.secondhandmarket.adapter.SelectedPhotoAdapter;
import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.bean.User;
import com.market.secondhandmarket.constant.DbConstant;
import com.market.secondhandmarket.util.MyImageUtils;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.market.secondhandmarket.constant.RequestConstant.REQUEST_CODE_CHOOSE;

public class PublishActivity extends Activity {
    @BindView(R.id.pb_title)
    MaterialEditText mPbTitle;
    @BindView(R.id.pb_content)
    MaterialEditText mPbContent;
    @BindView(R.id.publish_rv)
    RecyclerView mPublishRv;
    @BindView(R.id.publish_select)
    QMUIRoundButton mPublishSelect;
    @BindView(R.id.publish_btn)
    QMUIRoundButton mPublishBtn;
    @BindView(R.id.publish_price)
    MaterialEditText mPublishPrice;
    @BindView(R.id.publish_phone)
    MaterialEditText mPublishPhone;


    protected List<Uri> mSelectPhoto = new ArrayList<>();
    private SelectedPhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        mPhotoAdapter = new SelectedPhotoAdapter(mSelectPhoto, this);
        mPhotoAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//缩放
        mPhotoAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Logger.d("long click: " + position);
                return true;
            }
        });
        mPublishRv.setLayoutManager(new GridLayoutManager(this, 4));
        mPublishRv.setAdapter(mPhotoAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_OK) {
                    mSelectPhoto.addAll(Matisse.obtainResult(data));
                    mPhotoAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @OnClick({R.id.publish_select, R.id.publish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish_select:
                MyImageUtils.selectPic(this, REQUEST_CODE_CHOOSE);
                break;
            case R.id.publish_btn:
                if (BmobUser.getCurrentUser(User.class) != null) {
                    final Item item = new Item();
                    item.setUser(BmobUser.getCurrentUser(User.class));
                    item.setTitle(mPbTitle.getText().toString());
                    item.setContent(mPbContent.getText().toString());
                    item.setType(DbConstant.ITEM_SELL);
                    item.setPrice(mPublishPrice.getText().toString());
                    item.setPhone(mPublishPhone.getText().toString());

                    final String[] filePaths = new String[mSelectPhoto.size()];
                    for (int i = 0; i < mSelectPhoto.size(); i++) {
                        Uri uri = mSelectPhoto.get(i);
                        final String path = getRealFilePath(uri);
                        filePaths[i] = path;
                    }

                    if (filePaths.length != 0) {
                        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> urls) {
                                Logger.d("图片上传onSuccess: \n" + urls.size());
                                //全部上传完成
                                if (urls.size() == filePaths.length) {
                                    item.setUrls(urls);
                                    item.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(PublishActivity.this, "成功发布宝贝信息！", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Logger.e(e.getMessage() + " " + e.getErrorCode());
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                                //1、curIndex--表示当前第几个文件正在上传
                                //2、curPercent--表示当前上传文件的进度值（百分比）
                                //3、total--表示总的上传文件数
                                //4、totalPercent--表示总的上传进度（百分比）
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    } else {    //没有选择图片
                        item.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(PublishActivity.this, "成功发布宝贝信息！", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Logger.e(e.getMessage() + " " + e.getErrorCode());
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected String getRealFilePath(Uri uri) {
        if (uri == null)
            return null;
        String scheme = uri.getScheme();
        String path = null;
        if (scheme == null) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        path = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        if (path == null) {
            path = uri2path(uri);
        }
        return path;
    }

    private String uri2path(Uri uri) {
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        String coverStr = sdCardPath.substring(sdCardPath.lastIndexOf(File.separator) + 1);
        List<String> uriPaths = uri.getPathSegments();

        for (int i = 0; i < uriPaths.size(); i++) {
            if (uriPaths.get(i).equals(coverStr)) {
                for (int j = i + 1; j < uriPaths.size(); j++) {
                    sdCardPath = File.separator + uriPaths.get(j);
                }
                break;
            }
        }
        return sdCardPath;
    }
}
