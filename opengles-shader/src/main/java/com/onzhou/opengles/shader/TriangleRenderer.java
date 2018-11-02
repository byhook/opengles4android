package com.onzhou.opengles.shader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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
 * @description: 自定义三角形渲染器
 */
public class TriangleRenderer implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;

    private static final int POSITION_COMPONENT_COUNT = 2;

    private final FloatBuffer vertexBuffer;

    private final Context mContext;

    private int mProgram;

    private static final String U_COLOR = "u_Color";

    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private float[] tableVerticesWithTriangles = {
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f
    };

    float color[] = { 0.0f, 0.0f, 0.8f, 1.0f };

    public TriangleRenderer(Context context) {
        this.mContext = context;
        //分配内存空间
        vertexBuffer = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        //读取资源
        String vertexShaderSource = ResReadUtils.readResource(mContext, R.raw.triangle_vertex_shader);
        String fragmentShaderSource = ResReadUtils.readResource(mContext, R.raw.triangle_fragment_shader);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(vertexShaderSource);
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(fragmentShaderSource);
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //在OpenGLES2.0环境中使用程序片段
        GLES20.glUseProgram(mProgram);

        //获取着色器的句柄
        uColorLocation = GLES20.glGetUniformLocation(mProgram, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION);

        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(aPositionLocation, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(aPositionLocation);
    }
}
