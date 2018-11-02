package com.onzhou.opengles.shader;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class RendererActivity extends AbsBaseActivity {

    /**
     *
     */
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shader);
        setupViews();
    }

    private void setupViews() {
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3);
        GLSurfaceView.Renderer  renderer = new ColorRenderer(Color.GRAY);
        mGLSurfaceView.setRenderer(renderer);
    }

}
