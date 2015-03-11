/**  
 * @Project: FaceBeautyDemo
 * @Title: SaveActivity.java
 * @Package com.netease.facebeauty.ui
 * @author Young
 * @date 2015年2月6日 下午2:02:50
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.ui;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.netease.facebeauty.FaceBeautyApplication;
import com.netease.facebeauty.R;
import com.netease.facebeauty.common.Constants;
import com.netease.facebeauty.utils.FileUtils;
import com.netease.mobidroid.DATracker;

/**
 * SaveActivity
 * 
 * @author Young</br> 2015年2月6日 下午2:02:50
 */
public class SaveActivity extends Activity implements OnClickListener {

    private LinearLayout llBack;

    private Button btnHomePage;

    private ImageView ivWeChat;

    private ImageView ivWeibo;

    private ImageView ivDemoShow;

    private Bitmap bitmap;

    private ImageView ivYiXin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

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

        llBack = (LinearLayout) findViewById(R.id.common_title_back);
        btnHomePage = (Button) findViewById(R.id.common_title_setting);
        ivWeChat = (ImageView) findViewById(R.id.ivWeChat);
        ivWeibo = (ImageView) findViewById(R.id.ivWeibo);
        ivYiXin = (ImageView) findViewById(R.id.ivYiXin);
        ivDemoShow = (ImageView) findViewById(R.id.ivHintShow);
        btnHomePage.setVisibility(View.VISIBLE);
        init();

    }

    private void init() {
        bitmap = FaceBeautyApplication.getResultBitmap();
        ivDemoShow.setImageBitmap(bitmap);

        llBack.setOnClickListener(this);
        btnHomePage.setOnClickListener(this);
        ivWeChat.setOnClickListener(this);
        ivWeibo.setOnClickListener(this);
        ivYiXin.setOnClickListener(this);
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

    /**
     * onClick
     * 
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (v == llBack) {
            // 考虑到用户点击返回键时还会回到图片处理的地方，因此这里不删除创建在cache中的图片
            finish();
            return;
        }
        if (v == btnHomePage) {
            FaceBeautyActivity.instance.finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            // 在Activity销毁的时候，批量删除cache文件夹下临时创建的图片文件
            File file = new File(Constants.CACHE_FOLDER_NAME);
            FileUtils.deleteFiles(file);
            finish();
            return;
        }
        if (v == ivWeChat) {
            if (checkInstall("com.tencent.mm")) {

                DATracker.getInstance().trackEvent("分享给微信");

                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivityForResult(intent, WECHAT);
                // 在Activity销毁的时候，批量删除cache文件夹下临时创建的图片文件
                File file = new File(Constants.CACHE_FOLDER_NAME);
                FileUtils.deleteFiles(file);
                // finish();
            } else {
                Toast.makeText(getApplicationContext(), "您还尚未安装微信哦", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (v == ivWeibo) {
            if (checkInstall("com.sina.weibo")) {

                DATracker.getInstance().trackEvent("分享给微博");
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.sina.weibo", "com.sina.weibo.EditActivity");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivityForResult(intent, WEIBO);
                // 在Activity销毁的时候，批量删除cache文件夹下临时创建的图片文件
                File file = new File(Constants.CACHE_FOLDER_NAME);
                FileUtils.deleteFiles(file);
                // finish();
            } else {
                Toast.makeText(getApplicationContext(), "您还尚未安装微博哦", Toast.LENGTH_SHORT).show();

            }
            return;
        }

        if (v == ivYiXin) {

            if (checkInstall("im.yixin")) {
                DATracker.getInstance().trackEvent("分享给微博");
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("im.yixin", "im.yixin.activity.WelcomeActivity");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivityForResult(intent, YIXIN);
                // finish();
            } else {
                Toast.makeText(getApplicationContext(), "您还尚未安装易信哦", Toast.LENGTH_SHORT).show();

            }
            return;
        }

    }

    /**
     * 判断第三方应用是否安装的方法 checkInstall
     * 
     * @param @param packageName
     * @param @return
     * @return boolean
     * @throws
     */
    private boolean checkInstall(String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean backFlag = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backFlag) {
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int WECHAT = 0;

    private static final int WEIBO = 1;

    private static final int YIXIN = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

}
