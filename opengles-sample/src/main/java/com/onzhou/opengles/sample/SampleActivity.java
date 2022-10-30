package com.onzhou.opengles.sample;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;


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

    }

    public void onColorClick(View view) {

    }

    /**
     * 基于JNI实现的OpenGLES相关操作
     *
     * @param view
     */
    public void onNativeClick(View view) {

    }

    /**
     * 图片纹理处理
     *
     * @param view
     */
    public void onTextureClick(View view) {

    }

    /**
     * 黑白相机实现
     *
     * @param view
     */
    public void onCameraClick(View view) {

    }

    /**
     * 滤镜实现
     *
     * @param view
     */
    public void onFilterClick(View view) {

    }
}
