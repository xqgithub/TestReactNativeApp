package com.first.testreactnativeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.explame.testreactnativeapp.R;
import com.first.testreactnativeapp.preloadreact.ReactNativePreLoader;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    public void initView() {
        mActivity = MainActivity.this;
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //哈哈
                intentToJump(mActivity, HelloReactActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
    }

    /**
     * 初始化数据
     */

    public void initData() {

    }

    /**
     * 跳转页面
     *
     * @param context
     * @param cls
     * @param flag
     */
    public void intentToJump(Context context, Class<?> cls, int flag) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(flag);
        context.startActivity(intent);
    }


    /**
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ReactNativePreLoader.preLoad(MainActivity.this, "helloreactnative");
        }
    }
}
