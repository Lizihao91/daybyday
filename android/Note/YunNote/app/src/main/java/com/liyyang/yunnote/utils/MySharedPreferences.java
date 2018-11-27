package com.liyyang.yunnote.utils;

import android.content.Context;

/**
 * 登录状态
 * Created by leeyang on 16/7/5.
 */
public class MySharedPreferences {


    public static final String PRE_NAME = "cookie_sync";

    public static String getString(Context context, String key, String defeultValue){

        android.content.SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);

        return sp.getString(key, defeultValue);
    }

    public static void setString(Context context, String key, String value){

        android.content.SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);

        sp.edit().putString(key, value).commit();
    }

    public static void remove(Context context, String key, String value){

        android.content.SharedPreferences sp = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);

        sp.edit().remove(key).commit();
    }
}
