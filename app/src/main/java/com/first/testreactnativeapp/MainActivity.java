package com.first.testreactnativeapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.explame.testreactnativeapp.R;
import com.first.testreactnativeapp.preloadreact.ReactNativePreLoader;
import com.first.testreactnativeapp.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Button btn1;
    private Button btn2;

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
        btn2 = (Button) findViewById(R.id.btn2);
        //跳转到RN页面
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToJump(mActivity, HelloReactActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });

        //想RN发送消息
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getReactPackage().mModule.nativeCallRn("路飞是海贼王");
            }
        });
    }

    /**
     * 初始化数据
     */

    public void initData() {
        doPermissionApplication(mActivity, permissions[CALL_PHONE], CALL_PHONE);
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

    /**
     * 申请权限
     */
    public String[] permissions = {Manifest.permission.CALL_PHONE};
    public static final int CALL_PHONE = 0;

    public boolean doPermissionApplication(Activity mactivity, String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(mactivity, permission)
                != PackageManager.PERMISSION_GRANTED) {//　没有该权限
            ActivityCompat.requestPermissions(mactivity, new String[]{permission}, REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 通过
                    LogUtils.i("----->权限被通过了");
                } else {
                    // Permission Denied   被拒绝
                    LogUtils.i("----->权限被拒绝了");
                }
                break;
        }
    }

}
