
#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>


#ifdef ANDROID

#include <android/log.h>

#define LOG_TAG    "HelloColor"
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf(LOG_TAG format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf(LOG_TAG format "\n", ##__VA_ARGS__)
#endif


JNIEXPORT void JNICALL Java_com_onzhou_opengles_color_NativeColorRenderer_surfaceCreated
        (JNIEnv *env, jobject obj, jint color) {

    GLfloat redF = ((color >> 16) & 0xFF) * 1.0f / 255;
    GLfloat greenF = ((color >> 8) & 0xFF) * 1.0f / 255;
    GLfloat blueF = (color & 0xFF) * 1.0f / 255;
    GLfloat alphaF = ((color >> 24) & 0xFF) * 1.0f / 255;
    glClearColor(redF, greenF, blueF, alphaF);
}

JNIEXPORT void JNICALL Java_com_onzhou_opengles_color_NativeColorRenderer_surfaceChanged
        (JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);
}


JNIEXPORT void JNICALL Java_com_onzhou_opengles_color_NativeColorRenderer_onDrawFrame
        (JNIEnv *env, jobject obj) {
    //把颜色缓冲区设置为我们预设的颜色
    glClear(GL_COLOR_BUFFER_BIT);
}