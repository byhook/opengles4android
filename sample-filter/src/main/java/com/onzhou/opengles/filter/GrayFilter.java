package com.onzhou.opengles.filter;

import android.opengl.GLES30;

import com.onzhou.opengles.utils.ResReadUtils;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class GrayFilter extends BaseFilter {

    private int aFilterLocation;

    private float[] filterValue = new float[]{0.299f, 0.587f, 0.114f};

    public GrayFilter() {
        super(ResReadUtils.readResource(R.raw.gray_filter_vertex_shader), ResReadUtils.readResource(R.raw.gray_filter_fragment_shader));
    }

    @Override
    public void setupProgram() {
        super.setupProgram();
        aFilterLocation = GLES30.glGetUniformLocation(mProgram, "a_Filter");
    }

    @Override
    public void onUpdateDrawFrame() {
        //更新参数
        GLES30.glUniform3fv(aFilterLocation, 1, filterValue, 0);
    }

}
