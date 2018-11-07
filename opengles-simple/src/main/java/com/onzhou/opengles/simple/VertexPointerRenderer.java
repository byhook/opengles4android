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
public class VertexPointerRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer;

    private int mProgram;

    /**
     * 位置顶点属性的大小
     * 包含(x,y)
     */
    private static final int VERTEX_POSITION_SIZE = 2;

    /**
     * 颜色顶点属性的大小
     * 包含(r,g,b)
     */
    private static final int VERTEX_COLOR_SIZE = 3;

    /**
     * 浮点型数据占用字节数
     */
    private static final int BYTES_PER_FLOAT = 4;

    /**
     * 跨距
     */
    private static final int VERTEX_ATTRIBUTES_SIZE = (VERTEX_POSITION_SIZE + VERTEX_COLOR_SIZE) * BYTES_PER_FLOAT;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            //前两个为坐标,后三个为颜色
            0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
            //两个点的顶点属性
            0.0f, 0.25f, 0.5f, 0.5f, 0.5f,
            0.0f, -0.25f, 0.5f, 0.5f, 0.5f,
    };

    public VertexPointerRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_pointer_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_pointer_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(0, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, VERTEX_ATTRIBUTES_SIZE, vertexBuffer);
        //启用顶点属性
        GLES30.glEnableVertexAttribArray(0);
        //定位本地内存的位置
        vertexBuffer.position(VERTEX_POSITION_SIZE);
        GLES30.glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GLES30.GL_FLOAT, false, VERTEX_ATTRIBUTES_SIZE, vertexBuffer);

        GLES30.glEnableVertexAttribArray(1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 6);

        //绘制两个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 6, 2);

    }
}
