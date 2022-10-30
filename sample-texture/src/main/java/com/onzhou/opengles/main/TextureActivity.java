package com.onzhou.opengles.main;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.texture.TextureRenderer;

/**
 * @anchor: andy
 * @date: 18-11-10
 */
public class TextureActivity extends AbsGLSurfaceActivity {

    public static void intentStart(Context context) {
        Intent intent = new Intent(context, TextureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new TextureRenderer();
    }

}
