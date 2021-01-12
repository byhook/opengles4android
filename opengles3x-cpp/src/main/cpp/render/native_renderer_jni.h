#include <jni.h>

#ifndef OPENGLES_3X_NATIVE_RENDERER_H
#define OPENGLES_3X_NATIVE_RENDERER_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL surfaceCreated(JNIEnv *, jobject, jint);

JNIEXPORT void JNICALL surfaceChanged(JNIEnv *, jobject, jint, jint);

JNIEXPORT void JNICALL onDrawFrame(JNIEnv *, jobject);

JNIEXPORT void JNICALL onRelease(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif //OPENGLES_3X_NATIVE_RENDERER_H
