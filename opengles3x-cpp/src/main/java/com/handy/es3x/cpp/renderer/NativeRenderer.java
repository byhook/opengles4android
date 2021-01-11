package com.handy.es3x.cpp.renderer;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class NativeRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("native-es3x");
    }

    public native void surfaceCreated(int renderType);

    public native void surfaceChanged(int width, int height);

    public native void onDrawFrame();

    public native void onRelease();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceCreated(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        surfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }

    /**
     * 销毁
     */
    public void onDestroy(){
        onRelease();
    }

}
