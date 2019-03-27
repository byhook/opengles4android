package com.onzhou.opengles.filter;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class GrayFilter extends BaseFilter {

    @Override
    public int getType() {
        return FilterType.TYPE_GRAY;
    }

    @Override
    public float[] getFilter() {
        return new float[]{0.299f, 0.587f, 0.114f};
    }

}
