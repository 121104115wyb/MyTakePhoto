package com.btzh.mytakephoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.jph.takephoto.model.TImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BitmapUtils {

    /**
     * @param chooseFilePath 图片当前所在的目录
     * @param localPath 保存图片的路径
     * @return
     */
    public static String saveCompressPic(String chooseFilePath, String localPath) {

        String fileName = new SimpleDateFormat("yyyyMMddHHmmssms", Locale.CHINA)
                .format(new Date()) + ".jpg";// 照片命名
        String fileNewPath = localPath + fileName;
        File file = new File(fileNewPath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Bitmap bitmap2 = BitmapFactory.decodeFile(chooseFilePath);
            Bitmap sbitMap = watermarkBitmap_pzsj(bitmap2, chooseFilePath);
            FileOutputStream out = new FileOutputStream(file);
            if (sbitMap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fileNewPath;
    }

    /**
     * @param chooseFilePath 图片的当前路径
     * @param cachePath      保存图片的路径
     * @return
     */
    public static Bitmap saveCompressPic_pzsj(String chooseFilePath, String cachePath) {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssms", Locale.CHINA)
                .format(new Date()) + ".jpg";// 照片命名
        String fileNewPath = cachePath + fileName;
        File file = new File(fileNewPath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Bitmap bitmap2 = BitmapFactory.decodeFile(chooseFilePath);
            Bitmap sbitMap = watermarkBitmap_pzsj(bitmap2, chooseFilePath);
            FileOutputStream out = new FileOutputStream(file);
            if (sbitMap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
            //tImageList.get(0).setCompressPath(fileNewPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(fileNewPath);
    }


    /**
     * 添加水印
     *
     * @param oldBitmap 原Bitmap
     * @param localPath 图片路径
     * @return
     */
    public static Bitmap watermarkBitmap_pzsj(Bitmap oldBitmap, String localPath) {

        ExifInterface exif;
        String date = "";
        String title = "";
        try {
            exif = new ExifInterface(localPath);
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == date || "null".equals(date)) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            title = "日期:" + sdf1.format(new Date());
        } else {
            title = "日期:" + date;
        }

        if (oldBitmap == null) {
            return null;
        }
        int w = oldBitmap.getWidth();//388
        int h = oldBitmap.getHeight(); //648

        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(oldBitmap, 0, 0, null);// 在 0，0坐标开始画入src
        //加入文字
        if (!TextUtils.isEmpty(title)) {
            Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTypeface(font);
            textPaint.setTextSize(h / 30);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(title, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            layout.draw(cv);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);//
        cv.restore();// 存储
        return newb;
    }


}
