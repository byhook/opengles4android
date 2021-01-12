package com.handy.es3x.java.filter;

import com.handy.es3x.java.R;
import com.onzhou.opengles.utils.ResReadUtils;

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
public class QuarterMirrorFilter extends BaseFilter {

    public QuarterMirrorFilter() {
        super(ResReadUtils.readResource(R.raw.quarter_mirror_filter_vertex_shader), ResReadUtils.readResource(R.raw.quarter_mirror_filter_fragment_shader));
    }


}
