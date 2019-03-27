package com.onzhou.opengles.simple;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class NativeSimpleRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("native-simple");
    }

    public native void surfaceCreated();

    public native void surfaceChanged(int width, int height);

    public native void onDrawFrame();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        surfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }
}
