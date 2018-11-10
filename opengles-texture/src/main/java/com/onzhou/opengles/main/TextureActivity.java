package com.onzhou.opengles.main;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.texture.TextureRenderer;

/**
 * @anchor: andy
 * @date: 18-11-10
 */

public class TextureActivity extends AbsGLSurfaceActivity {

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new TextureRenderer();
    }

}
