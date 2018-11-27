package com.liyyang.yunnote.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.utils.HttpUtil;
import com.liyyang.yunnote.utils.MySharedPreferences;

/**
 * Created by leeyang on 16/7/14.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    Button btLogin, btReg;
    EditText etAccount, etPassWord;
    String name,password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = (Button) findViewById(R.id.btn_login);
        btReg = (Button) findViewById(R.id.btn_reg);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassWord = (EditText) findViewById(R.id.et_password);

        etPassWord.addTextChangedListener(this);
        etAccount.addTextChangedListener(this);

        btReg.setOnClickListener(this);
        btLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_login:
                if (name != null&&password!=null){
                    if (name.length() < 4|| password.length()<4||name.length() > 8||password.length() > 8){
                        Toast.makeText(this, R.string.loginNot, Toast.LENGTH_SHORT).show();

                    }else {
                        HttpUtil h = new HttpUtil();
                        h.login(name, password,this);
                        syncState();
                    }
                }else {
                    Toast.makeText(this, R.string.loginNull, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_reg:
                startActivity(new Intent(this, RegActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        name = etAccount.getText().toString();
        password = etPassWord.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s) {

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
            switch (msg.what) {
                case 0:
                    if ( MySharedPreferences.getString(LoginActivity.this,"state",null) != null){
                        finish();
                    }
                    syncState();
                    break;
                default:
                    break;
            }
        }
    };
}
