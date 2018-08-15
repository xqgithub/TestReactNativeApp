package com.first.testreactnativeapp.http;

import rx.Subscriber;

/**
 * 订阅基类
 * Created by admin on 2018/8/15.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private boolean isReconnect;

    public abstract void onSuccess(T t);

    public abstract void onFailure(String message, int error_code);

    /**
     * 完成的时候
     */
    @Override
    public void onCompleted() {
        // hide load dialog
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        try {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onStart() {
        super.onStart();
        // show load dialog
    }
}
