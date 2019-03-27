

#include <jni.h>
#include <stdio.h>
#include <time.h>
#include <android/bitmap.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "native_window.h"

/**
 * 动态注册
 */
JNINativeMethod methods[] = {
        {"drawColor",  "(Ljava/lang/Object;I)V",                  (void *) drawColor},
        {"drawBitmap", "(Ljava/lang/Object;Ljava/lang/Object;)V", (void *) drawBitmap},
};

/**
 * 动态注册
 * @param env
 * @return
 */
jint registerNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("com/onzhou/opengles/nativewindow/NativeWindowSample");
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

void ThrowException(JNIEnv *env, const char *exception, const char *message) {
    jclass clazz = env->FindClass(exception);
    if (NULL != clazz) {
        env->ThrowNew(clazz, message);
    }
}

void drawColor(JNIEnv *env, jobject obj, jobject surface, jint colorARGB) {
    //分离ARGB
    int alpha = (colorARGB >> 24) & 0xFF;
    int red = (colorARGB >> 16) & 0xFF;
    int green = (colorARGB >> 8) & 0xFF;
    int blue = colorARGB & 0xFF;

    int colorABGR = (alpha << 24) | (blue << 16) | (green << 8) | red;

    //获取目标surface
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    if (NULL == window) {
        ThrowException(env, "java/lang/RuntimeException", "unable to get native window");
        return;
    }
    //默认的是RGB_565
    int32_t result = ANativeWindow_setBuffersGeometry(window, 640, 640, WINDOW_FORMAT_RGBA_8888);
    if (result < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to set buffers geometry");
        //释放窗口
        ANativeWindow_release(window);
        window = NULL;
        return;
    }
    ANativeWindow_acquire(window);

    ANativeWindow_Buffer buffer;
    if (ANativeWindow_lock(window, &buffer, NULL) < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to lock native window");
        //释放窗口
        ANativeWindow_release(window);
        window = NULL;
        return;
    }

    uint32_t *line = (uint32_t *) buffer.bits;
    for (int y = 0; y < buffer.height; y++) {
        for (int x = 0; x < buffer.width; x++) {
            line[x] = colorABGR;
        }
        line = line + buffer.stride;
    }

    if (ANativeWindow_unlockAndPost(window) < 0) {
        ThrowException(env, "java/lang/RuntimeException",
                       "unable to unlock and post to native window");
    }
    //释放窗口
    ANativeWindow_release(window);

}

void drawBitmap(JNIEnv *env, jobject obj, jobject surface, jobject bitmap) {
    //获取bitmap的信息,比如宽和高
    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to get bitmap info");
        return;
    }

    char *data = NULL;
    //获取bitmap对应的native指针
    if (AndroidBitmap_lockPixels(env, bitmap, (void **) &data) < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to lock pixels");
        return;
    }
    if (AndroidBitmap_unlockPixels(env, bitmap) < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to unlock pixels");
        return;
    }

    //获取目标surface
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    if (NULL == window) {
        ThrowException(env, "java/lang/RuntimeException", "unable to get native window");
        return;
    }
    //这里设置为RGBA的方式,总共是4字节32位
    int32_t result = ANativeWindow_setBuffersGeometry(window, info.width, info.height,
                                                      WINDOW_FORMAT_RGBA_8888);
    if (result < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to set buffers geometry");
        //释放窗口
        ANativeWindow_release(window);
        window = NULL;
        return;
    }
    ANativeWindow_acquire(window);

    ANativeWindow_Buffer buffer;
    //锁定窗口的绘图表面
    if (ANativeWindow_lock(window, &buffer, NULL) < 0) {
        ThrowException(env, "java/lang/RuntimeException", "unable to lock native window");
        //释放窗口
        ANativeWindow_release(window);
        window = NULL;
        return;
    }

    //转换为像素点来处理
    int32_t *bitmapPixes = (int32_t *) data;
    uint32_t *line = (uint32_t *) buffer.bits;
    for (int y = 0; y < buffer.height; y++) {
        for (int x = 0; x < buffer.width; x++) {
            line[x] = bitmapPixes[buffer.height * y + x];
        }
        line = line + buffer.stride;
    }
    //解锁窗口的绘图表面
    if (ANativeWindow_unlockAndPost(window) < 0) {
        ThrowException(env, "java/lang/RuntimeException",
                       "unable to unlock and post to native window");
    }
    //释放
    ANativeWindow_release(window);

}


