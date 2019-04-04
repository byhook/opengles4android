package com.onzhou.opengles.utils;

import android.util.Log;

/**
 * 作者: Andy
 * 时间: 2016-12-20
 * 描述:
 * 日志工具类
 */

public class LogUtils {

    protected static final String TAG = "LogUtils";

    /**
     * 对应多个参数
     *
     * @param tag
     * @param content
     * @param args
     */
    public static void v(String tag, String content, Object... args) {
        if (args != null && args.length > 0) {
            final String msg = String.format(content, args);
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String content, Object... args) {
        if (args != null && args.length > 0) {
            final String msg = String.format(content, args);
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String content, Object... args) {
        if (args != null && args.length > 0) {
            Log.d(tag, String.format(content, args));
        }
    }

    public static void e(String tag, String content, Object... args) {
        if (args != null && args.length > 0) {
            Log.e(tag, String.format(content, args));
        }
    }

    /**
     * 基本包装
     *
     * @param tag
     * @param content
     */
    public static void v(String tag, Object content) {
        Log.v(tag, String.valueOf(content));
    }

    public static void i(String tag, Object content) {
        Log.i(tag, String.valueOf(content));
    }

    public static void d(String tag, Object content) {
        Log.d(tag, String.valueOf(content));
    }

    public static void e(String tag, Object content) {
        Log.e(tag, String.valueOf(content));
    }

    public static void e(Exception content) {
        e(TAG, content);
    }

    public static void e(Throwable content) {
        e(TAG, content);
    }

    /**
     * 打印异常信息
     *
     * @param tag
     * @param exception
     */
    public static void e(String tag, Exception exception) {
        if (exception != null) {
            e(tag, exception.toString());
        }
    }

    /**
     * 打印异常信息
     *
     * @param tag
     * @param throwable
     */
    public static void e(String tag, Throwable throwable) {
        if (throwable != null) {
            e(tag, throwable.toString());
        }
    }

}
