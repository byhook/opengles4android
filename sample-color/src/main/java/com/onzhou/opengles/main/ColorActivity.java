package com.onzhou.opengles.main;

import android.graphics.Color;
import android.opengl.GLSurfaceView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.color.NativeColorRenderer;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
@Route(path = RouteTable.PAGE_COLOR)
public class ColorActivity extends AbsGLSurfaceActivity {

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new NativeColorRenderer(Color.GRAY);
    }

}
