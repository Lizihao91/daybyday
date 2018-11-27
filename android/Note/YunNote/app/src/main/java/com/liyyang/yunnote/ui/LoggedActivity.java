package com.liyyang.yunnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.utils.MySharedPreferences;

/**登录后界面
 * Created by leeyang on 16/7/14.
 */
public class LoggedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        TextView tvuserId = (TextView) findViewById(R.id.tv_userId);
        TextView tvbiguserId = (TextView) findViewById(R.id.tvbiguserId);
        String userId = MySharedPreferences.getString(this,"userId",null);
        tvbiguserId.setText(userId);
        tvuserId.setText(userId);
    }

    public void logoff(View view) {
        MySharedPreferences.remove(this,"state",null);
        MySharedPreferences.remove(this,"reg",null);
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
