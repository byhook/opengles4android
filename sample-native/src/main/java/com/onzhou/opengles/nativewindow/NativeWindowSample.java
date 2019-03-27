package com.onzhou.opengles.nativewindow;

/**
 * @anchor: andy
 * @date: 2018-11-13
 * @description:
 */
public class NativeWindowSample {

    static {
        System.loadLibrary("native-window");
    }

    /**
     * 绘制指定颜色背景
     *
     * @param surface
     * @param color
     */
    public native void drawColor(Object surface, int color);

    /**
     * 绘制指定颜色背景
     *
     * @param surface
     * @param bitmap
     */
    public native void drawBitmap(Object surface, Object bitmap);

}
