package com.netease.facebeauty.ui;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.facebeauty.FaceBeautyApplication;
import com.netease.facebeauty.R;
import com.netease.facebeauty.adapter.FaceBeautyModelsAdapter;
import com.netease.facebeauty.bean.StickerParameters;
import com.netease.facebeauty.common.Constants;
import com.netease.facebeauty.utils.FileUtils;
import com.netease.facebeauty.utils.ImageUtils;
import com.netease.facebeauty.utils.NativeFunction;
import com.netease.facebeauty.utils.UriUtils;
import com.netease.facebeauty.view.StickerView;
import com.netease.mobidroid.DATracker;

public class FaceBeautyActivity extends Activity implements OnClickListener, OnItemClickListener {

    private static final String TAG = "FaceBeautyActivity";

    private static int widthPixels;

    private static int heightPixels;

    private LinearLayout btnBack;

    private ImageView showView;

    private long faceInfo;

    private Bitmap srcBmp;

    private Bitmap desBmp;

    private Bitmap sketchBmp;

    private boolean backFlag = false;

    private int rawWidth = -1;

    private ImageView btnSave;

    // 图片制作过程当中的pd
    private ProgressDialog pdExcuting;

    // 图片保存过程中的pd
    private ProgressDialog pdSaving;

    // 保存切割后的九图的ArrayList
    private List<StickerParameters> pieces = null;

    // 处理图片的线程
    private DealPicHandler dealHandler;

    // 保存图片的线程
    private SavingHandler savingHandler;

    private int clickCode = Constants.TEMPLATE_NUM - 1; // 点击模板以后的代码，0-效果1，1-效果2，2-效果3

    private Bitmap resultSticker;

    private static final int CAMERA = 0;

    private static final int GALLERY = 1;

    public static final String PASS_BITMAP = "BITMAP";

    // 进行人脸识别的两个重要路径参数
    private String paramPath, inputPath;

    // 用相机拍照获取的照片名称
    private String photoTakenName;

    // 放置模板的GridView
    private GridView gvModels;

    private FaceBeautyModelsAdapter modelAdapter;

    private TextView tvSave;

    public static FaceBeautyActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 对Activity埋点追踪
        if (Constants.ANT_PACKAGE_FLAG) {
            try {
                ApplicationInfo info = this.getPackageManager().getApplicationInfo(getPackageName(),
                        PackageManager.GET_META_DATA);
                String releaseChannel = info.metaData.getString("RELEASE_CHANEL");
                DATracker.enableTracker(this, Constants.RELEASE_SIGNATURE, Constants.RELEASE_VERSION, releaseChannel);
            } catch (NameNotFoundException e) {
                if (null != e && null != e.getMessage()) {
                    Log.e(Constants.TAG, e.getMessage());
                }
            }
        } else {
            DATracker.enableTracker(this, Constants.RELEASE_SIGNATURE, Constants.RELEASE_VERSION,
                    Constants.RELEASE_CHANNEL);
        }

        setContentView(R.layout.activity_face_beauty);

        instance = this;
        // 获取设备屏幕的宽和高的像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;

        showView = (ImageView) findViewById(R.id.face_iv);
        btnBack = (LinearLayout) findViewById(R.id.common_title_back);
        btnSave = (ImageView) findViewById(R.id.btnSave);
        gvModels = (GridView) findViewById(R.id.gvModels);
        tvSave = (TextView) findViewById(R.id.tvSave);

        // 设置“保存/分享”字体的大小，将px转成sp
        float fontScale = this.getResources().getDisplayMetrics().scaledDensity;
        tvSave.setTextSize(24 / fontScale + 0.5f);

