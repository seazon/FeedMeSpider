package com.seazon.feedme.spider.utils;

import android.util.Log;

public class LogUtils {

    private static String TAG = "FeedMeSpider";

    public static boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }

    public static void debug(String content) {
//        if (content != null && isDebugMode()) {
            Log.d(TAG, content);
//        }
    }

    public static void info(String content) {
//        if (content != null && isDebugMode()) {
            Log.i(TAG, content);
//        }
    }

    public static void warn(String content) {
//        if (content != null && isDebugMode()) {
            Log.w(TAG, content);
//        }
    }

    public static void error(String content) {
        error(content, null);
    }

    public static void error(Throwable e) {
        error(null, e);
    }

    public static void error(String content, Throwable e) {
        if (isDebugMode()) {
            Log.e(TAG, content, e);
        }
    }

}
