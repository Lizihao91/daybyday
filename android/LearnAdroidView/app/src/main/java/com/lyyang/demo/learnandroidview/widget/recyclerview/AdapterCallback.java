package com.lyyang.demo.learnandroidview.widget.recyclerview;


/**
 * Created by LeeYang
 */

public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
