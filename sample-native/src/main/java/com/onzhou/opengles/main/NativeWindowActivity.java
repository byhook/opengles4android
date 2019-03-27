package com.onzhou.opengles.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.nativewindow.NativeWindowSample;
import com.onzhou.opengles.nativewindow.R;
import com.onzhou.opengles.task.AssertReleaseTask;

/**
 * @anchor: andy
 * @date: 2018-11-13
 * @description:
 */
@Route(path = RouteTable.PAGE_NATIVE)
public class NativeWindowActivity extends AbsBaseActivity {

    /**
     * 根容器
     */
    private ViewGroup mRootLayer;

    private Button mBtnColor, mBtnBitmap;

    private NativeWindowSample mNativeWindowSample;

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_window);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNativeWindowSample = new NativeWindowSample();
        setupView();
    }

    private void setupView() {
        mRootLayer = (ViewGroup) findViewById(R.id.root_layer);
        mBtnColor = (Button) findViewById(R.id.btn_draw_color);
        mBtnBitmap = (Button) findViewById(R.id.btn_draw_bitmap);
        mSurfaceView = new SurfaceView(this);
        mRootLayer.addView(mSurfaceView);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mBtnColor.setEnabled(true);
                mBtnBitmap.setEnabled(true);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void onDrawColorClick(View view) {
        mNativeWindowSample.drawColor(mSurfaceView.getHolder().getSurface(), Color.GRAY);
    }

    public void onDrawBitmapClick(View view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main, options);
        mNativeWindowSample.drawBitmap(mSurfaceView.getHolder().getSurface(), bitmap);
    }

}
