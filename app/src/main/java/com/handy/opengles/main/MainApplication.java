package com.handy.opengles.main;

import android.app.Application;

import com.handy.common.core.AppCore;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCore.getInstance().init(this);
    }

}
