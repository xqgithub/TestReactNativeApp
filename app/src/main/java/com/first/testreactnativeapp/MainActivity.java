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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.explame.testreactnativeapp.R;
import com.first.testreactnativeapp.constants.FileConstant;
import com.first.testreactnativeapp.hotupdate.HotUpdate;
import com.first.testreactnativeapp.http.ApiService;
import com.first.testreactnativeapp.http.RetrofitServiceManager;
import com.first.testreactnativeapp.preloadreact.ReactNativePreLoader;
import com.first.testreactnativeapp.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Button btn1;//跳转到HelloReactActivity类
    private Button btn2;//向RN发送消息
    private Button btn3;//更新最新的JsBundle文件

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
        btn3 = (Button) findViewById(R.id.btn3);
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
        //更新JsBundle文件
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
    }

    /**
     * 初始化数据
     */

    public void initData() {
        /**
         * 申请权限
         */
        doPermissionApplication(mActivity, permissions[CALL_PHONE], CALL_PHONE);
        doPermissionApplication(mActivity, permissions[WRITE_EXTERNAL_STORAGE], WRITE_EXTERNAL_STORAGE);
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
    public String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int CALL_PHONE = 0;
    public static final int WRITE_EXTERNAL_STORAGE = 1;

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
                    LogUtils.i("CALL_PHONE----->权限被通过了");
                } else {
                    // Permission Denied   被拒绝
                    LogUtils.i("CALL_PHONE----->权限被拒绝了");
                }
            case WRITE_EXTERNAL_STORAGE:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 通过
                    LogUtils.i("WRITE_EXTERNAL_STORAGE----->权限被通过了");
                } else {
                    // Permission Denied   被拒绝
                    LogUtils.i("WRITE_EXTERNAL_STORAGE----->权限被拒绝了");
                }
                break;
        }
    }


    /**
     * 00001
     * 检查版本号
     */
    private void checkVersion() {
        // 默认有最新版本
        Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
        downLoadBundle();
    }

    /**
     * 00001
     * 下载最新Bundle
     */
    private void downLoadBundle() {
        // 1.下载前检查SD卡是否存在更新包文件夹
        HotUpdate.checkPackage(getApplicationContext(), FileConstant.LOCAL_FOLDER);

        downloadConfig("http://192.168.123.178/wan.zip");
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downloadConfig(String url) {
        final String fileNametemporary = url.substring(url.lastIndexOf("/") + 1);
        final String[] fileNameArray = fileNametemporary.split("\\?");
        final String fileName = fileNameArray[0];
        ApiService apiService = RetrofitServiceManager.getInstance().getApiService();
        Call<ResponseBody> call = apiService.downloadFileWithDynamicUrlSync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    LogUtils.i("下载更新文件成功----->1");
                    write2Data(response.body(), fileName);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.e("onFailure----->" + t);
            }
        });
    }

    /**
     * 写入文件
     *
     * @param body
     * @param fileName
     * @return
     */
    private boolean write2Data(ResponseBody body, String fileName) {
        try {
            File futureStudioIconFile = new File(FileConstant.JS_PATCH_LOCAL_FOLDER + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
                HotUpdate.handleZIP(getApplicationContext());
                LogUtils.i("写入文件成功----->2");
            }
        } catch (IOException e) {
            LogUtils.e("写入文件失败----->" + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                System.exit(0);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}
