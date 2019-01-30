package com.btzh.mytakephoto.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.btzh.androidphpfiles.moudle.MyTImage;
import com.btzh.androidphpfiles.utils.BitmapUtils;
import com.btzh.androidphpfiles.utils.ImageHelper;
import com.btzh.androidphpfiles.view.adapter.PhotoAdapter;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TResult;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 自定义图片
 */
public class SimpleImage extends TakePhotoActivity {
    private Context mThis;
    private PhotoAdapter photoAdapter;
    protected ArrayList<MyTImage> myTImages = new ArrayList<>();
    private static ExecutorService poolExecutor;
    private String uploadPath = "WYB";
    protected String photoLocalPath = "";// 本地照片路径
    protected Boolean agreedAllPermissions = false;

    // 服务器上接收文件的处理页面，这里根据需要换成自己的
    protected String actionUrl = "http://192.168.200.224:8082/receive_file1.php";
    private String imageType = "sqet";
    private String choosefile = "";

    private Handler HandlerMes = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle resultB = msg.getData();
                String resultMes = resultB.getString("data");
                if (TextUtils.isEmpty(resultMes)) {
                    Toast.makeText(mThis, "上传失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonResultMes = new JSONObject(resultMes);
                if ("1".equalsIgnoreCase(jsonResultMes.optString("R"))) {
                    MyTImage myTImage = new MyTImage();
                    myTImage.setType(imageType);
                    myTImage.setLocalPath(resultB.getString("filename"));
                    myTImage.setFileName(jsonResultMes.optString("targetpath"));
                    myTImages.add(myTImage);
                    System.out.println();
                    photoAdapter.notifyDataSetChanged();
                }
                Toast.makeText(mThis, jsonResultMes.optString("mes"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mThis, "未知错误！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        //检查权限
        dynamicPermissions();
        //设置图片本地地址
        photoLocalPath = initPath("");
    }

    /**
     * 权限申请
     */
    private void dynamicPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SYSTEM_ALERT_WINDOW, //设置悬浮窗等
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.REQUEST_INSTALL_PACKAGES)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        agreedAllPermissions = true;
                    } else {
                        agreedAllPermissions = false;
                    }
                });
    }

    /**
     * folder 为你自定义的文件夹，名称
     * folder 为空时，默认为"bmmces"
     *
     * @param folder
     * @return 片保存在本地的路径
     */
    protected String initPath(String folder) {
        if (TextUtils.isEmpty(folder)) {
            folder = "/bmmces/";
        }
        String photoLocalPath = "";
        // 拍照功能，获取照片目录
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            photoLocalPath = Environment.getExternalStorageDirectory()
                    .toString() + folder;
        } else {
            //modsd = false;
            File filesDir = getFilesDir();
            photoLocalPath = filesDir.getPath() + folder;
        }
        File out = new File(photoLocalPath);
        if (!out.exists()) {
            out.mkdirs();
        }

        return photoLocalPath;
    }

    /**
     * localPath 为你自定义的文件夹，名称
     * localPath 为空时，默认为"bmmces"
     *
     * @return 片保存在本地的路径
     */
    protected String initPath() {
        String photoLocalPath = "";
        // 拍照功能，获取照片目录
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            photoLocalPath = Environment.getExternalStorageDirectory()
                    .toString() + "/bmmces/";
        } else {
            File filesDir = getFilesDir();
            photoLocalPath = filesDir.getPath() + "/bmmces/";
        }
        File out = new File(photoLocalPath);
        if (!out.exists()) {
            out.mkdirs();
        }

        return photoLocalPath;
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        choosefile = result.getImage().getCompressPath();
        commitPhotos();
    }


//    private void showImg(ArrayList<TImage> images) {
//        Intent intent = new Intent(this, ResultActivity.class);
//        intent.putExtra("images", images);
//        startActivity(intent);
//    }

    private void showImg(ArrayList<MyTImage> images) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("images", images);
        startActivity(intent);
    }



    /**
     * 设置图片显示视图
     * @param recyclerView
     */
    protected void initPhotoView(RecyclerView recyclerView) {
        if (recyclerView != null) {
            photoAdapter = new PhotoAdapter(mThis, myTImages);
            photoAdapter.setItemClickListener(((view, position) -> {
                if (myTImages.size() > 0) {
                    showOrDelete(position);
                }
            }));

            LinearLayoutManager manager = new LinearLayoutManager(mThis);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(photoAdapter);
        } else
            try {
                throw new Exception("recyclerView can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /**
     * 拍照提示框
     * "imageType"用来区分多种不同类型的照片
     * 不填默认为"sqet";
     *
     * @param imageType
     */
    protected void showImageDialog(String imageType) {

        if (!TextUtils.isEmpty(imageType)) {
            this.imageType = imageType;
        }
        String[] items = {"从相册选择", "拍照"};
        new MaterialDialog.Builder(this)
                .items(items)// 列表数据
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        dialog.dismiss();
                        ImageHelper.of(getTakePhoto(), position);
                    }
                })
                .show();// 显示对话框
    }

    /**
     * 拍照提示框
     * "imageType"用来区分多种不同类型的照片
     * 不填默认为"sqet";
     */
    protected void showImageDialog() {
        String[] items = {"从相册选择", "拍照"};
        new MaterialDialog.Builder(this)
                .items(items)// 列表数据
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        dialog.dismiss();
                        ImageHelper.of(getTakePhoto(), position);
                    }
                })
                .show();// 显示对话框
    }

    //拍照提示框
    protected void showOrDelete(int itemId) {
        String[] items = {"查看图片", "删除图片"};
        new MaterialDialog.Builder(this)
                .title("请选择:")
                .items(items)// 列表数据
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        switch (position) {
                            case 0:
                                dialog.dismiss();
                                showImg(myTImages);
                                break;
                            case 1:
                                dialog.dismiss();
                                if (myTImages.size() > itemId) {
                                    myTImages.remove(itemId);
                                    photoAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mThis, "操作有误，无法删除！", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();// 显示对话框
    }


    /**
     * @param uploadUrl 服务器url
     * @param pathLocal 图片的本地地址
     * @param pathWeb   图片的服务器路径
     * @return
     */
    protected String uploadFile(String uploadUrl, String pathLocal, String pathWeb) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + pathLocal.substring(pathLocal.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(pathLocal);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            dos.close();
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    /**
     * 上传图片
     * 判断图片是否为空
     * 开启线程池
     */
    protected void commitPhotos() {
        if (poolExecutor == null) {
            poolExecutor = Executors.newCachedThreadPool();
        }
        poolExecutor.execute(new PhotoRunable(photoLocalPath, ""));
    }

    /**
     * 上传加水印后的图片
     */
    private class PhotoRunable implements Runnable {

        private String cachePath;
        private String webPath;

        public PhotoRunable(String cachePath, String webPath) {
            this.cachePath = cachePath;
            this.webPath = webPath;
        }

        @Override
        public void run() {
            String fileName = BitmapUtils.saveCompressPic(choosefile, cachePath);
            String result = uploadFile(actionUrl, fileName, webPath);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("data", result);
            bundle.putString("filename", fileName);
            message.setData(bundle);
            HandlerMes.sendMessage(message);
        }
    }


    //设置图片列表
    protected void setImageList(ArrayList<MyTImage> myTImages) {

    }

    //释放线程
    public void releaseThreadPool() {
        if (null != poolExecutor && !poolExecutor.isShutdown()) {
            poolExecutor.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseThreadPool();
    }
}
