package com.onzhou.opengles.api;

import android.graphics.SurfaceTexture;

/**
 * @anchor: andy
 * @date: 2018-11-11
 * @description:
 */
public interface ITextureRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(SurfaceTexture surfaceTexture);

}
