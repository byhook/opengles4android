package com.handy.es3x.cpp.renderer;

import android.opengl.GLSurfaceView;

import com.onzhou.opengles.renderer.SurfaceRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class NativeRenderer implements SurfaceRenderer {

    static {
        System.loadLibrary("native-es3x");
    }

    public native void onNativeSurfaceCreated(int renderType);

    public native void onNativeSurfaceChanged(int width, int height);

    public native void onNativeDrawFrame();

    public native void onNativeRelease();

    @Override
    public void onSurfaceCreated() {
        onNativeSurfaceCreated(0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        onNativeSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        onNativeDrawFrame();
    }

    @Override
    public void onDestroy(){
        onNativeRelease();
    }

}
