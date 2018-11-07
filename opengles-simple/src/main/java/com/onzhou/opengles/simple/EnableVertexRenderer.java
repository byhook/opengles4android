package com.onzhou.opengles.simple;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.onzhou.opengles.shader.R;
import com.onzhou.opengles.utils.ResReadUtils;
import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
public class EnableVertexRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer, colorBuffer;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private float[] vertexColors = new float[]{
            0.5f, 0.5f, 0.8f, 1.0f
    };

    public EnableVertexRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
        //为颜色数据分配本地内存空间
        colorBuffer = ByteBuffer.allocateDirect(vertexColors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        colorBuffer.put(vertexColors);
        colorBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_enable_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_enable_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //颜色数据都是一致的
        GLES30.glVertexAttrib4fv(1, colorBuffer);

        //获取位置的顶点数组
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0);

        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁用顶点属性
        GLES30.glDisableVertexAttribArray(0);
    }
}
