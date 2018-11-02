package com.onzhou.opengles.utils;

import android.opengl.GLES20;

/**
 * @anchor: andy
 * @date: 2018-09-13
 * @description:
 */
public class ShaderUtils {

    private static final String TAG = "ShaderUtils";

    /**
     * 编译顶点着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片段着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译
     *
     * @param type
     * @param shaderCode
     * @return
     */
    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId != 0) {
            GLES20.glShaderSource(shaderObjectId, shaderCode);
            GLES20.glCompileShader(shaderObjectId);
            //检测状态
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                //创建失败
                GLES20.glDeleteShader(shaderObjectId);
                return 0;
            }
            return shaderObjectId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 链接小程序
     *
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId != 0) {
            //将顶点着色器加入到程序
            GLES20.glAttachShader(programObjectId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES20.glAttachShader(programObjectId, fragmentShaderId);
            //链接着色器程序
            GLES20.glLinkProgram(programObjectId);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programObjectId);
                return 0;
            }
            return programObjectId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 验证程序片段是否有效
     *
     * @param programObjectId
     * @return
     */
    public static boolean validProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] programStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, programStatus, 0);
        return programStatus[0] != 0;
    }

}
