package com.onzhou.opengles.camera;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.view.TextureView;

import com.onzhou.opengles.renderer.CameraTextureRenderer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @anchor: andy
 * @date: 2018-11-11
 * @description: 基于相机
 */
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

    /**
     * 纹理ID
     */
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
                    initEGLContext(3);
                    return;
                case EGLMessage.MSG_RENDER:
                    drawFrame();
                    return;
                case EGLMessage.MSG_DESTROY:
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
        mHandlerThread = new HandlerThread("Renderer Thread");
        mHandlerThread.start();
        mHandler = new TextureHandler(mHandlerThread.getLooper());
        mHandler.sendEmptyMessage(EGLMessage.MSG_INIT);
    }

    private void initEGLContext(int clientVersion) {
        //获取显示设备
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
        mTextureRenderer.onSurfaceChanged(width, height);
    }

    private void drawFrame() {
        if (mTextureRenderer != null) {
            EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext);
            mTextureRenderer.onDrawFrame(mOESSurfaceTexture);
            EGL14.eglSwapBuffers(mEGLDisplay, mEglSurface);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(EGLMessage.MSG_RENDER);
        }
    }

    public SurfaceTexture loadOESTexture() {
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
