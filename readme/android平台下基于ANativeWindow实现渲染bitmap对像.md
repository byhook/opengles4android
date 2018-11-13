
## 概述

`原生Window API`支持我们在`ndk下`开发原生的绘制功能，后续的一些视频渲染，包括相机采集预览等都可以通过这些`API`来实现，笔者今天通过几个简单的API来实践一下`native层`的绘制功能

## 配置环境

笔者`Android Studio`配置的是`android-ndk-r16b`版本，操作系统是`ubuntu 16.05`

## 绘制背景色

还是从最简单的入手，开始尝试绘制背景颜色，我们此次使用的方案是`SurfaceView+ANativeWindow`的方式，基于之前的[项目工程](https://github.com/byhook/opengles4android)

先定义`Java层的本地方法`：

```java
/**
 * @anchor: andy
 * @date: 2018-11-13
 * @description:
 */
public class NativeWindowSample {

    static {
        System.loadLibrary("native-window");
    }

    /**
     * 绘制指定颜色背景
     *
     * @param surface
     * @param color
     */
    public native void drawColor(Object surface, int color);

    /**
     * 绘制指定颜色背景
     *
     * @param surface
     * @param bitmap
     */
    public native void drawBitmap(Object surface, Object bitmap);

}
```

配置`CMakeLists.txt`文件内容如下：

```java

cmake_minimum_required(VERSION 3.4.1)

##官方标准配置
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fno-rtti -fno-exceptions -Wall")

add_library(native-window
           SHARED
           src/main/cpp/native_window.cpp)

target_link_libraries(native-window
            ${OPENGL_LIB}
            android
            jnigraphics
            log)

```

`build.gradle`中的配置，这里不赘述，比较简单

在我们的子工程目录`src/main/cpp`下新建我们的`native_window.cpp`和`native_window.h`文件：

来看看`native_window.cpp`中`drawColor`的实现：

```java
void drawColor(JNIEnv *env, jobject obj, jobject surface, jint colorARGB) {
    //分离ARGB
    int alpha = (colorARGB >> 24) & 0xFF;
    int red = (colorARGB >> 16) & 0xFF;
    int green = (colorARGB >> 8) & 0xFF;
    int blue = colorARGB & 0xFF;

    int colorABGR = (alpha << 24) | (blue << 16) | (green << 8) | red;

    //获取目标surface
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    //默认的是RGB_565
    ANativeWindow_setBuffersGeometry(window, 640, 640, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_acquire(window);

    ANativeWindow_Buffer buffer;
    ANativeWindow_lock(window, &buffer, NULL);

    uint32_t *line = (uint32_t *) buffer.bits;
    for (int y = 0; y < buffer.height; y++) {
        for (int x = 0; x < buffer.width; x++) {
            line[x] = colorABGR;
        }
        line = line + buffer.stride;
    }

    ANativeWindow_unlockAndPost(window);
    //释放窗口
    ANativeWindow_release(window);
}
```
这里要注意的就是，我们从Java层传入的是`32位的ARGB`的颜色，直接写入我们的`windowBuffer`，颜色显示可能不正确，需要按照`ANativeWindow_Buffer`指定的颜色顺序作一次转换

`绘制一个灰色背景：`

```java
mNativeWindowSample.drawColor(mSurfaceView.getHolder().getSurface(), Color.GRAY);
```

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181113134235686.png)

## 绘制bitmap

直接绘制`bitmap`也比较简单，但是我们需要通过`AndroidBitmap_lockPixels`方法获取`bitmap`对应的本地的数据的指针，通过这个指针来读取对应的像素数据，注释也比较清楚

```java
void drawBitmap(JNIEnv *env, jobject obj, jobject surface, jobject bitmap) {
    //获取bitmap的信息,比如宽和高
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);

    char *data = NULL;
    //获取bitmap对应的native指针
    AndroidBitmap_lockPixels(env, bitmap, (void **) &data);
    AndroidBitmap_unlockPixels(env, bitmap);

    //获取目标surface
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    //这里设置为RGBA的方式,总共是4字节32位
    ANativeWindow_setBuffersGeometry(window, info.width, info.height, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_acquire(window);

    ANativeWindow_Buffer buffer;
    //锁定窗口的绘图表面
    ANativeWindow_lock(window, &buffer, NULL);

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
    ANativeWindow_unlockAndPost(window);
    //释放
    ANativeWindow_release(window);

}
```

`绘制一个bitmap对象`

```java
BitmapFactory.Options options = new BitmapFactory.Options();
options.inScaled = false;
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main, options);
mNativeWindowSample.drawBitmap(mSurfaceView.getHolder().getSurface(), bitmap);
```

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181113134654161.png)

项目地址：
https://github.com/byhook/opengles4android
