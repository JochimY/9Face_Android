package com.netease.facebeauty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.netease.facebeauty.R;
import com.netease.facebeauty.common.Constants;

public class FaceBeautyApplication extends Application {

    public static Bitmap resultBitmap;

    public static final String APP_ID = "2882303761517307533";

    public static final String APP_KEY = "5601730790533";

    public static final String TAG = "com.netease.facebeauty";

    // private static FaceBeautyHandler handler = null;

    private static ArrayList<Bitmap> wordBitmaps = null;

    private boolean isUpdate = false;

    private boolean isUserChecked = false;

    private String apkUrl = null;

    private String apkMessage = null;

    public boolean isUserChecked() {
        return isUserChecked;
    }

    public void setUserChecked(boolean isUserChecked) {
        this.isUserChecked = isUserChecked;
    }

    public boolean checkVersion() {
        if (isUpdate && apkUrl != null) {
            return true;
        }
        return false;
    }

    public String getUpdateUrl() {
        return apkUrl;
    }

    public String getUpdateMessage() {
        return apkMessage;
    }

    private String getCurrentVersion() {
        try {
            String currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            return currentVersion;
        } catch (NameNotFoundException e) {
            if (null != e && null != e.getMessage()) {
                Log.e(Constants.TAG, e.getMessage());
            }
        }

        return new String();
    }

    // update realted
    private JSONObject getServerVersion() throws ClientProtocolException, IOException, JSONException {

        // modified by newtonker
        // 新的版本更新方式
        HttpPost post = new HttpPost(getResources().getString(R.string.serverurl));

        // 设置请求超时参数
        HttpParams httpParameters = new BasicHttpParams();
        // 设置请求超时5秒
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
        // 设置等待数据超时5秒
        HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000);
        // 此时构造DefaultHttpClient时将参数传入
        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpResponse response = httpClient.execute(post);

