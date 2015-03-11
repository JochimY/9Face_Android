/**  
 * @Project: FaceBeautyDemo
 * @Title: StickerParameters.java
 * @Package com.netease.facebeauty.bean
 * @author Young
 * @date 2015年2月3日 上午11:24:02
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.bean;

import android.graphics.Bitmap;

/**
 * StickerParameters
 * 
 * @author Young</br> 2015年2月3日 上午11:24:02
 */
public class StickerParameters {

    public int index = 0;

    public Bitmap bitmap = null;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
