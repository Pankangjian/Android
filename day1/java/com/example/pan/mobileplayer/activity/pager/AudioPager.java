package com.example.pan.mobileplayer.activity.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.pan.mobileplayer.activity.base.BasePager;
import com.example.pan.mobileplayer.activity.utils.LogUtil;

/**
 * Created by pan on 2018/9/9.
 */
public class AudioPager extends BasePager{
    private TextView textView;
    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地被初始化");
        textView =new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("数据被初始化");
        textView.setText("本地音乐页面");
    }
}
