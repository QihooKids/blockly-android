package com.qihoo.ailab.util;

import android.util.Log;

public class L {

    private static String BASE = "AIRule.";

    private static boolean DEBUG = true;

    public static void e(String tag, String s) {
        if(DEBUG){
            Log.e(prefix(tag), s);
        }
    }

    public static void d(String tag, String s) {
        if(DEBUG){
            Log.d(prefix(tag), s);
        }
    }

    private static String prefix(String tag) {
        return BASE+tag;
    }
}
