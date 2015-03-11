/**  
 * @Project: NineFace
 * @Title: AboutActivity.java
 * @Package com.netease.facebeauty.ui
 * @author Young
 * @date 2015年3月4日 下午3:10:46
 * @version V1.0  
 * 版权所有      
 */
package com.netease.facebeauty.ui;

import com.netease.facebeauty.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * AboutActivity
 * 
 * @author Young</br> 2015年3月4日 下午3:10:46
 */
public class AboutActivity extends Activity implements OnClickListener {

    private LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        llBack = (LinearLayout) findViewById(R.id.common_title_back);
        init();

    }

    /**
     * init
     * 
     * @param
     * @return void
     * @throws
     */
    private void init() {
        llBack.setOnClickListener(this);

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
            finish();
            return;
        }

    }

}
