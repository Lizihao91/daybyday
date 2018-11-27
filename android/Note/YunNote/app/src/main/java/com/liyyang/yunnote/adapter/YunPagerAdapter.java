package com.liyyang.yunnote.adapter;

import android.support.v13.app.FragmentPagerAdapter;

import com.liyyang.yunnote.view.DocFragment;
import com.liyyang.yunnote.view.MineFragment;
import com.liyyang.yunnote.view.YunCorpFragment;

/**
 * Created by leeyang on 16/7/6.
 */
public class YunPagerAdapter extends FragmentPagerAdapter {

    public YunPagerAdapter(android.app.FragmentManager fm) {
        super(fm);
    }


    @Override
    public android.app.Fragment getItem(int position) {
        switch (position) {

            case 0:
//                MainActivity.RefreshViewIF re = new DocFragment();
                return new DocFragment();
            case 1:
                return new YunCorpFragment();
            case 2:
                return new MineFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 3;
    }
}
