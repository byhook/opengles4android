package com.onzhou.opengles.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.TextureView;

import androidx.core.app.ActivityCompat;

import com.onzhou.opengles.camera.CameraV1Pick;

/**
 * @anchor: andy
 * @date: 18-11-10
 */
public class CameraTextureActivity extends Activity {

    private static final int PERMISSION_CODE = 1000;

    private TextureView mTextureView;

    private CameraV1Pick mCameraPick;

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
        mTextureView = new TextureView(this);
        setContentView(mTextureView);

        mCameraPick = new CameraV1Pick();
        mCameraPick.bindTextureView(mTextureView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPick != null) {
            mCameraPick.onDestroy();
            mCameraPick = null;
        }
    }
}