        if (200 == response.getStatusLine().getStatusCode()) {
            String result = EntityUtils.toString(response.getEntity());
            if (null == result) {
                return null;
            }

            JSONObject jsonObject = JSON.parseObject(result);
            if (null == jsonObject) {
                return null;
            }

            if (200 != jsonObject.getIntValue("code")) {
                return null;
            }

            return jsonObject;
        }
        return null;
    }

    private void checkVersionUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String currentVersion = getCurrentVersion();
                if (currentVersion.isEmpty()) {
                    return;
                }
                JSONObject serverResp = null;
                try {
                    serverResp = getServerVersion();
                } catch (ClientProtocolException e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                } catch (IOException e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                } catch (JSONException e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                }

                if (serverResp == null) {
                    return;
                }

                try {
                    // String serverVersion =
                    // serverResp.getJSONObject("response").getString("loftcamVersion");
                    // if(null == serverVersion || null == currentVersion)
                    // {
                    // return;
                    // }

                    // 版本更新新的解决方案
                    JSONObject responseObject = serverResp.getJSONObject("response");
                    String serverVersion = responseObject.getString("loftcamVersion");

                    // 将版本号分割，分段判断
                    String[] serverArray = serverVersion.split("\\.");
                    String[] currentArray = currentVersion.split("\\.");

                    int firstServerNum = Integer.parseInt(serverArray[0]);
                    int secondServerNum = Integer.parseInt(serverArray[1]);
                    int thirdServerNum = Integer.parseInt(serverArray[2]);

                    int firstCurrentNum = Integer.parseInt(currentArray[0]);
                    int secondCurrentNum = Integer.parseInt(currentArray[1]);
                    int thirdCurrentNum = Integer.parseInt(currentArray[2]);

                    if ((firstServerNum > firstCurrentNum)
                            || (firstServerNum == firstCurrentNum && secondServerNum > secondCurrentNum)
                            || (firstServerNum == firstCurrentNum && secondServerNum == secondCurrentNum && thirdServerNum > thirdCurrentNum)) {
                        apkUrl = responseObject.getString("loftcamUpdateUrl");
                        apkMessage = responseObject.getString("loftcamUpdateDesc").replace('|', '\n');
                        isUpdate = true;
                    } else {
                        return;
                    }
                } catch (JSONException e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                } catch (Exception e) {
                    if (null != e && null != e.getMessage()) {
                        Log.e(Constants.TAG, e.getMessage());
                    }
                }

            }

        }).start();
    }

    public static ArrayList<Bitmap> getWordBitmaps() {
        return wordBitmaps;
    }

    public static void setWordBitmaps(List<Bitmap> wordBitmaps2) {
        FaceBeautyApplication.wordBitmaps = (ArrayList<Bitmap>) wordBitmaps2;
    }

    public static Bitmap getResultBitmap() {
        return resultBitmap;
    }

    public static void setResultBitmap(Bitmap resultBitmap) {
        FaceBeautyApplication.resultBitmap = resultBitmap;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        checkVersionUpdate();

        File folder = new File(getApplicationContext().getFilesDir().getPath() + "/haarcascade");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String[] fileList = folder.list();
        if (fileList.length < 3) {
            try {
                String[] haarcascadeList = getApplicationContext().getAssets().list("haarcascade");
                for (int i = 0; i < haarcascadeList.length; i++) {
                    InputStream fin = getApplicationContext().getAssets().open("haarcascade/" + haarcascadeList[i]);
                    File outputFile = new File(getApplicationContext().getFilesDir().getPath() + "/haarcascade/"
                            + haarcascadeList[i]);
                    FileOutputStream fout = new FileOutputStream(outputFile);
                    int d;
                    byte[] b = new byte[8192];
                    while ((d = fin.read(b)) != -1) {
                        fout.write(b, 0, d);
                    }
                    fin.close();
                    fout.flush();
                    fout.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File templateFolder = new File(getApplicationContext().getFilesDir().getPath() + "/sticker");
        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
        }
        String[] templateList = templateFolder.list();
        if (templateList.length < 56) {
            try {
                String[] localTemplateList = getApplicationContext().getAssets().list("sticker");
                for (int i = 0; i < localTemplateList.length; i++) {
                    InputStream fin = getApplicationContext().getAssets().open("sticker/" + localTemplateList[i]);
                    File outputFile = new File(getApplicationContext().getFilesDir().getPath() + "/sticker/"
                            + localTemplateList[i]);
                    FileOutputStream fout = new FileOutputStream(outputFile);
                    int d;
                    byte[] b = new byte[8192];
                    while ((d = fin.read(b)) != -1) {
                        fout.write(b, 0, d);
                    }
                    fin.close();
                    fout.flush();
                    fout.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 初始化push推送服务
    // if (shouldInit()) {
    // MiPushClient.registerPush(this, APP_ID, APP_KEY);
    // }
    // 调试小米推送的日志
    // LoggerInterface newLogger = new LoggerInterface() {
    // @Override
    // public void setTag(String tag) {
    // // ignore
    // }
    //
    // @Override
    // public void log(String content, Throwable t) {
    // Log.d(TAG, content, t);
    // }
    //
    // @Override
    // public void log(String content) {
    // Log.d(TAG, content);
    // }
    // };
    // Logger.setLogger(this, newLogger);
    // if (handler == null)
    // handler = new FaceBeautyHandler(getApplicationContext());
    //
    // }
    //
    // private boolean shouldInit() {
    // ActivityManager am = ((ActivityManager)
    // getSystemService(Context.ACTIVITY_SERVICE));
    // List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
    // String mainProcessName = getPackageName();
    // int myPid = Process.myPid();
    // for (RunningAppProcessInfo info : processInfos) {
    // if (info.pid == myPid && mainProcessName.equals(info.processName)) {
    // return true;
    // }
    // }
    // return false;
    // }
    //
    // public static FaceBeautyHandler getHandler() {
    // return handler;
    // }
}
