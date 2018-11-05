package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;

import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.simple.NativeSimpleRenderer;
import com.onzhou.opengles.simple.SimpleRenderer;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class SimpleActivity extends AbsGLSurfaceActivity {

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new SimpleRenderer();
    }

}
