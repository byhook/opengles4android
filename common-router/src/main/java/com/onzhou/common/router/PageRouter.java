package com.onzhou.common.router;

import android.app.Application;
import android.net.Uri;

import com.onzhou.common.impl.PageRouterImpl;

/**
 * @anchor: andy
 * @date: 2019-03-19
 * @description:
 */
public abstract class PageRouter {

    private static PageRouter sInstance;

    public static PageRouter getInstance() {
        if (sInstance == null) {
            sInstance = new PageRouterImpl();
        }
        return sInstance;
    }

    public abstract void init(Application application);

    /**
     * 进入基础页面
     */
    public abstract void routeBasisPage();

    /**
     * 路由指定页面
     *
     * @param routePage
     */
    public abstract void routePage(String routePage);

}
