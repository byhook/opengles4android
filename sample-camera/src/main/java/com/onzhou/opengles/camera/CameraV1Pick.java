package com.onzhou.opengles.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import com.onzhou.opengles.api.ICamera;
import com.onzhou.opengles.utils.TextureUtils;

/**
 * @anchor: andy
 * @date: 18-11-11
 */

public class CameraV1Pick implements TextureView.SurfaceTextureListener {

    private static final String TAG = "CameraV1Pick";

    private TextureView mTextureView;

    private int mCameraId;

    private ICamera mCamera;

    private TextureEGLHelper mTextureEglHelper;

    public void bindTextureView(TextureView textureView) {
        this.mTextureView = textureView;
        mTextureEglHelper = new TextureEGLHelper();
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //加载OES纹理ID
        final int textureId = TextureUtils.loadOESTexture();
        //初始化操作
        mTextureEglHelper.initEgl(mTextureView, textureId);
        //自定义的SurfaceTexture
        SurfaceTexture surfaceTexture = mTextureEglHelper.loadOESTexture();
        //前置摄像头
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = new CameraV1((Activity) mTextureView.getContext());
        if (mCamera.openCamera(mCameraId)) {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.enablePreview(true);
        } else {
            Log.e(TAG, "openCamera failed");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mTextureEglHelper.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.enablePreview(false);
            mCamera.closeCamera();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onDestroy() {
        if (mTextureEglHelper != null) {
            mTextureEglHelper.onDestroy();
        }
    }
}
