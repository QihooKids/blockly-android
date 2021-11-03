package com.qihoo.ailab;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@StringDef({TextType.KEY_TITLE, TextType.KEY_TEXT, TextType.KEY_DES, TextType.KEY_SELECTED, TextType.KEY_SELECT, TextType.KEY_CHECK})
public @interface TextType {
    String KEY_TITLE = "title";
    String KEY_TEXT = "text";
    String KEY_DES = "des";
    String KEY_SELECTED = "selectedtext";
    String KEY_SELECT = "select";
    String KEY_CHECK = "check";
    String KEY_NEXT = "next";
}
