package com.onzhou.opengles.color;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.onzhou.opengles.utils.ResReadUtils;
import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description: 自定义三角形渲染器
 */
public class ColorRenderer implements GLSurfaceView.Renderer {

    private int color;

    public ColorRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        float redf = (float) Color.red(color) / 255;
        float greenf = (float) Color.green(color) / 255;
        float bluef = (float) Color.blue(color) / 255;
        float alphaf = (float) Color.alpha(color) / 255;
        GLES20.glClearColor(redf, greenf, bluef, alphaf);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //把颜色缓冲区设置为我们预设的颜色
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
