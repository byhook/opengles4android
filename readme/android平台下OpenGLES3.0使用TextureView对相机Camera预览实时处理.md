
## GLSurfaceView和TextureView对比

相信接触过直播或者视频播放的朋友，对这两个控件应该很熟悉，这个两个控件都可以用来渲染视频用的，下面来看下他们的区别：

- **GLSurfaceView**：

>前面的一些博客都是基于`GLSurfaceView`来实现渲染的，因为它内部自带了EGL的管理以及渲染线程。另外它定义了用户需要实现的Render接口，其中`EglHelper`和`GLThread`分别实现了上面提到的管理`EGL环境`和`渲染线程`的工作。

`缺点`：`GLSurfaceView将OpenGL绑定到一起，换言之，GLSurfaceView一但销毁，伴随的OpenGL也一起销毁了，一个OpenGL只能渲染一个GLSurfaceView`。

- **TextureView**：

>这个控件是在在android 4.0中引入的，和SurfaceView不同，它不会在WMS中单独创建窗口，而是作为一个普通View，因此可以和其它普通View一样进行移动，旋转，缩放，动画等变化。值得注意的是TextureView必须在`硬件加速的窗口`中。
>
`缺点`：`使用这个控件需要自己来实现EGL管理和渲染线程，当然了，虽然麻烦了一点，但是也更为灵活`。

`注意：`TextureView本身内置了一个`SurfaceTexture`，用来配合EGL来将图像显示到屏幕上，而我们自定义的SurfaceTexture用来接收Camera的预览图像来做二次处理。

## 开始实践

我们现在就可以仿照`GLSurfaceView`的实现原理，基于`HandlerThread`来自己实现`渲染线程和EGL环境管理`：

