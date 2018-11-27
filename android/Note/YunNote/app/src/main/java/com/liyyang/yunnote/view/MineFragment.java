package com.liyyang.yunnote.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.ui.LoggedActivity;
import com.liyyang.yunnote.utils.Login;
import com.liyyang.yunnote.utils.MySharedPreferences;

/**
 * Created by leeyang on 16/7/4.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    TextView tvClikLogin;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_me,null);

        tvClikLogin = (TextView) rootView.findViewById(R.id.cliklogin);
        loginState();
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.lLogin);
        linearLayout.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        //TODO 登录
        if (loginState()){
            startActivity(new Intent(getActivity(),LoggedActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else {
            startActivity(new Login().Login(getActivity()));
        }
    }

    public boolean loginState(){
        if (MySharedPreferences.getString(getActivity(),"state",null) != null){
            if (MySharedPreferences.getString(getActivity(),"state",null).equals("login")){
                tvClikLogin.setText("已登录");
                return true;
            }else {
                tvClikLogin.setText("点此登录");
                return false;
            }
        }
        return false;
    }
}
