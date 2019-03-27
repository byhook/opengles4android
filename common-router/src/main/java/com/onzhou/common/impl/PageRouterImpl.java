package com.onzhou.common.impl;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.onzhou.common.router.PageRouter;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.core.AppCore;


/**
 * @anchor: andy
 * @date: 2019-03-19
 * @description:
 */
public class PageRouterImpl extends PageRouter {

    @Override
    public void init(Application application) {
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(application);
    }

    @Override
    public void routeBasisPage() {
        ARouter.getInstance().build(RouteTable.PAGE_BASIS).navigation();
    }

    @Override
    public void routePage(String routePage) {
        ARouter.getInstance().build(routePage).navigation();
    }

}
