package com.btzh.mytakephoto.utils;

import android.net.Uri;
import android.os.Environment;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;


public class ImageHelper {
    private TakePhoto takePhoto;
    private int limit;

    public static ImageHelper of(TakePhoto takePhoto, int photoId) {
        return new ImageHelper(takePhoto, photoId);
    }

    private ImageHelper(TakePhoto takePhoto, int limit) {
        this.takePhoto = takePhoto;
        this.limit = limit;
        init();
    }


    private void init() {
        if (takePhoto == null) {
            try {
                throw new Exception("takePhoto can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showView();
        }
    }

    private void showView() {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        switch (limit) {
            case 0:
//              takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                takePhoto.onPickMultiple(3);
//                if (rgFrom.getCheckedRadioButtonId() == R.id.rbFile) {
//                    if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
//                        takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
//                    } else {
//                        takePhoto.onPickFromDocuments();
//                    }
//                    return;
//                } else {
//                    if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
//                        takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
//                    } else {
//                        takePhoto.onPickFromGallery();
//                    }
//                }
                break;
            case 1:
//                if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
//                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
//                } else {
                takePhoto.onPickFromCapture(imageUri);
//                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置takephoto属性
     *
     * @param takePhoto
     */
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //使用takephoto自带相册
        builder.setWithOwnGallery(true);
        //矫正拍照的旋转角度
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * 压缩参数
     *
     * @param takePhoto
     */
    private void configCompress(TakePhoto takePhoto) {
//      takePhoto.onEnableCompress(null, false);
        int maxSize = 144000;
        int width = 800;
        int height = 800;
        boolean showProgressBar = true; //显示压缩进度条
        //boolean showProgressBar = false; //显示压缩进度条
        boolean enableRawFile = true;   //压缩后保存原图
        CompressConfig config;
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();
        //使用第三方的裁切工具LubanOptions
//      LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
//      config = CompressConfig.ofLuban(option);
//      config.enableReserveRaw(enableRawFile);
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    /**
     * 剪切
     *
     * @return
     */
    private CropOptions getCropOptions() {
        /**
         * 高*宽
         */
        int height = 800;
        int width = 800;
        //使用takephoto自带裁切工具
        boolean withWonCrop = true;
        CropOptions.Builder builder = new CropOptions.Builder();
        //使用宽高比
//        builder.setAspectX(width).setAspectY(height);
        //使用宽高积
        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }
}
