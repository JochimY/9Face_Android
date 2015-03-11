package com.netease.facebeauty.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.facebeauty.FaceBeautyApplication;
import com.netease.facebeauty.R;
import com.netease.facebeauty.common.Constants;
import com.netease.facebeauty.utils.FileUtils;
import com.netease.facebeauty.utils.ImageUtils;
import com.netease.facebeauty.utils.UpdateService;
import com.netease.facebeauty.utils.UriUtils;
import com.netease.mobidroid.DATracker;

public class MainActivity extends Activity {

    private ImageView galleryButton;

    private ImageView cameraButton;

    private static final int GALLARY = 1;// 从相册选取照片

    private static final int CAMERA = 2; // 拍照选取照片

    private String photoTakenName; // 用相机拍照后保存的照片文件名

    public static List<String> logList = new ArrayList<String>();

    public static MainActivity sMainActivity = null;

    public TextView logView = null;

    private FaceBeautyApplication appData;

    private ImageView ivAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        appData = (FaceBeautyApplication) getApplication();

        System.out.println("appdata.checkversion" + appData.checkVersion() + "ischecked" + !appData.isUserChecked());
        if (appData.checkVersion() && !appData.isUserChecked()) {
            appData.setUserChecked(true);
            showUpdateDialog();
        }

        sMainActivity = this;
        init();
        register();
    }

    private void init() {
        galleryButton = (ImageView) findViewById(R.id.pickImageBtn);
        cameraButton = (ImageView) findViewById(R.id.openCameraBtn);
        ivAbout = (ImageView) findViewById(R.id.ivAbout);
        FileUtils.createDirInSDCard(this);
        folderScan(Constants.FOLDER_NAME);

        saveHandler = new SaveBitmapListHandler();
        wordBitmaps = new ArrayList<Bitmap>();
        SaveBitmapListRunnable saveRunnable = new SaveBitmapListRunnable();
        new Thread(saveRunnable).start();

    }

    @Override
    protected void onPause() {
        DATracker.getInstance().close();
        super.onPause();
    }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        // logView.setText(AllLog);
    }

    private void startApkInstall() {
        Intent intent = new Intent(this, UpdateService.class);
        intent.putExtra("Key_App_Name", "LOFTCam");
        intent.putExtra("Key_Down_Url", appData.getUpdateUrl());
        this.startService(intent);
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(appData.getUpdateMessage());
        builder.setTitle(R.string.updatetitle);
        builder.setPositiveButton(R.string.doupdate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startApkInstall();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.notupdate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        refreshLogInfo();
        DATracker.getInstance().resume();
        super.onResume();

    }

    // public void refreshPushState(boolean run) {
    // if (run) {
    // findViewById(R.id.pause_push).setVisibility(View.VISIBLE);
    // findViewById(R.id.reset_container).setVisibility(View.GONE);
    // } else {
    // findViewById(R.id.pause_push).setVisibility(View.INVISIBLE);
    // findViewById(R.id.reset_container).setVisibility(View.VISIBLE);
    // }
    // }

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

    private void register() {
        galleryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, GALLARY);

            }
        });

        cameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                photoTakenName = "IMG" + System.currentTimeMillis() + ".jpeg";
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.FOLDER_NAME, photoTakenName)));
                startActivityForResult(intent, CAMERA);

            }
        });
        ivAbout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                // finish();

            }
        });
    }

    @SuppressLint("SdCardPath")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLARY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String uriStr = "file://" + UriUtils.getPath(getApplicationContext(), uri);
            Intent intent = new Intent(getApplicationContext(), FaceBeautyActivity.class);
            // 从4.4开始通过代码获取系统相册的Uri发生了很大变化，因此这里需要先判断一下系统的版本，再不同的处理方式
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
            Intent intent = new Intent(getApplicationContext(), FaceBeautyActivity.class);
            // intent.putExtra("uri", Uri.parse(fileName));
            intent.setData(Uri.parse(fileName));
            startActivity(intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private static Boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Timer tExit = null;
            if (isExit == false) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            } else {
                // 在Activity销毁的时候，批量删除cache文件夹下临时创建的图片文件
                File file = new File(Constants.CACHE_FOLDER_NAME);
                file.delete();
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    SaveBitmapListHandler saveHandler;

    @SuppressLint("HandlerLeak")
    public class SaveBitmapListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle b = msg.getData();
            b.getInt("result");
            wordBitmaps = b.getParcelableArrayList("bitmaps");
            FaceBeautyApplication.setWordBitmaps(wordBitmaps);
        }

    }

    private List<Bitmap> wordBitmaps = null;

    public static int wordBitmapWidth;

    public static int wordBitmapHeight;

    class SaveBitmapListRunnable implements Runnable {

        private List<Bitmap> bitmaps = null;

        /**
         * run
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            Bitmap wordBitmap = null;
            bitmaps = new ArrayList<Bitmap>();
            for (int i = 0; i < Constants.drawableView.length; i++) {
                wordBitmap = ImageUtils.convertDrawableToBitmap(MainActivity.this, Constants.drawableView[i]);
                bitmaps.add(wordBitmap);
            }
            if (null != wordBitmap) {
                wordBitmapWidth = wordBitmap.getWidth();
                wordBitmapHeight = wordBitmap.getHeight();
            } else {
                wordBitmapHeight = 200;
                wordBitmapWidth = 400;
            }
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putInt("result", 1);
            b.putParcelableArrayList("bitmaps", (ArrayList<? extends Parcelable>) bitmaps);
            msg.setData(b);

            MainActivity.this.saveHandler.sendMessage(msg);

        }

    }

}
