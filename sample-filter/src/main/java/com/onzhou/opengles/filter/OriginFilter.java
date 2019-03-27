package com.onzhou.opengles.filter;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class OriginFilter extends BaseFilter {

    @Override
    public int getType() {
        return FilterType.TYPE_ORIGIN;
    }

    @Override
    public float[] getFilter() {
        return new float[]{1.0f, 1.0f, 1.0f};
    }

}
