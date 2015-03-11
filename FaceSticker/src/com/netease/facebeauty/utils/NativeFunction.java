package com.netease.facebeauty.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.util.Log;

public class NativeFunction {

    // TAG for log.
    private static final String TAG = "NativeFunction";

    // Single Instance.
    private static NativeFunction instance = new NativeFunction();

    private int w;

    private int h;

    // Get instance.
    public static NativeFunction getInstance() {
        return instance;
    }

    public long init(Bitmap mBmp, String templatePath, float[] landmarks) {
        w = mBmp.getWidth();
        h = mBmp.getHeight();
        int[] pixels = new int[w * h];
        mBmp.getPixels(pixels, 0, w, 0, 0, w, h);
        long faceInfo = initLib(pixels, w, h, templatePath, landmarks);
        pixels = null;
        return faceInfo;
    }

    public Bitmap GetWarpedImg(Bitmap mBmp, int[] srcPts, int[] dstPts, int backgroundType) {
        Bitmap resultBmp = null;
        int w = mBmp.getWidth();
        int h = mBmp.getHeight();
        int[] pixels = new int[w * h];
        mBmp.getPixels(pixels, 0, w, 0, 0, w, h);

        int[] resultPixels = warpImg(w, h, pixels, srcPts, dstPts, backgroundType);
        if (resultPixels == null) {
            return null;
        } else {
            resultBmp = Bitmap.createBitmap(resultPixels, 1200, 1200, Config.ARGB_8888);
            return resultBmp;
        }
    }

    public Bitmap GetFaceMask(Bitmap mBmp, Rect roi, float[] landmarks, int maskType) {
        Bitmap resultBmp = null;
        int w = mBmp.getWidth();
        int h = mBmp.getHeight();

        int[] roiRect = new int[] { roi.left, roi.top, roi.width(), roi.height() };

        int[] resultPixels = getMask(w, h, roiRect, landmarks, maskType);
        if (resultPixels == null) {
            return null;
        } else {
            resultBmp = Bitmap.createBitmap(resultPixels, roi.width(), roi.height(), Config.ARGB_8888);
            return resultBmp;
        }
    }

    public Bitmap GetFaceSketch(Bitmap mBmp, int[] roiRect) {
        Bitmap resultBmp = null;
        int w = mBmp.getWidth();
        int h = mBmp.getHeight();
        int[] pixels = new int[w * h];
        mBmp.getPixels(pixels, 0, w, 0, 0, w, h);

        int[] resultPixels = getSketch(w, h, pixels, roiRect);
        if (resultPixels == null) {
            return null;
        } else {
            resultBmp = Bitmap.createBitmap(resultPixels, roiRect[2], roiRect[3], Config.ARGB_8888);
            Log.i("WEILI","width222_"+String.valueOf(roiRect[2])+" height_"+String.valueOf(roiRect[3]));
            Log.i("WEILI","width333_"+String.valueOf(resultBmp.getWidth())+" height_"+String.valueOf(resultBmp.getHeight()));
            
            pixels=null;
            return resultBmp;
        }
    }

    public Bitmap GetExpression(Long faceInfo, Bitmap warpedFace, Bitmap warpedMask, int nStickerType, int nStickerIdx) {
        Bitmap resultBmp = null;
        int w = 1200;
        int h = 1200;
        int[] facePixels = new int[w * h];
        int[] maskPixels = new int[w * h];
        warpedFace.getPixels(facePixels, 0, w, 0, 0, w, h);
        warpedMask.getPixels(maskPixels, 0, w, 0, 0, w, h);

        int[] resultPixels = expressionProcess(faceInfo, w, h, facePixels, maskPixels, nStickerType, nStickerIdx);
        if (resultPixels == null) {
            return null;
        } else {
            resultBmp = Bitmap.createBitmap(resultPixels, w, h, Config.ARGB_8888);
            return resultBmp;
        }
    }

    public Bitmap process(long faceInfo, Bitmap mBmp, String param) {
        int[] pixels = new int[w * h];
        mBmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int result = processImage(faceInfo, pixels, w, h, param);
        if (result == 0) {
            pixels = null;
            return null;
        } else {
            Bitmap resultBmp = Bitmap.createBitmap(pixels, w, h, Config.ARGB_8888);
            pixels = null;
            return resultBmp;
        }
    }

    public Bitmap processSticker(long faceInfo, Bitmap mBmp,Bitmap sketchBmp, int nStickerType, int nStickerIdx, int maskType) {
        int[] pixels = new int[w * h];
        mBmp.getPixels(pixels, 0, w, 0, 0, w, h);
        
        int sw = sketchBmp.getWidth();
        int sh = sketchBmp.getHeight();
        int[] sketchPixels = new int[sw*sh];
        sketchBmp.getPixels(sketchPixels,0,sw,0,0,sw,sh);
        int[] result = processSticker(faceInfo, pixels, w, h,sketchPixels,sw,sh, nStickerType, nStickerIdx, maskType);
        if (result == null) {
            pixels = null;
            return null;
        } else {
            Bitmap resultBmp = Bitmap.createBitmap(result, 1200, 1200, Config.ARGB_8888);
            pixels = null;
            return resultBmp;
        }
    }

    public static void recycle_bitmap(Bitmap mBmp) {
        if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
            mBmp = null;
        }
    }

    private native int[] getSketch(int w, int h, int[] pixels, int[] roi);

    private native int[] getMask(int w, int h, int[] roi, float[] landmarks, int maskType);

    private native int[] expressionProcess(long faceInfo, int w, int h, int[] facePixels, int[] maskPixels,
            int nStickerType, int nStickerIdx);

    private native int[] warpImg(int w, int h, int[] pixels, int[] srcPts, int[] dstPts, int backgroundType);

    public native int getLandmarks(float[] landmarks, String paramPath, String inputPath);

    public native long initLib(int[] pixels, int w, int h, String templatePath, float[] landmarks);

    public native int processImage(long faceInfo, int[] pixels, int w, int h, String param);

    public native int[] processSticker(long faceInfo, int[] pixels, int w, int h,
    		int[] sketchPixels,int sw,int sh, int nStickerType, int nStickerIdx,
            int maskType);

    public native int freeLib(long faceInfo);

    static {
        System.loadLibrary("FaceBeauty");
    }
}
