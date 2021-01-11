package com.onzhou.opengles.renderer;

/**
 * date: 2021-01-11
 * description:
 */
public interface SurfaceRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

    void onDestroy();

}
