package com.qihoo.ailab.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.qihoo.ailab.R;

public abstract class BaseActivity extends Activity {

    protected FrameLayout vContent;
    protected TextView vTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        vContent = findViewById(R.id.content_container);
        vContent.addView(getLayoutInflater().inflate(getContentLayoutId(), null));
        vTitle = findViewById(R.id.title);
        initView();
        setHeader();
        setData();
    }

    protected abstract void setData();

    protected abstract void setHeader();

    protected abstract void initView();

    protected abstract int getContentLayoutId();

    protected TextView getTitleView(){
        return vTitle;
    }

}
