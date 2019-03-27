package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.filter.FilterRenderer;
import com.onzhou.opengles.filter.R;

/**
 * @anchor: andy
 * @date: 2019-03-15
 * @description:
 */
@Route(path = RouteTable.PAGE_FILTER)
public class FilterActivity extends AbsBaseActivity implements SeekBar.OnSeekBarChangeListener {

    private ViewGroup mRootLayer;

    private SeekBar mSeekR, mSeekG, mSeekB;

    private FilterRenderer mFilterRenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setupViews();
        setupSeekBars();
    }

    private void setupViews() {
        mRootLayer = (ViewGroup) findViewById(R.id.linear_root_layer);
        mSeekR = (SeekBar) findViewById(R.id.filter_seek_bar_r);
        mSeekG = (SeekBar) findViewById(R.id.filter_seek_bar_g);
        mSeekB = (SeekBar) findViewById(R.id.filter_seek_bar_b);


        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        mRootLayer.addView(glSurfaceView, 0);
        //设置版本
        glSurfaceView.setEGLContextClientVersion(3);
        GLSurfaceView.Renderer renderer = bindRenderer();
        glSurfaceView.setRenderer(renderer);
    }

    private void setupSeekBars() {
        mSeekR.setOnSeekBarChangeListener(this);
        mSeekG.setOnSeekBarChangeListener(this);
        mSeekB.setOnSeekBarChangeListener(this);
    }

    protected GLSurfaceView.Renderer bindRenderer() {
        mFilterRenderer = new FilterRenderer();
        return mFilterRenderer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float r = 1.0f, g = 1.0f, b = 1.0f;
        if (seekBar == mSeekR) {
            r = (float) progress / 100;
        } else if (seekBar == mSeekG) {
            g = (float) progress / 100;
        } else {
            b = (float) progress / 100;
        }
        mFilterRenderer.updateParam(r, g, b);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}