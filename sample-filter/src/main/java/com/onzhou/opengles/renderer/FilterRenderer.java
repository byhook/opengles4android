package com.onzhou.opengles.renderer;

import android.opengl.GLSurfaceView;

import com.onzhou.opengles.filter.BaseFilter;
import com.onzhou.opengles.filter.OriginFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description:
 */
public class FilterRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "FilterRenderer";

    private int mSurfaceWidth, mSurfaceHeight;

    private BaseFilter mTargetFilter = new OriginFilter();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTargetFilter.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mTargetFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mTargetFilter.onDrawFrame();
    }

    public void setFilter(BaseFilter baseFilter) {
        if (mTargetFilter != null) {
            mTargetFilter.onDestroy();
        }
        mTargetFilter = baseFilter;
        if (mTargetFilter != null) {
            mTargetFilter.onSurfaceCreated();
            mTargetFilter.onSurfaceChanged(mSurfaceWidth, mSurfaceHeight);
        }
    }

}
