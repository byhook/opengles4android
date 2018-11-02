package com.onzhou.opengles.color;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.shader.R;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class ColorActivity extends AbsBaseActivity {

    /**
     *
     */
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        setupViews();
    }

    private void setupViews() {
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3);
        GLSurfaceView.Renderer renderer = new NativeColorRenderer(Color.GRAY);
        mGLSurfaceView.setRenderer(renderer);
    }

}
