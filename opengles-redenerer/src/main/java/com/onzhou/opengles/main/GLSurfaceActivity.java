package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.simple.SimpleRenderer;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class GLSurfaceActivity extends AbsBaseActivity {

    private GLSurfaceView mGLSurfaceView;

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
        //GLSurfaceView.Renderer renderer = new ColorRenderer(Color.GRAY);
        GLSurfaceView.Renderer renderer = new SimpleRenderer();
        mGLSurfaceView.setRenderer(renderer);
    }

}
