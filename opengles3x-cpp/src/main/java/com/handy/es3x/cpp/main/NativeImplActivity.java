package com.handy.es3x.cpp.main;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;

import com.handy.es3x.cpp.renderer.NativeRenderer;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;
import com.onzhou.opengles.renderer.SurfaceRenderer;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class NativeImplActivity extends AbsGLSurfaceActivity {

    public static void intentStart(Context context) {
        Intent intent = new Intent(context, NativeImplActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected SurfaceRenderer bindRenderer() {
        return new NativeRenderer();
    }

}
