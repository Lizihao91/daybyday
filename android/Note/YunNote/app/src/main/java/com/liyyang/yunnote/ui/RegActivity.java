package com.liyyang.yunnote.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.utils.HttpUtil;
import com.liyyang.yunnote.utils.MySharedPreferences;

/**
 * Created by leeyang on 16/7/14.
 */
public class RegActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    Button btnReg;
    EditText etUsername,etPassword;
    String name,password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        Toolbar toolbar_setting = (Toolbar) findViewById(R.id.toolbar_login_activity);
        toolbar_setting.setTitle(R.string.zhuceactivity);
        setSupportActionBar(toolbar_setting);

        etUsername = (EditText) findViewById(R.id.et_reg_username);
        etPassword = (EditText) findViewById(R.id.et_reg_password);
        btnReg = (Button) findViewById(R.id.btn_reg_reg);

        etUsername.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);

        btnReg.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar_setting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //账号密码同时满足条件，注册按钮可以点击
        if (etUsername.getText().length() >= 4&& etPassword.getText().length() >= 4){
            System.out.println("开始>>长度"+etUsername.getText().length());
            btnReg.setEnabled(true);
        }else {
            System.out.println(s.length());
            btnReg.setEnabled(false);
        }



    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        name = etUsername.getText().toString();
        password = etPassword.getText().toString();
        HttpUtil h = new HttpUtil();
        h.reg(name,password,this);
        syncState();
    }

    private void syncState() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //UI更新；
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String reg = null;
            switch (msg.what) {
                case 0:
                    if ( MySharedPreferences.getString(RegActivity.this,"reg",null) != null){
                        reg = MySharedPreferences.getString(RegActivity.this,"reg",null);
                        if (reg.equals("regok")){
                            finish();
                        }
                    }
                    syncState();
                    break;
                default:
                    break;
            }
        }
    };
}
