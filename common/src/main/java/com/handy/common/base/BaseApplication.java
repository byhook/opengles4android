package com.handy.common.base;

import android.app.Application;

import com.handy.common.core.AppCore;

/**
 * @anchor: andy
 * @date: 2018-11-07
 * @description:
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCore.getInstance().init(this);
    }
}
