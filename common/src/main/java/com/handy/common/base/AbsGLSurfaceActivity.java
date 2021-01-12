package com.handy.common.base;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.handy.common.renderer.SurfaceRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public abstract class AbsGLSurfaceActivity extends AbsBaseActivity implements GLSurfaceView.Renderer {

    private GLSurfaceView mGLSurfaceView;

    private SurfaceRenderer mSurfaceRenderer;

    protected abstract SurfaceRenderer bindRenderer();

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
        mSurfaceRenderer = bindRenderer();
        mGLSurfaceView.setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (mSurfaceRenderer != null) {
            mSurfaceRenderer.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mSurfaceRenderer != null) {
            mSurfaceRenderer.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceRenderer != null) {
            mSurfaceRenderer.onDrawFrame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSurfaceRenderer != null) {
            mSurfaceRenderer.onDestroy();
        }
    }
}