package com.handy.es3x.java.api;

import android.graphics.SurfaceTexture;

/**
 * @anchor: andy
 * @date: 2018-11-11
 * @description:
 */
public interface TextureRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(SurfaceTexture surfaceTexture);

}
