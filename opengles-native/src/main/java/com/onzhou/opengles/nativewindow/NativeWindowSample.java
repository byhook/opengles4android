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

    /*public byte[] bitmap2RGB(Bitmap bitmap) {
        //返回可用于储存此位图像素的最小字节数
        int bytes = bitmap.getByteCount();
        //创建字节缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        //将位图的像素复制到指定的缓冲区
        bitmap.copyPixelsToBuffer(buffer);
        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {
            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B
        }
        return pixels;
    }*/
}
