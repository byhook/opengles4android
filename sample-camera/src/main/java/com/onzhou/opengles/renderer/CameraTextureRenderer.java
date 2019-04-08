package com.onzhou.opengles.renderer;


import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.onzhou.opengles.api.ITextureRenderer;
import com.onzhou.opengles.camera.R;
import com.onzhou.opengles.utils.ResReadUtils;
import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @anchor: andy
 * @date: 2018-11-11
 * @description: 基于相机
 */
public class CameraTextureRenderer implements ITextureRenderer {

    private FloatBuffer mVertexBuffer;

    /**
     * OES纹理ID
     */
    private int mOESTextureId = -1;

    /**
     * 程序
     */
    private int mShaderProgram = -1;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoord";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    private static final int POSITION_SIZE = 2;

    private static final int TEXTURE_SIZE = 2;

    private static final int STRIDE = (POSITION_SIZE + TEXTURE_SIZE) * 4;

    /**
     * 前两个为顶点坐标
     * 后两个为纹理坐标
     */
    private static final float[] VERTEX_DATA = {
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 0.0f, 1.0f,
            -1.0f, -1f, 0.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 0f, 0.0f,
            1.0f, -1.0f, 1.0f, 0.0f
    };

    /**
     * 变换矩阵
     */
    private float[] transformMatrix = new float[16];

    public CameraTextureRenderer(int OESTextureId) {
        mOESTextureId = OESTextureId;
        mVertexBuffer = loadVertexBuffer(VERTEX_DATA);
    }

    public FloatBuffer loadVertexBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    @Override
    public void onSurfaceCreated() {
        final int vertexShader = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture_shader));
        final int fragmentShader = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture_shader));
        mShaderProgram = ShaderUtils.linkProgram(vertexShader, fragmentShader);

        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRenderer.POSITION_ATTRIBUTE);
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_SAMPLER_UNIFORM);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(SurfaceTexture surfaceTexture) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //开始使用程序
        GLES30.glUseProgram(mShaderProgram);

        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(transformMatrix);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES30.glUniform1i(uTextureSamplerLocation, 0);
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        mVertexBuffer.position(0);
        GLES30.glEnableVertexAttribArray(aPositionLocation);
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        mVertexBuffer.position(2);
        GLES30.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES30.glVertexAttribPointer(aTextureCoordLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);

    }
}

