package com.onzhou.opengles.main;

import android.app.Application;

import com.onzhou.opengles.core.AppCore;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCore.getInstance().init(this);
    }

}