回到之后的[项目工程](https://github.com/byhook/opengles4android)，新建`TextureEGLHelper`

在启动的`CameraTextureActivity中初始化如下代码`：

```java
private void setupView() {
    mTextureView = new TextureView(this);
    setContentView(mTextureView);

	//实例化相机采集类
    mCameraPick = new CameraV1Pick();
    mCameraPick.bindTextureView(mTextureView);
}
```

因为这里使用的都是`CameraV1版本的相机`，所以新建一个`CameraV1Pick相机采集类`：

```java
public class CameraV1Pick implements TextureView.SurfaceTextureListener {

    private static final String TAG = "CameraV1Pick";

    private TextureView mTextureView;

    private int mCameraId;

    private ICamera mCamera;

    private TextureEGLHelper mTextureEglHelper;

    public void bindTextureView(TextureView textureView) {
        this.mTextureView = textureView;
        mTextureEglHelper = new TextureEGLHelper();
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //加载OES纹理ID
        final int textureId = TextureUtils.loadOESTexture();
        //初始化操作
        mTextureEglHelper.initEgl(mTextureView, textureId);
        //自定义的SurfaceTexture
        SurfaceTexture surfaceTexture = mTextureEglHelper.loadOESTexture();
        //前置摄像头
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = new CameraV1((Activity) mTextureView.getContext());
        if (mCamera.openCamera(mCameraId)) {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.enablePreview(true);
        } else {
            Log.e(TAG, "openCamera failed");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mTextureEglHelper.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.enablePreview(false);
            mCamera.closeCamera();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onDestroy() {
        if (mTextureEglHelper != null) {
            mTextureEglHelper.onDestroy();
        }
    }
}
```

上面的注释比较清楚了，在`onSurfaceTextureAvailable`的回调中，首先通过`TextureUtils.loadOESTexture`方法加载外部纹理`textureId`，然后通过`mTextureEglHelper`这个实例初始化`EGL环境`，接着创建一个基于`textureId`的`自定义SurfaceTexture(它对图像流的处理并不直接显示，而是转为GL外部纹理，因此可用于图像流数据的二次处理(如Camera滤镜，桌面特效等)`，最后开启相机，将刚才自定义的`SurfaceTexture`设置到相机中。

其中加载外部纹理ID的`loadOESTexture`的方法实现如下：

```java
/**
     * 加载OES Texture
     *
     * @return
     */
    public static int loadOESTexture() {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return textureIds[0];
    }
```

## 封装EGL环境管理和线程渲染类

EGL主要需要四个对象，一个EGLDisplay描述EGL显示屏，一个EGLConfig，一个EGLContext，一个EGLSurface描述

接下来就是笔者刚刚说到的`EGL环境管理和线程渲染`类，主要用来初始化`EGL环境`和在线程中渲染。

```java

public class TextureEGLHelper extends HandlerThread implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "TextureEGLHelper";

    @IntDef({EGLMessage.MSG_INIT, EGLMessage.MSG_RENDER, EGLMessage.MSG_DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EGLMessage {
        int MSG_INIT = 100;
        int MSG_RENDER = 200;
        int MSG_DESTROY = 300;
    }

    private HandlerThread mHandlerThread;

    private Handler mHandler;

    private TextureView mTextureView;

    private int mOESTextureId;

    /**
     * 显示设备
     */
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;

    /**
     * EGL上下文
     */
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;

	/**
	 * 描述帧缓冲区配置参数
	 */
    private EGLConfig[] configs = new EGLConfig[1];

    /**
     * EGL绘图表面
     */
    private EGLSurface mEglSurface;

	/**
	 * 自定义的SurfaceTexture
	 * 用来接受Camera数据作二次处理
	 */
    private SurfaceTexture mOESSurfaceTexture;

    private CameraTextureRenderer mTextureRenderer;

    private final class TextureHandler extends Handler {

        public TextureHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EGLMessage.MSG_INIT:
                	//笔者使用的是OpenGL ES 3.0
                    initEGLContext(3);
                    return;
                case EGLMessage.MSG_RENDER:
                    //开始渲染
                    drawFrame();
                    return;
                case EGLMessage.MSG_DESTROY:
                    //销毁
                    return;
                default:
                    return;
            }
        }
    }

    public TextureEGLHelper() {
        super("TextureEGLHelper");
    }

    public void initEgl(TextureView textureView, int textureId) {
        mTextureView = textureView;
        mOESTextureId = textureId;
        //启动线程
        mHandlerThread = new HandlerThread("Renderer Thread");
        mHandlerThread.start();
        mHandler = new TextureHandler(mHandlerThread.getLooper());
        //线程中初始化
        mHandler.sendEmptyMessage(EGLMessage.MSG_INIT);
    }

    /**
     * 初始化EGL环境
     *
     * @param clientVersion
     */
    private void initEGLContext(int clientVersion) {
        //获取默认显示设备
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay error: " + EGL14.eglGetError());
        }
        //存放EGL版本号
        int[] version = new int[2];
        version[0] = 3;
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize error: " + EGL14.eglGetError());
        }
        //配置列表
        int[] attributes = {
                EGL14.EGL_BUFFER_SIZE, 32,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, 4,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };
        int[] numConfigs = new int[1];
        //EGL选择配置
        if (!EGL14.eglChooseConfig(mEGLDisplay, attributes, 0, configs, 0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("eglChooseConfig error: " + EGL14.eglGetError());
        }
        //获取TextureView内置的SurfaceTexture作为EGL的绘图表面，也就是跟系统屏幕打交道
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        if (surfaceTexture == null) {
            throw new RuntimeException("surfaceTexture is null");
        }
        //创建EGL显示窗口
        final int[] surfaceAttributes = {EGL14.EGL_NONE};
        mEglSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], surfaceTexture, surfaceAttributes, 0);
        //创建上下文环境
        int[] contextAttributes = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, clientVersion,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, contextAttributes, 0);

        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY || mEGLContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("eglCreateContext fail error: " + EGL14.eglGetError());
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent error: " + EGL14.eglGetError());
        }
        //加载渲染器
        mTextureRenderer = new CameraTextureRenderer(mOESTextureId);
        mTextureRenderer.onSurfaceCreated();
    }

    public void onSurfaceChanged(int width, int height) {
        //设置视口
        mTextureRenderer.onSurfaceChanged(width, height);
    }

    private void drawFrame() {
        if (mTextureRenderer != null) {
            //指定mEGLContext为当前系统的EGL上下文
            EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext);
            //调用渲染器绘制
            mTextureRenderer.onDrawFrame(mOESSurfaceTexture);
            //交换缓冲区,android使用双缓冲机制,所以我们绘制的都是在后台缓冲区,通过交换将后台缓冲区变为前台显示区,下一帧的绘制仍然在后台缓冲区
            EGL14.eglSwapBuffers(mEGLDisplay, mEglSurface);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mHandler != null) {
            //通知子线程渲染
            mHandler.sendEmptyMessage(EGLMessage.MSG_RENDER);
        }
    }

    public SurfaceTexture loadOESTexture() {
    	//加载自定义的SurfaceTexture传递给相机
        mOESSurfaceTexture = new SurfaceTexture(mOESTextureId);
        mOESSurfaceTexture.setOnFrameAvailableListener(this);
        return mOESSurfaceTexture;
    }

    /**
     * 销毁
     * 释放
     */
    public void onDestroy() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
```

## 相机操作封装

`ICamera代码就不贴了`，本身是抽象的一个相机的操作接口类。

```java
public class CameraV1 implements ICamera {

    private Activity mActivity;

    private int mCameraId;

    private Camera mCamera;

    public CameraV1(Activity activity) {
        mActivity = activity;
    }

    /**
     * 打开相机
     *
     * @param cameraId
     * @return
     */
    public boolean openCamera(int cameraId) {
        try {
            mCameraId = cameraId;
            mCamera = Camera.open(mCameraId);
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 释放开启预览
     *
     * @param enable
     */
    @Override
    public void enablePreview(boolean enable) {
        if (mCamera != null) {
            if (enable) {
                mCamera.startPreview();
            } else {
                mCamera.stopPreview();
            }
        }
    }

    /**
     * 设置相机的旋转角度
     * 前置相机旋转270度
     * 后置相机旋转90度
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭相机释放资源
     */
    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        mActivity = null;
    }

}
```

## 实现自定义的渲染器

最后是`渲染器代码`，当然了，笔者这里是仿照`GLSurfaceView`的`Renderer接口`实现的，你也可以根据这个接口自定义实现一个`渲染器`，主要用来将相机的数据绘制到我们自定义的`SurfaceTexture`上去，这里的`纹理坐标`不再细说，可以对照之前的博客 [android平台下OpenGL ES 3.0使用GLSurfaceView对相机Camera预览实时处理](https://blog.csdn.net/byhook/article/details/83933470)。

```java
/**
 * @anchor: andy
 * @date: 2018-11-11
 * @description: 基于相机
 */
public class CameraTextureRenderer implements ITextureRenderer {

    private FloatBuffer mVertexBuffer;

    /**
     * OES纹理ID
     */
    private int mOESTextureId = -1;

    /**
     * 程序
     */
    private int mShaderProgram = -1;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoord";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    private static final int POSITION_SIZE = 2;

    private static final int TEXTURE_SIZE = 2;

    private static final int STRIDE = (POSITION_SIZE + TEXTURE_SIZE) * 4;

    /**
     * 前两个为顶点坐标
     * 后两个为纹理坐标
     */
    private static final float[] VERTEX_DATA = {
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 0.0f, 1.0f,
            -1.0f, -1f, 0.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 0f, 0.0f,
            1.0f, -1.0f, 1.0f, 0.0f
    };

    /**
     * 变换矩阵
     */
    private float[] transformMatrix = new float[16];

    public CameraTextureRenderer(int OESTextureId) {
        mOESTextureId = OESTextureId;
        mVertexBuffer = loadVertexBuffer(VERTEX_DATA);
    }

    public FloatBuffer loadVertexBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    @Override
    public void onSurfaceCreated() {
        final int vertexShader = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_texture_shader));
        final int fragmentShader = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_texture_shader));
        mShaderProgram = ShaderUtils.linkProgram(vertexShader, fragmentShader);
        //开始使用程序
        GLES30.glUseProgram(mShaderProgram);

        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRenderer.POSITION_ATTRIBUTE);
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mShaderProgram, CameraTextureRenderer.TEXTURE_SAMPLER_UNIFORM);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(SurfaceTexture surfaceTexture) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(transformMatrix);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES30.glUniform1i(uTextureSamplerLocation, 0);
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        mVertexBuffer.position(0);
        GLES30.glEnableVertexAttribArray(aPositionLocation);
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        mVertexBuffer.position(2);
        GLES30.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES30.glVertexAttribPointer(aTextureCoordLocation, 2, GLES30.GL_FLOAT, false, STRIDE, mVertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);

    }
}
```

## 顶点着色器

```java
#version 300 es
layout (location = 0) in vec4 aPosition;
layout (location = 1) in vec4 aTextureCoord;
//纹理矩阵
uniform mat4 uTextureMatrix;
out vec2 vTextureCoord;
void main() {
     gl_Position  = aPosition;
     gl_PointSize = 10.0;
     vTextureCoord = (uTextureMatrix * aTextureCoord).xy;
}
```

## 片段着色器

```java
#version 300 es
//外部纹理
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
in vec2 vTextureCoord;
out vec4 vFragColor;
void main()
{
  vec4 vCameraColor = texture(uTextureSampler, vTextureCoord);
  //黑白滤镜
  float fGrayColor = (0.299*vCameraColor.r + 0.587*vCameraColor.g + 0.114*vCameraColor.b);
  vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}

```

输出结果：

![](https://github.com/byhook/opengles4android/blob/master/readme/images/20181112222945107.png)

项目地址：`opengles-camera`
https://github.com/byhook/opengles4android

参考：

https://www.jianshu.com/p/9db986365cda

https://blog.csdn.net/lb377463323/article/details/77096652
