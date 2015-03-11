package com.netease.facebeauty.utils;

import java.io.File;

import com.netease.facebeauty.common.Constants;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class FileUtils {
    public static File updateDir = null;

    public static File updateFile = null;

    public static boolean isCreateFileSucess;

    /**
     * 创建文件
     * 
     * @param String
     *            app_name
     * @return
     * @see FileUtils
     */
    public static void createFile(String app_name) {
        // modified by newtonker
        // 首先判断是否已挂载SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            updateDir = new File(Environment.getExternalStorageDirectory(), "9FaceDownload");
            if (!updateDir.exists()) {
                if (!updateDir.mkdirs()) {
                    isCreateFileSucess = false;
                    return;
                }
            }
            updateFile = new File(updateDir.getPath() + File.separator + app_name + ".apk");
            if (null == updateFile) {
                isCreateFileSucess = false;
            } else {
                isCreateFileSucess = true;
            }
        } else {
            isCreateFileSucess = false;
        }
    }

    /**
     * 在SDCard中新建一个文件夹 createDirInSDCard
     * 
     * @param
     * @return void
     * @throws
     */
    public static void createDirInSDCard(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File folder = new File(Constants.CACHE_FOLDER_NAME);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } else {
            Toast.makeText(context, "SDCard不存在", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 判断文件是否存在 isFileExists
     * 
     * @param @param fileName
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isFileExists(int fileName) {
        try {
            File file = new File(Constants.CACHE_FOLDER_NAME + "/" + fileName + ".jpeg");
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;

    }

    /**
     * 删除指定文件 deleteFiles
     * 
     * @param @param filePath
     * @return void
     * @throws
     */
    public static void deleteFiles(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File listFile : files) {
                deleteFiles(listFile);
                listFile.delete();
            }
        } else {
            file.delete();
        }

    }

}
