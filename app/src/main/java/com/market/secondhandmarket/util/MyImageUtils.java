package com.market.secondhandmarket.util;

/**
 * Created by a6100890 on 2018/3/16.
 */

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import com.blankj.utilcode.util.ImageUtils;
import com.market.secondhandmarket.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.market.secondhandmarket.constant.RequestConstant.REQUEST_CODE_CHOOSE;

/**
 * 知乎的图片选择器n
 */
public class MyImageUtils {
    public static void selectPic(Activity activity, int requestCode) {
        Set<MimeType> typeSet = new HashSet<>();
        typeSet.add(MimeType.JPEG);
        typeSet.add(MimeType.PNG);

        Matisse.from(activity)
                .choose(typeSet) // 选择 mime 的类型
                .countable(true) // 显示选择的数量
                .capture(true)  // 开启相机，和 captureStrategy 一并使用否则报错
                .captureStrategy(new CaptureStrategy(true, "com.market.secondhandmarket.fileprocider")) // 拍照的图片路径
                .theme(R.style.Matisse_Zhihu) // 蓝色背景
                .maxSelectable(9) // 图片选择的最多数量
                .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size)) // 列表中显示的图片大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.5f) // 缩略图的比例
                .imageEngine(new MyGlideEngine()) // 使用的图片加载引擎
                .forResult(REQUEST_CODE_CHOOSE); // 设置作为标记的请求码，返回图片时使用

    }

    /*public static File bitmap2File(Bitmap bitmap,int compressRate) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        byte[] bytes = baos.toByteArray();
        mPhotoFile = new File(getExternalCacheDir(), "photo_file.jpg");
        new BufferedOutputStream(new FileOutputStream(mPhotoFile)).write(bitmapByte);
    }*/
}
