package com.onzhou.opengles.sample;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.handy.es3x.cpp.main.NativeImplActivity;
import com.handy.es3x.java.main.JavaImplActivity;
import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.main.CameraSurfaceActivity;
import com.handy.es3x.java.main.FilterActivity;
import com.handy.es3x.cpp.main.NativeWindowActivity;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class SampleActivity extends AbsBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengles_sample);
    }

    public void onBasisClick(View view) {
        JavaImplActivity.intentStart(this);
    }

    public void onColorClick(View view) {
        NativeImplActivity.intentStart(this);
    }

    /**
     * 基于JNI实现的OpenGLES相关操作
     *
     * @param view
     */
    public void onNativeClick(View view) {
        NativeWindowActivity.intentStart(this);
    }

    /**
     * 图片纹理处理
     *
     * @param view
     */
    public void onTextureClick(View view) {
        JavaImplActivity.intentStart(this);
    }

    /**
     * 黑白相机实现
     *
     * @param view
     */
    public void onCameraClick(View view) {
        CameraSurfaceActivity.intentStart(this);
    }

    /**
     * 滤镜实现
     *
     * @param view
     */
    public void onFilterClick(View view) {
        FilterActivity.intentStart(this);
    }
}
