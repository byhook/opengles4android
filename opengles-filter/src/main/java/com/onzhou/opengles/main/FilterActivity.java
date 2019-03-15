package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.filter.FilterRenderer;

/**
 * @anchor: andy
 * @date: 2019-03-15
 * @description:
 */
public class FilterActivity extends AbsGLSurfaceActivity {

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new FilterRenderer();
    }

}