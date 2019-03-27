package com.onzhou.opengles.simple;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.onzhou.opengles.shader.R;
import com.onzhou.opengles.utils.ResReadUtils;
import com.onzhou.opengles.utils.ShaderUtils;

import java.io.UnsupportedEncodingException;
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
public class UniformRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "UniformRenderer";

    private int mProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_uniform_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_uniform_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
        //在OpenGLES环境中使用程序片段
        GLES30.glUseProgram(mProgram);


        final int[] maxUniforms = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxUniforms, 0);
        final int[] numUniforms = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_ACTIVE_UNIFORMS, numUniforms, 0);

        Log.d(TAG, "maxUniforms=" + maxUniforms[0] + " numUniforms=" + numUniforms[0]);

        int[] length = new int[1];
        int[] size = new int[1];
        int[] type = new int[1];
        byte[] nameBuffer = new byte[maxUniforms[0] - 1];

        for (int index = 0; index < numUniforms[0]; index++) {
            GLES30.glGetActiveUniform(mProgram, index, maxUniforms[0], length, 0, size, 0, type, 0, nameBuffer, 0);
            String uniformName = new String(nameBuffer);
            int location = GLES30.glGetUniformLocation(mProgram, uniformName);

            Log.d(TAG, "uniformName=" + uniformName + " location=" + location + " type=" + type[0] + " size=" + size[0]);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    }
}
