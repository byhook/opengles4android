package com.onzhou.opengles.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.onzhou.common.router.PageRouter;
import com.onzhou.common.table.RouteTable;
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
        PageRouter.getInstance().routeBasisPage();
    }

    public void onColorClick(View view) {
        PageRouter.getInstance().routePage(RouteTable.PAGE_COLOR);
    }

    /**
     * 基于JNI实现的OpenGLES相关操作
     *
     * @param view
     */
    public void onNativeClick(View view) {
        PageRouter.getInstance().routePage(RouteTable.PAGE_NATIVE);
    }

    /**
     * 图片纹理处理
     *
     * @param view
     */
    public void onTextureClick(View view) {
        PageRouter.getInstance().routePage(RouteTable.PAGE_TEXTURE);
    }

    /**
     * 黑白相机实现
     *
     * @param view
     */
    public void onCameraClick(View view) {
        PageRouter.getInstance().routePage(RouteTable.PAGE_CAMERA);
    }

    /**
     * 滤镜实现
     *
     * @param view
     */
    public void onFilterClick(View view) {
        PageRouter.getInstance().routePage(RouteTable.PAGE_FILTER);
    }
}
