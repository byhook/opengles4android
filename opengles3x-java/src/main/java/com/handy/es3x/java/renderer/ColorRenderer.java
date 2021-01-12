package com.handy.es3x.java.renderer;

import android.graphics.Color;
import android.opengl.GLES30;

import com.handy.common.renderer.SurfaceRenderer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class ColorRenderer implements SurfaceRenderer {

    private int color;

    public ColorRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated() {
        //设置背景颜色
        float redF = (float) Color.red(color) / 255;
        float greenF = (float) Color.green(color) / 255;
        float blueF = (float) Color.blue(color) / 255;
        float alphaF = (float) Color.alpha(color) / 255;
        GLES30.glClearColor(redF, greenF, blueF, alphaF);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //设置视图窗口
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        //把颜色缓冲区设置为我们预设的颜色
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void onDestroy() {

    }
}
