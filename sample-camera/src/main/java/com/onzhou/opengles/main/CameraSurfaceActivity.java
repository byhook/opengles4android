package com.onzhou.opengles.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.renderer.CameraSurfaceRenderer;

/**
 * @anchor: andy
 * @date: 18-11-10
 */
public class CameraSurfaceActivity extends AbsBaseActivity {

    private static final int PERMISSION_CODE = 100;

    private GLSurfaceView mGLSurfaceView;

    public static void intentStart(Context context) {
        Intent intent = new Intent(context, CameraSurfaceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults != null && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupView();
            }
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
