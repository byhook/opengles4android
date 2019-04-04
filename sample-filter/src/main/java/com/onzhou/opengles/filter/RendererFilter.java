package com.onzhou.opengles.filter;


/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public interface RendererFilter {

    /**
     * 创建回调
     */
    void onSurfaceCreated();

    /**
     * 宽高改变回调
     *
     * @param width
     * @param height
     */
    void onSurfaceChanged(int width, int height);

    /**
     * 绘制回调
     */
    void onDrawFrame();

    /**
     * 销毁回调
     */
    void onDestroy();

}
