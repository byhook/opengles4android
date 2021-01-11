
#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include "native_simple.h"

#ifdef ANDROID

#include <android/log.h>

#define LOG_TAG    "HelloTriangle"
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf(LOG_TAG format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf(LOG_TAG format "\n", ##__VA_ARGS__)
#endif

const GLfloat TRIANGLE_VERTICES[] = {0.0f, 0.5f, 0.0f,
                                     -0.5f, -0.5f, 0.0f,
                                     0.5f, -0.5f, 0.0f};

const char VERTEX_SHADER[] =
        "#version 300 es \n"
        "layout (location = 0) in vec4 vPosition;\n"
        "layout (location = 1) in vec4 aColor;\n"
        "out vec4 vColor;\n"
        "void main() { \n"
        "gl_Position  = vPosition;\n"
        "gl_PointSize = 10.0;\n"
        "vColor = aColor;\n"
        "}\n";

const char FRAGMENT_SHADER[] =
        "#version 300 es \n"
        "precision mediump float;\n"
        "in vec4 vColor;\n"
        "out vec4 fragColor;\n"
        "void main() { \n"
        "fragColor = vColor;\n"
        "}\n";

float color[] = {
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
};

GLuint CompileShader(GLenum type, const char *shaderCode) {
    GLint shader = glCreateShader(type);
    if (shader != 0) {
        glShaderSource(shader, 1, &shaderCode, NULL);
        glCompileShader(shader);
        //检测状态
        GLint glResult = GL_FALSE;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &glResult);
        if (glResult == GL_FALSE) {
            //创建失败
            glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
    //创建失败
    return 0;
}

GLint LinkProgram(GLint vertexShader, GLint fragmentShader) {
    GLint program = glCreateProgram();
    if (program != 0) {
        //将顶点着色器加入到程序
        glAttachShader(program, vertexShader);
        //将片元着色器加入到程序中
        glAttachShader(program, fragmentShader);
        //链接着色器程序
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus == GL_FALSE) {
            glDeleteProgram(program);
            return 0;
        }
        return program;
    }
    //创建失败
    return 0;
}

void NativeSimpleRenderer::surfaceCreated() {
    //创建顶点着色器
    GLint vertexShader = CompileShader(GL_VERTEX_SHADER, VERTEX_SHADER);
    if (vertexShader == 0) {
        LOGE("loadVertexShader Failed");
        return;
    }
    //创建片段着色器
    GLint fragmentShader = CompileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
    if (fragmentShader == 0) {
        LOGE("loadFragmentShader Failed");
        return;
    }
    GLint program = LinkProgram(vertexShader, fragmentShader);
    //在OpenGLES环境中使用程序片段
    glUseProgram(program);

    //设置背景颜色
    glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
}

void NativeSimpleRenderer::surfaceChanged(unsigned int width, unsigned int height) {
    glViewport(0, 0, width, height);
}

void NativeSimpleRenderer::onDrawFrame() {
    //把颜色缓冲区设置为我们预设的颜色
    glClear(GL_COLOR_BUFFER_BIT);
    //准备坐标数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, TRIANGLE_VERTICES);
    //启用顶点的句柄
    glEnableVertexAttribArray(0);

    //绘制三角形颜色
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, color);

    //绘制
    glDrawArrays(GL_TRIANGLES, 0, 3);
    //禁止顶点数组的句柄
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
}