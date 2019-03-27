package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.onzhou.common.table.RouteTable;
import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.texture.TextureRenderer;

/**
 * @anchor: andy
 * @date: 18-11-10
 */
@Route(path = RouteTable.PAGE_TEXTURE)
public class TextureActivity extends AbsGLSurfaceActivity {

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new TextureRenderer();
    }

}
