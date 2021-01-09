package com.onzhou.opengles.base;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public abstract class AbsGLSurfaceActivity extends AbsBaseActivity {

    private GLSurfaceView mGLSurfaceView;

    protected abstract GLSurfaceView.Renderer bindRenderer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3);
        GLSurfaceView.Renderer renderer = bindRenderer();
        mGLSurfaceView.setRenderer(renderer);
    }

}