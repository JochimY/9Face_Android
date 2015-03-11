/**  
 * @Project: FaceBeautyDemo
 * @Title: ImageUtils.java
 * @Package com.netease.facebeauty.utils
 * @author Young
 * @date 2015年2月6日 上午10:26:31
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.netease.facebeauty.bean.StickerParameters;
import com.netease.facebeauty.common.Constants;

/**
 * ImageUtils 对图像进行处理的工具类
 * 
 * @author Young</br> 2015年2月6日 上午10:26:31
 */
public class ImageUtils {

   
    /**
     * 将图片资源转成位图 convertDrawableToBitmap
     * 
     * @param @param context
     * @param @param drawableID
     * @param @return
     * @return Bitmap
     * @throws
     */
    public static Bitmap convertDrawableToBitmap(Context context, int drawableID) {

        Bitmap bitmap;

        bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID);

        return bitmap;

    }

    /**
     * 对完成的贴图进行裁剪，生成九宫格图片 ImageSplitter split
     * 
     * @param @param bitmap
     * @param @param xPiece
     * @param @param yPiece
     * @param @return
     * @return List<StickerParameters>
     * @throws
     */
    public static List<StickerParameters> split(Bitmap bitmap, int xPiece, int yPiece) {

        List<StickerParameters> pieces = new ArrayList<StickerParameters>(xPiece * yPiece);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / 3;
        int pieceHeight = height / 3;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                StickerParameters parameters = new StickerParameters();
                parameters.index = j + i * xPiece;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                parameters.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue, pieceWidth, pieceHeight);
                pieces.add(parameters);
            }
        }

        return pieces;
    }

    /**
     * 将已经处理后的View转成bitmap convertViewToBitmap
     * 
     * @param @param view
     * @param @return
     * @return Bitmap
     * @throws
     */
    public static Bitmap convertViewToBitmap(View view, int widthPixels, int heightPixels, int width, int height) {

        // 因为保存位图的时候只能保存从(0,0)坐标开始的view，所以要分两步。第一步，将包含屏幕大小的整个view保存下来转成位图
        Bitmap bitmapWhole = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWhole);
        view.draw(canvas);

        // 将全屏幕的位图保存以后，再对bitmap根据坐标进行截图，再生成位图
        Bitmap bitmapScreenshot = Bitmap.createBitmap(bitmapWhole, (widthPixels - width) / 2,
                (heightPixels - height) / 2, width, height);
        Canvas canvasScreenshot = new Canvas(bitmapScreenshot);
        view.draw(canvasScreenshot);

        return bitmapScreenshot;
    }

    /**
     * 将图片存储到SDcard新建的文件夹当中 storeBitmapIntoSDCard
     * 
     * @param @param name
     * @param @param bitmap
     * @return void
     * @throws
     */
    public static boolean storeBitmapIntoSDCard(String path, String name, Bitmap bitmap) {
        File f = new File(path + File.separator + name + ".jpeg");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        // resize to desired dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] newSize = getScaleSize(width, height);
        Bitmap workBitmap = Bitmap.createScaledBitmap(bitmap, newSize[0], newSize[1], true);
        if (workBitmap != bitmap) {
            bitmap.recycle();
            bitmap = workBitmap;
            System.gc();
        }
        return bitmap;
    }

    private static int[] getScaleSize(int width, int height) {
        float resizedWidth;
        float resizedHeight;
        boolean resizedAdjustWidth = width < height;
        if (resizedAdjustWidth) {
            resizedHeight = Constants.MAX_SIZE;
            resizedWidth = Math.round(((float) resizedHeight / height) * width);
        } else {
            resizedWidth = Constants.MAX_SIZE;
            resizedHeight = Math.round(((float) resizedWidth / width) * height);
        }
        return new int[] { Math.round(resizedWidth), Math.round(resizedHeight) };
    }

}
