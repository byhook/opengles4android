package com.onzhou.opengles.api;

import android.graphics.SurfaceTexture;

import java.io.IOException;

/**
 * @anchor: andy
 * @date: 2018-11-12
 * @description:
 */
public interface ICamera {

    boolean openCamera(int cameraId);

    void enablePreview(boolean enable);

    void setPreviewTexture(SurfaceTexture surfaceTexture);

    void closeCamera();
}
