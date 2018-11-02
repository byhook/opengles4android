
#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>


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

const GLfloat TRIANGLE_COLOR[] = {0.0f, 0.5f, 0.5f, -0.5f};

const char VERTEX_SHADER[] =
        "attribute vec4 a_Position;\n"
                "void main() {\n"
                "    gl_Position = a_Position;\n"
                "}\n";

const char FRAGMENT_SHADER[] =
        "precision mediump float;\n"
                "uniform vec4 u_Color;\n"
                "void main() {\n"
                "    gl_FragColor = u_Color;\n"
                "}\n";


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

GLint uColorLocation = 0;
GLint aPositionLocation = 0;

JNIEXPORT void JNICALL Java_com_onzhou_opengles_triangle_NativeTriangleRenderer_surfaceCreated
        (JNIEnv *env, jobject obj) {
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

    //获取着色器的句柄
    uColorLocation = glGetUniformLocation(program, "u_Color");
    aPositionLocation = glGetAttribLocation(program, "a_Position");
    //设置背景颜色
    glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
}

JNIEXPORT void JNICALL Java_com_onzhou_opengles_triangle_NativeTriangleRenderer_surfaceChanged
        (JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT void JNICALL Java_com_onzhou_opengles_triangle_NativeTriangleRenderer_onDrawFrame
        (JNIEnv *env, jobject obj) {
    //把颜色缓冲区设置为我们预设的颜色
    glClear(GL_COLOR_BUFFER_BIT);
    //准备三角形的坐标数据
    glVertexAttribPointer(aPositionLocation, 3, GL_FLOAT, GL_FALSE, 0, TRIANGLE_VERTICES);
    //启用三角形顶点的句柄
    glEnableVertexAttribArray(aPositionLocation);

    //设置绘制三角形的颜色
    glUniform4fv(aPositionLocation, 1, TRIANGLE_COLOR);
    //绘制三角形
    glDrawArrays(GL_TRIANGLES, 0, 3);
    //禁止顶点数组的句柄
    glDisableVertexAttribArray(aPositionLocation);
}