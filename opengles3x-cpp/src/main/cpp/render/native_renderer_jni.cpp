#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "native_renderer_jni.h"
#include "native_base_renderer.h"
#include "native_color.h"
#include "native_simple.h"

/**
 * 动态注册
 */
JNINativeMethod methods[] = {
        {"onNativeSurfaceCreated", "(I)V",  (void *) surfaceCreated},
        {"onNativeSurfaceChanged", "(II)V", (void *) surfaceChanged},
        {"onNativeDrawFrame",      "()V",   (void *) onDrawFrame},
        {"onNativeRelease",        "()V",   (void *) onRelease}
};

/**
 * 动态注册
 * @param env
 * @return
 */
jint registerNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("com/handy/es3x/cpp/renderer/NativeRenderer");
    if ((env->RegisterNatives(cl, methods, sizeof(methods) / sizeof(methods[0]))) < 0) {
        return -1;
    }
    return 0;
}

/**
 * 加载默认回调
 * @param vm
 * @param reserved
 * @return
 */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    //注册方法
    if (registerNativeMethod(env) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

NativeBaseRenderer *pBaseRenderer = NULL;

JNIEXPORT void JNICALL surfaceCreated(JNIEnv *env, jobject obj, jint color) {
    if (NULL == pBaseRenderer) {
        pBaseRenderer = new NativeColorRenderer();
    }
    pBaseRenderer->surfaceCreated();
}

JNIEXPORT void JNICALL surfaceChanged(JNIEnv *env, jobject obj, jint width, jint height) {
    if (NULL != pBaseRenderer) {
        pBaseRenderer->surfaceChanged(width, height);
    }
}

JNIEXPORT void JNICALL onDrawFrame(JNIEnv *env, jobject obj) {
    if (NULL != pBaseRenderer) {
        pBaseRenderer->onDrawFrame();
    }
}

JNIEXPORT void JNICALL onRelease(JNIEnv *env, jobject obj) {
    if (NULL != pBaseRenderer) {
        delete pBaseRenderer;
        pBaseRenderer = NULL;
    }
}