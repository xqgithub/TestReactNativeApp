package com.first.testreactnativeapp;

import android.app.Application;

import com.facebook.react.BuildConfig;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.first.testreactnativeapp.communication.CommPackage;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2018/8/10.
 */

public class MainApplication extends Application implements ReactApplication {

    private static MainApplication instance;
    private static final CommPackage mCommPackage = new CommPackage();


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    mCommPackage
            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }


    /**
     * 获取Application实例
     */
    public static MainApplication getInstance() {
        return instance;
    }

    /**
     * 包名
     */
    public String getAppPackageName() {
        return this.getPackageName();
    }

    /**
     * 获取 reactPackage
     *
     * @return
     */
    public static CommPackage getReactPackage() {
        return mCommPackage;
    }

}