        // 初始化方法
        init();

    }

    @Override
    protected void onPause() {
        DATracker.getInstance().close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        DATracker.getInstance().resume();
        super.onResume();
    }

    // 返回屏幕宽度
    public static int getWidthPixels() {
        return widthPixels;
    }

    // 返回屏幕高度
    public static int getHeightPixels() {
        return heightPixels;
    }

    float[] resultFloat;

    int result;

    @SuppressLint("SdCardPath")
    private int init() {

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        /** 设置GridView ******************************************************/
        // 得到像素密度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        float density = outMetrics.density; // 像素密度

        modelAdapter = new FaceBeautyModelsAdapter(getApplicationContext());

        // 根据item的数目，动态设定gv的宽度,现假定每个item的宽度和高度均为100dp，列间距为5dp
        ViewGroup.LayoutParams params = gvModels.getLayoutParams();
        int itemWidth = (int) (55 * density);
        int spacingWidth = (int) (20 * density);

        params.width = itemWidth * modelAdapter.getCount() + (modelAdapter.getCount()) * spacingWidth - spacingWidth
                / 2;
        params.height = (int) (65 * density);
        gvModels.setStretchMode(GridView.NO_STRETCH); // 设置为禁止拉伸模式
        gvModels.setNumColumns(modelAdapter.getCount());
        gvModels.setHorizontalSpacing(spacingWidth);
        gvModels.setColumnWidth(itemWidth);
        gvModels.setLayoutParams(params);
        gvModels.setGravity(Gravity.CENTER);
        gvModels.setAdapter(modelAdapter);

        gvModels.setOnItemClickListener(this);

        pdExcuting = new ProgressDialog(this);
        pdExcuting.setMessage("贴纸制作中，请稍后...");
        pdExcuting.setCancelable(false);
        pdExcuting.show();

        pdSaving = new ProgressDialog(this);
        pdSaving.setMessage("保存进行中...");
        pdSaving.setCancelable(false);

        paramPath = getApplicationContext().getFilesDir().getPath() + "/haarcascade";
        // inputPath = getRealPathFromURI((Uri)
        // getIntent().getExtras().getParcelable("uri"));
        inputPath = getRealPathFromURI(getIntent().getData());

        // 开启处理图片的子线程
        dealHandler = new DealPicHandler();
        DealPicThreadInit dealThreadInit = new DealPicThreadInit();
        new Thread(dealThreadInit).start();

        return 1;
    }

    /**
     * 第一次处理图片的线程 DealPicThreadInit
     * 
     * @author Young</br> 2015年2月4日 上午9:51:24
     */
    class DealPicThreadInit implements Runnable {

        /**
         * run
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            // 人脸识别
            resultFloat = new float[154];
            result = NativeFunction.getInstance().getLandmarks(resultFloat, paramPath, inputPath);

            if (result != 0) {
                // 图片压缩
                srcBmp = loadResizedImage();
                float rate = (float) srcBmp.getWidth() / rawWidth;
                if (rate != 1) {
                    for (int i = 0; i < resultFloat.length; i++) {
                        resultFloat[i] *= rate;
                    }
                }

                // 贴纸初始化
                faceInfo = NativeFunction.getInstance().init(srcBmp, getApplicationContext().getFilesDir().getPath(),
                        resultFloat);
                int[] roiRect = new int[4];
                roiRect[0] = (int) resultFloat[0];
                roiRect[1] = (int) resultFloat[29];
                roiRect[2] = (int) (resultFloat[24] - resultFloat[0]);
                roiRect[3] = (int) (resultFloat[13] - resultFloat[29]);

                sketchBmp = NativeFunction.getInstance().GetFaceSketch(srcBmp, roiRect);
            }
            Message msg = new Message();
            Bundle b = new Bundle();
            // 当result=0时代表识别失败，不能继续进行图像的合成操作
            if (result == 0) {
                b.putInt("DETECTION_RESULT", result);
            } else {

                // 每次执行一次
                resultSticker = NativeFunction.getInstance().processSticker(faceInfo, srcBmp, sketchBmp, 0, clickCode,
                        0);
                b.putParcelable("resultBitmap", resultSticker);
                b.putInt("DETECTION_RESULT", 1);
            }
            msg.setData(b);

            FaceBeautyActivity.this.dealHandler.sendMessage(msg);

        }

    }

    /**
     * 处理图片的线程 DealPicThread
     * 
     * @author Young</br> 2015年2月4日 上午9:51:24
     */
    class DealPicThread implements Runnable {

        /**
         * run
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            Message msg = new Message();
            Bundle b = new Bundle();
            // 当result=0时代表识别失败，不能继续进行图像的合成操作
            if (result == 0) {
                b.putInt("DETECTION_RESULT", result);
            } else {

                // 每次执行一次
                resultSticker = NativeFunction.getInstance().processSticker(faceInfo, srcBmp, sketchBmp, 0, clickCode,
                        0);
                b.putParcelable("resultBitmap", resultSticker);
                b.putInt("DETECTION_RESULT", 1);
            }
            msg.setData(b);

            FaceBeautyActivity.this.dealHandler.sendMessage(msg);

        }

    }

    private AlertDialog alert;

    /**
     * 接受从DealPicThread线程中传过来的bitmap，并展示 DealPicHandler
     * 
     * @author Young</br> 2015年2月4日 上午9:50:19
     */
    @SuppressLint("HandlerLeak")
    class DealPicHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle b = msg.getData();
            Bitmap bitmap;

            // 接受来自DealPicThread的值，如果result值为0，则提示没有检测到人脸；检测到了则显示制作出来的图片
            if (b.getInt("DETECTION_RESULT") == 0) {
                pdExcuting.dismiss();

                alert = new AlertDialog.Builder(FaceBeautyActivity.this).create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
                // 当用户点击返回时alert消失并返回首页
                alert.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
                Window window = alert.getWindow();
                window.setContentView(R.layout.face_beauty_failed_alert);
                TextView tvChoosePhotoAgain = (TextView) window.findViewById(R.id.tvChoosePhotoAgain);
                TextView tvTakePhotoAgain = (TextView) window.findViewById(R.id.tvTakePhotoAgain);
                tvChoosePhotoAgain.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent();
                        intent1.setType("image/*");
                        intent1.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent1, GALLERY);

                    }
                });
                tvTakePhotoAgain.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        photoTakenName = "IMG" + System.currentTimeMillis() + ".jpeg";
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Constants.FOLDER_NAME, photoTakenName)));
                        startActivityForResult(intent, CAMERA);

                    }
                });

                return;
            } else {
                // 制作图片和切成九宫格形式的图片展示效果
                bitmap = b.getParcelable("resultBitmap");
                Toast.makeText(getApplicationContext(), "制作成功", Toast.LENGTH_LONG).show();
                showView.setImageBitmap(bitmap);

                // 将图片保存到cache文件夹下，并以clickCode命名
                ImageUtils.storeBitmapIntoSDCard(Constants.CACHE_FOLDER_NAME, clickCode + "", bitmap);
                Log.d(TAG, "图片" + clickCode + "保存成功");

            }
            pdExcuting.dismiss();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 当获取图片的Uri后，需要重新进入该Activity，刷新获取到的数据
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String uriStr = "file://" + UriUtils.getPath(getApplicationContext(), uri);
            Intent intent = new Intent(getApplicationContext(), FaceBeautyActivity.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent.setData(Uri.parse(uriStr));
            } else {
                intent.setData(uri);
            }
            startActivity(intent);
            finish();

        }

        else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            // 判断存储卡是否可用
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.i("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            // 获取文件所在路径，并转成Uri传给下个Activity
            String fileName = "file://" + Constants.FOLDER_NAME + File.separator + photoTakenName;
            // 获取文件所在路径，并转成Uri传给下个Activity
            Intent intent = new Intent(getApplicationContext(), FaceBeautyActivity.class);
            intent.setData(Uri.parse(fileName));
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    // 扫描某个文件
    public void fileScan(String file) {
        Uri data = Uri.parse("file://" + file);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    // 扫描某个文件夹
    public void folderScan(String folderPath) {
        File file = new File(folderPath);
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                File f = fileArray[i];
                if (f.isFile()) {
                    // 将获取到的文件名转成小写字母用于后续判断
                    String name = f.getName().toLowerCase(Locale.ENGLISH);
                    if (name.contains(".jpeg") || name.contains(".jpg") || name.contains(".png")) {
                        fileScan(f.getAbsolutePath());
                    }
                } else {
                    folderScan(f.getAbsolutePath());
                }
            }
        }
    }

    // 载入已经调整过大小的图片
    private Bitmap loadResizedImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decode(options);
        rawWidth = options.outWidth;
        int scale = 1;
        while ((options.outWidth / scale >= Constants.MAX_SIZE) || (options.outHeight / scale >= Constants.MAX_SIZE)) {
            scale++;
        }
        scale--;
        if (scale < 1) {
            scale = 1;
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = decode(options);
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() > Constants.MAX_SIZE || bitmap.getHeight() > Constants.MAX_SIZE) {
            // 如果不符合大小则重新调整
            bitmap = ImageUtils.scaleBitmap(bitmap);
        }
        return bitmap;
    }

    // 拿到从MainActivity传过来的图片uri
    private Bitmap decode(BitmapFactory.Options options) {
        try {
            InputStream inputStream;
            inputStream = this.getContentResolver().openInputStream(getIntent().getData());
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backFlag) {
                return true;
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getRealPathFromURI(Uri contentUri) {

        String uriString = contentUri.toString();

        if (uriString.contains("file:///"))
            return contentUri.getPath();

        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * onClick
     * 
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        if (v == btnBack) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
            return;
        }
        // 点击保存按钮事件，将贴图进行切割并显示
        if (v == btnSave) {

            pdSaving.show();
            if (null != resultSticker) {
                savingHandler = new SavingHandler();
                SavingRunnable savingRunnable = new SavingRunnable();
                new Thread(savingRunnable).start();
            } else {
                pdSaving.dismiss();
                btnSave.setClickable(false);
                Toast.makeText(getApplicationContext(), "抱歉，未能识别照片，请重新选择", Toast.LENGTH_SHORT).show();
            }
            return;

        }

    }

    /**
     * onItemClick 模板点击事件
     * 
     * @param parent
     * @param view
     * @param position
     * @param id
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     *      android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (clickCode != Constants.TEMPLATE_NUM - position - 1) {
            clickCode = Constants.TEMPLATE_NUM - position - 1;
            if (FileUtils.isFileExists(clickCode)) {
                resultSticker = BitmapFactory.decodeFile(Constants.CACHE_FOLDER_NAME + "/" + clickCode + ".jpeg");
                showView.setImageBitmap(resultSticker);
            } else {
                pdExcuting.show();
                dealHandler = new DealPicHandler();
                DealPicThread dealThread = new DealPicThread();
                new Thread(dealThread).start();
            }
        } else {
            return;
        }
    }

    // }

    @SuppressLint("HandlerLeak")
    class SavingRunnable implements Runnable {

        /**
         * run
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            Message msg = new Message();
            Bundle b = new Bundle();
            // 对选中的图进行截图
            pieces = ImageUtils.split(resultSticker, 3, 3);

            // 按时间倒叙来，首先保存最后一张引导图
            Bitmap guideLastPic = ImageUtils.convertDrawableToBitmap(getApplicationContext(),
                    R.drawable.face_beauty_guide_last);
            ImageUtils.storeBitmapIntoSDCard(Constants.FOLDER_NAME, "NetEase" + System.currentTimeMillis(),
                    guideLastPic);

            // 循环取出ArrayList里面的图片，并用StickerView类对bitmap重新处理
            for (int i = pieces.size() - 1; i >= 0; i--) {
                Calendar c = Calendar.getInstance();
                int mm = c.get(Calendar.MILLISECOND);
                String imgSplitedName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))
                        + Integer.toString(mm);
                StickerView view = new StickerView(getApplicationContext(), pieces.get(i).getBitmap());

                // 因为保存位图的时候只能保存从(0,0)坐标开始的view，所以要分两步。第一步，将包含屏幕大小的整个view保存下来转成位图
                Bitmap bitmapWhole = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapWhole);
                view.draw(canvas);

                // 将全屏幕的位图保存以后，再对bitmap根据坐标进行截图生成最后的bitmap
                Bitmap bitmapScreenshot = Bitmap.createBitmap(bitmapWhole, (widthPixels - pieces.get(i).getBitmap()
                        .getWidth()) / 2, (heightPixels - pieces.get(i).getBitmap().getHeight() * 2) / 2, pieces.get(i)
                        .getBitmap().getWidth(), pieces.get(i).getBitmap().getHeight() * 2);

                // 保存图片到SDCard中
                ImageUtils.storeBitmapIntoSDCard(Constants.FOLDER_NAME, imgSplitedName, bitmapScreenshot);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 循环保存九图结束以后保存第一张引导图
            Bitmap guideFirstPic = ImageUtils.convertDrawableToBitmap(getApplicationContext(),
                    R.drawable.face_beauty_guide_first);
            ImageUtils.storeBitmapIntoSDCard(Constants.FOLDER_NAME, "NetEase" + System.currentTimeMillis(),
                    guideFirstPic);

            pdSaving.dismiss();
            // 跳转到保存页面
            Intent intentToSave = new Intent(getApplicationContext(), SaveActivity.class);
            // 将最后点击的图片作为全局变量在保存页面显示
            Bitmap bitmapCache = BitmapFactory.decodeFile(Constants.CACHE_FOLDER_NAME + "/" + clickCode + ".jpeg");
            FaceBeautyApplication.setResultBitmap(bitmapCache);

            startActivity(intentToSave);
            // 把切图保存到SDCard以后再对系统相册进行一遍刷新
            folderScan(Constants.FOLDER_NAME);

            b.putInt("RESULT", 1);
            msg.setData(b);
            FaceBeautyActivity.this.savingHandler.sendMessage(msg);

        }
    }

    @SuppressLint("HandlerLeak")
    class SavingHandler extends Handler {

        /**
         * handleMessage
         * 
         * @param msg
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle b = msg.getData();
            if (b.getInt("RESULT") == 1) {
                // 在Activity销毁的时候，批量删除cache文件夹下临时创建的图片文件
                File file = new File(Constants.CACHE_FOLDER_NAME);
                file.delete();
                return;
            }

        }

    }

    @Override
    protected void onDestroy() {
        int result = NativeFunction.getInstance().freeLib(faceInfo);
        if (result == 0) {
            Log.e(TAG, "free error");
        }
        NativeFunction.recycle_bitmap(srcBmp);
        NativeFunction.recycle_bitmap(desBmp);

        File file = new File(Constants.CACHE_FOLDER_NAME);
        FileUtils.deleteFiles(file);

        super.onDestroy();
    }

}
