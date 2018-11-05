
## 目录

- [新建工程]()
- [基于SDK实现渲染器]()
- [基于NDK实现渲染器]()
- [实现效果]()

#### 新建工程

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104164419781.png)

`RendererActivity文件`
```java
public class RendererActivity extends AbsBaseActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3);
        //GLSurfaceView.Renderer renderer = new ColorRenderer(Color.GRAY);
        GLSurfaceView.Renderer renderer = new NativeColorRenderer(Color.GRAY);
        mGLSurfaceView.setRenderer(renderer);
    }

}
```
#### 基于SDK实现渲染器
新建渲染器`ColorRenderer`文件
```java
public class ColorRenderer implements GLSurfaceView.Renderer {

    private int color;

    public ColorRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        float redF = (float) Color.red(color) / 255;
        float greenF = (float) Color.green(color) / 255;
        float blueF = (float) Color.blue(color) / 255;
        float alphaF = (float) Color.alpha(color) / 255;
        GLES30.glClearColor(redF, greenF, blueF, alphaF);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视图窗口
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //把颜色缓冲区设置为我们预设的颜色
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
```

#### 基于NDK实现渲染器
新建渲染器`NativeColorRenderer`文件
```java
public class NativeColorRenderer implements GLSurfaceView.Renderer {

    static {
        System.loadLibrary("native-color");
    }

    public native void surfaceCreated(int color);

    public native void surfaceChanged(int width, int height);

    public native void onDrawFrame();

    private int color;

    public NativeColorRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceCreated(color);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        surfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }
}
```
配置对应的`CMakeLists.txt`文件
```java

cmake_minimum_required(VERSION 3.4.1)

##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

##ANDROID_PLATFORM_LEVEL=18
add_definitions("-DDYNAMIC_ES3")
set(OPENGL_LIB GLESv3)

add_library(native-color
           SHARED
           src/main/cpp/native_color.cpp)

target_link_libraries(native-color
            ${OPENGL_LIB}
            android
            EGL
            log
            m)

```
编写对应的`native_color.cpp类`

```java

#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "native_color.h"

/**
 * 动态注册
 */
JNINativeMethod methods[] = {
        {"surfaceCreated", "(I)V",  (void *) surfaceCreated},
        {"surfaceChanged", "(II)V", (void *) surfaceChanged},
        {"onDrawFrame",    "()V",   (void *) onDrawFrame}
};

/**
 * 动态注册
 * @param env
 * @return
 */
jint registerNativeMethod(JNIEnv *env) {
    jclass cl = env->FindClass("com/onzhou/opengles/color/NativeColorRenderer");
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

JNIEXPORT void JNICALL surfaceCreated(JNIEnv *env, jobject obj, jint color) {
	//分离RGBA的百分比
    GLfloat redF = ((color >> 16) & 0xFF) * 1.0f / 255;
    GLfloat greenF = ((color >> 8) & 0xFF) * 1.0f / 255;
    GLfloat blueF = (color & 0xFF) * 1.0f / 255;
    GLfloat alphaF = ((color >> 24) & 0xFF) * 1.0f / 255;
    glClearColor(redF, greenF, blueF, alphaF);
}

JNIEXPORT void JNICALL surfaceChanged(JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT void JNICALL onDrawFrame(JNIEnv *env, jobject obj) {
    //把颜色缓冲区设置为我们预设的颜色
    glClear(GL_COLOR_BUFFER_BIT);
}
```
实现效果：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181104165427818.png)

项目地址：
https://github.com/byhook/opengles4android

参考：
《OpenGL ES 3.0 编程指南第2版》
《OpenGL ES应用开发实践指南Android卷》
