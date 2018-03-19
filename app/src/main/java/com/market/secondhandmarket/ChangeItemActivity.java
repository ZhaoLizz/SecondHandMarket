package com.market.secondhandmarket;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.market.secondhandmarket.bean.Item;
import com.market.secondhandmarket.util.MyImageUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.market.secondhandmarket.constant.RequestConstant.REQUEST_CODE_CHOOSE;

/**
 * 更新出售物品信息
 */
public class ChangeItemActivity extends PublishActivity {
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (Item) getIntent().getSerializableExtra("item");

        mPublishBtn.setText("更新");
        mPbTitle.setText(item.getTitle());
        mPbContent.setText(item.getContent());
        mPublishPrice.setText(item.getPrice());
        mPublishPhone.setText(item.getPhone());
    }

    @Override
    @OnClick({R.id.publish_select, R.id.publish_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish_select:
                MyImageUtils.selectPic(this, REQUEST_CODE_CHOOSE);
                break;
            case R.id.publish_btn:
                item.setTitle(mPbTitle.getText().toString());
                item.setContent(mPbContent.getText().toString());
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
                                            Toast.makeText(ChangeItemActivity.this, "成功发布宝贝信息！", Toast.LENGTH_SHORT).show();
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
                    item.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(ChangeItemActivity.this, "成功更新宝贝信息！", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Logger.e(e.getMessage() + " " + e.getErrorCode());
                            }
                        }
                    });
                }
                break;
        }
    }
}
