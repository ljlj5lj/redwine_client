package com.example.lj.redwine.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


/**
 * Created by L.J on 2016/8/16.
 */
public class BitmapUtil {

    public final static int activity_result_camera_with_data = 1006;
    public final static int activity_result_crop_image_with_data = 1007;

    public File tempFile;

    private Activity activity;

    public BitmapUtil(Activity activity) {
        super();
        this.activity = activity;
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if(createNewFile()) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

            activity.startActivityForResult(intent, activity_result_camera_with_data);
        }
    }


    /**
     * 照相获取完成图片时候裁剪图片
     */
    public void cropImageByCamera() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(tempFile), "image/*");
        buildCropIntent(intent);

        activity.startActivityForResult(intent, activity_result_crop_image_with_data);
    }


    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        if(createNewFile()) {
            buildCropIntent(intent);
            activity.startActivityForResult(intent, activity_result_crop_image_with_data);
        }
    }

    /**
     * 构建截图的intent
     * @param intent
     */
    private void buildCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // 是否去除面部检测， 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
    }

    /**
     * 创建新文件
     * @return
     */
    private boolean createNewFile() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            Toast.makeText(activity, "SD卡不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        tempFile = new File(Environment.getExternalStorageDirectory().getPath() + "/stchat" + "/images/" + System.currentTimeMillis() + ".jpg");
        if(!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }

        if(!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void setBitmap(ImageView view, String head_portrait) {
        // 设置用户头像
        Bitmap bitmap = BitmapFactory.decodeFile(head_portrait);
        if(bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }

}