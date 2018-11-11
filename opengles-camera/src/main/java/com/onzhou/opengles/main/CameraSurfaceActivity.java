package com.onzhou.opengles.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.camera.CameraSurfaceRenderer;

/**
 * @anchor: andy
 * @date: 18-11-10
 */

public class CameraSurfaceActivity extends AbsBaseActivity {

    private static final int PERMISSION_CODE = 100;

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyPermission();
    }

    private void applyPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            setupView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            setupView();
        }
    }

    private void setupView() {
        //实例化一个GLSurfaceView
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setRenderer(new CameraSurfaceRenderer(mGLSurfaceView));
        setContentView(mGLSurfaceView);
    }

}
