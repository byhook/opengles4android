package com.onzhou.opengles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.onzhou.opengles.filter.BaseFilter;
import com.onzhou.opengles.filter.IFilter;
import com.onzhou.opengles.renderer.FilterRenderer;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class GLView extends GLSurfaceView implements IFilter {

    private FilterRenderer mGLRender;

    public GLView(Context context) {
        this(context, null);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupSurfaceView();
    }

    private void setupSurfaceView() {
        //设置版本
        setEGLContextClientVersion(3);
        mGLRender = new FilterRenderer();
        setRenderer(mGLRender);

        try {
            requestRender();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFilter(BaseFilter baseFilter) {
        if (mGLRender != null) {
            mGLRender.setFilter(baseFilter);
        }
    }
}
