package com.onzhou.opengles.filter;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public abstract class BaseFilter {

    /**
     * 滤镜类型声明
     */
    interface FilterType {

        int TYPE_ORIGIN = 0;

        int TYPE_GRAY = 1;

    }

    /**
     * 获取滤镜类型
     *
     * @return
     */
    public abstract int getType();

    /**
     * 获取滤镜数据
     *
     * @return
     */
    public abstract float[] getFilter();

}
