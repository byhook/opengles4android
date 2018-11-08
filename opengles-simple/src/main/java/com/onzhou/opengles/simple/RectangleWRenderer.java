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
public class RectangleWRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer vertexBuffer;

    private int mProgram;

    private static final int POSITION_COMPONENT_COUNT = 4;

    private static final int COLOR_COMPONENT_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    /**
     * 点的坐标
     * (X, Y, Z, W, R, G, B)
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.0f, 0.0f, 1.5f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.6f, 0.0f, 1f, 1.0f, 1.0f, 1.0f,
            0.5f, -0.6f, 0.0f, 1f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.6f, 0.0f, 2f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.6f, 0.0f, 2f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.6f, 0.0f, 1f, 1.0f, 1.0f, 1.0f,

            0.0f, -0.4f, 0.0f, 1.25f, 0.5f, 0.5f, 0.5f,
            0.0f, 0.4f, 0.0f, 1.75f, 0.5f, 0.5f, 0.5f,
    };

    private int aPositionLocation;

    private int aColorLocation;

    public RectangleWRenderer() {
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
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_w_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_w_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //在OpenGLES环境中使用程序片段
        GLES30.glUseProgram(mProgram);

        //uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");

        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "vPosition");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "aColor");

        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(aPositionLocation,
                POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer);

        GLES30.glEnableVertexAttribArray(aPositionLocation);

        vertexBuffer.position(POSITION_COMPONENT_COUNT);
        GLES30.glVertexAttribPointer(aColorLocation,
                COLOR_COMPONENT_COUNT, GLES30.GL_FLOAT, false, STRIDE, vertexBuffer);

        GLES30.glEnableVertexAttribArray(aColorLocation);

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
