package com.liyyang.yunnote.utils;

import android.content.Context;
import android.content.Intent;

import com.liyyang.yunnote.ui.LoggedActivity;
import com.liyyang.yunnote.ui.LoginActivity;

/**
 * Created by leeyang on 16/7/14.
 */
public class Login {

    public Intent Login(Context context){
        Intent i = new Intent(context, LoginActivity.class);
        return i;
    }

    public Intent Logged(Context context){
        Intent i = new Intent(context, LoggedActivity.class);
        return i;
    }
}
