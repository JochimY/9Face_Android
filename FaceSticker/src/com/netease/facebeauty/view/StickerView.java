/**  
 * @Project: FaceBeautyDemo
 * @Title: StickerView.java
 * @Package com.netease.facebeauty.utils
 * @author Young
 * @date 2015年2月3日 下午1:58:39
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.netease.facebeauty.FaceBeautyApplication;
import com.netease.facebeauty.R;
import com.netease.facebeauty.ui.FaceBeautyActivity;
import com.netease.facebeauty.utils.ImageUtils;

/**
 * StickerView
 * 
 * @author Young</br> 2015年2月3日 下午1:58:39
 */
public class StickerView extends View {

    private Bitmap bitmap;

    public int offsetX = 0;

    public int offsetY = 0;

    private int viewWidth, viewHeight;

    private Context context;

    /**
     * @param context
     * @param attrs
     */
    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerView(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
        this.context = context;
        viewWidth = bitmap.getWidth();
        viewHeight = bitmap.getHeight() * 2;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(viewWidth, viewHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (bitmap != null) {
            int picLeft = (FaceBeautyActivity.getWidthPixels() - bitmap.getWidth()) / 2;
            int picRight = (FaceBeautyActivity.getWidthPixels() + bitmap.getWidth()) / 2;
            int picTop = (FaceBeautyActivity.getHeightPixels() - bitmap.getHeight()) / 2;
            int picBottom = (FaceBeautyActivity.getHeightPixels() + bitmap.getHeight()) / 2;

            int whiteRecTop = FaceBeautyActivity.getHeightPixels() / 2 - bitmap.getHeight();
            int whiteRecBottom = FaceBeautyActivity.getHeightPixels() / 2 + bitmap.getHeight();

            // int wordRecTop = FaceBeautyActivity.getHeightPixels() / 2 - 3 *
            // bitmap.getHeight() / 4;

            // 画中间放置照片的矩形，背景为白色，居中
            Rect rectWhite = new Rect();
            rectWhite.set(picLeft, whiteRecTop, picRight, whiteRecBottom);
            paint.setColor(Color.WHITE);
            canvas.drawRect(rectWhite, paint);

            // 画布上部分的框,先将资源图片转成bitmap
            Bitmap bitmapTop = ImageUtils.convertDrawableToBitmap(context, R.drawable.sticker_view_top);

            Rect rectTop = new Rect();
            rectTop.set(picLeft, whiteRecTop, picRight, picTop);
            canvas.drawBitmap(bitmapTop, null, rectTop, paint);

            // String note = CommonUtils.getRandomNote(context);
            // 设置文字
            // paint.setColor(Color.BLACK);
            // paint.setTextSize(30);
            // paint.setTextAlign(Paint.Align.CENTER);
            // canvas.drawText(note, rectWhite.centerX(),
            // FaceBeautyActivity.getHeightPixels() / 2 - bitmap.getHeight(),
            // paint);

            int wordLeft = picLeft + (400 - 290) / 2;
            int wordTop = whiteRecTop + (200 - 90) / 2;
            // 设置文字图片
            Rect rectWord = new Rect();
            rectWord.set(wordLeft, wordTop, wordLeft + 290, wordTop + 90);

            List<Integer> randomCounts = new ArrayList<Integer>();
            int j = (int) (Math.random() * 35);
            if (!randomCounts.contains(j)) {
                canvas.drawBitmap(FaceBeautyApplication.getWordBitmaps().get(j), null, rectWord, paint);
                randomCounts.add(j);

            }

            canvas.drawBitmap(FaceBeautyApplication.getWordBitmaps().get(j), null, rectWord, paint);

            // 将图片放入白色画布中间位置
            Rect rectPic = new Rect();
            rectPic.set(picLeft, picTop, picRight, picBottom);
            canvas.drawBitmap(bitmap, null, rectPic, paint);

            // 画布上部分的框,先将资源图片转成bitmap
            Bitmap bitmapBottom = ImageUtils.convertDrawableToBitmap(context, R.drawable.sticker_view_bottom);

            Rect rectBottom = new Rect();
            rectBottom.set(picLeft, picBottom, picRight, whiteRecBottom);
            canvas.drawBitmap(bitmapBottom, null, rectBottom, paint);

        }
    }

}
