/**  
 * @Project: FaceBeautyDemo
 * @Title: FaceBeautyModelsAdapter.java
 * @Package com.netease.facebeauty.adapter
 * @author Young
 * @date 2015年2月9日 下午3:59:23
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.adapter;

import com.netease.facebeauty.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * FaceBeautyModelsAdapter
 * 
 * @author Young</br> 2015年2月9日 下午3:59:23
 */
public class FaceBeautyModelsAdapter extends BaseAdapter {

    private Context context;

    private int[] models = { R.drawable.sticker0_7, R.drawable.sticker0_6, R.drawable.sticker0_5,
            R.drawable.sticker0_4, R.drawable.sticker0_3, R.drawable.sticker0_2, R.drawable.sticker0_1,
            R.drawable.sticker0_0 };

    /**
     * @param context
     */
    public FaceBeautyModelsAdapter(Context context) {
        super();
        this.context = context;
    }

    /**
     * getCount
     * 
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return models.length;
    }

    /**
     * getItem
     * 
     * @param position
     * @return
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return models[position];
    }

    /**
     * getItemId
     * 
     * @param position
     * @return
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * getView
     * 
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.Adapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (null == convertView) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_face_beauty_models, null);
            holder.ivModels = (ImageView) convertView.findViewById(R.id.ivModels);
            holder.ivModels.setBackgroundResource(models[position]);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        return convertView;
    }

    class Holder {
        public ImageView ivModels;
    }

}
