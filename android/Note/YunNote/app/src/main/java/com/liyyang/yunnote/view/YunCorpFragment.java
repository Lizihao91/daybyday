package com.liyyang.yunnote.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;

import com.liyyang.yunnote.R;

/**
 * Created by leeyang on 16/7/4.
 */
public class YunCorpFragment extends Fragment {

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_collaboration,null);


        return rootView;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
