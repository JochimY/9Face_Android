/**  
 * @Project: FaceBeautyDemo
 * @Title: Constants.java
 * @Package com.netease.facebeauty.common
 * @author Young
 * @date 2015年2月6日 上午10:58:58
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.common;

import java.io.File;

import com.netease.facebeauty.R;

import android.os.Environment;

/**
 * Constants
 * 
 * @author Young</br> 2015年2月6日 上午10:58:58
 */
public class Constants {
    // 在SDCard中的文件夹
    public static final String FOLDER_NAME = Environment.getExternalStorageDirectory() + File.separator
            + "NetEase Gallery" + File.separator + "Sticker";

    public static final String CACHE_FOLDER_NAME = Environment.getExternalStorageDirectory() + File.separator
            + "NetEase Gallery" + File.separator + "Sticker" + File.separator + "cache";

    public static final int MAX_SIZE = 500;

    public static final String TAG = "FaceBeauty";

    // 如果批量打包，该位设为true，否则设为false
    public static final boolean ANT_PACKAGE_FLAG = true;

    public static final String RELEASE_SIGNATURE = "MA-8DF6-85BE21AC401A";

    public static final String RELEASE_VERSION = "1.0.1";

    public static final String RELEASE_CHANNEL = "91手机助手";

    public static final int TEMPLATE_NUM = 8;

    public static final int[] drawableView = { R.drawable.sticker_view_word1, R.drawable.sticker_view_word2,
            R.drawable.sticker_view_word3, R.drawable.sticker_view_word4, R.drawable.sticker_view_word5,
            R.drawable.sticker_view_word6, R.drawable.sticker_view_word7, R.drawable.sticker_view_word8,
            R.drawable.sticker_view_word9, R.drawable.sticker_view_word10, R.drawable.sticker_view_word11,
            R.drawable.sticker_view_word12, R.drawable.sticker_view_word13, R.drawable.sticker_view_word14,
            R.drawable.sticker_view_word15, R.drawable.sticker_view_word16, R.drawable.sticker_view_word17,
            R.drawable.sticker_view_word18, R.drawable.sticker_view_word19, R.drawable.sticker_view_word20,
            R.drawable.sticker_view_word21, R.drawable.sticker_view_word22, R.drawable.sticker_view_word23,
            R.drawable.sticker_view_word24, R.drawable.sticker_view_word25, R.drawable.sticker_view_word26,
            R.drawable.sticker_view_word27, R.drawable.sticker_view_word28, R.drawable.sticker_view_word29,
            R.drawable.sticker_view_word30, R.drawable.sticker_view_word31, R.drawable.sticker_view_word32,
            R.drawable.sticker_view_word33, R.drawable.sticker_view_word34, R.drawable.sticker_view_word35 };

}
