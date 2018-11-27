package com.liyyang.yunnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.liyyang.yunnote.R;
import com.liyyang.yunnote.domain.ItemData;
import com.liyyang.yunnote.utils.MySharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Recyclerview 适配器
 * Created by leeyang on 16/7/5.
 */
public class RecyclerVAdapter extends RecyclerView.Adapter {

    ArrayList itemList;
    String id;//item序号
    static JSONObject JsonObject;
    Context context;


    public RecyclerVAdapter(ArrayList list, Context context){
        this.itemList = list;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public View itemView;

        private TextView tv_title,tv_content,tv_date,tv_size;
        ImageView ivSync;
        public MyViewHolder(View itemView, OnRecyclerViewItemClickListener mOnItemClickListener, OnRecyclerViewLongItemClickListener mOnLongItemClickListener) {
            super(itemView);

            ivSync = (ImageView) itemView.findViewById(R.id.iv_sync);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public TextView getTv_content() {
            return tv_content;
        }
        public ImageView getIv_sync() {
            return ivSync;
        }
        public TextView getTv_date() {
            return tv_date;
        }

        public TextView getTv_size() {
            return tv_size;
        }

        public TextView getTv_title() {
            return tv_title;
        }

        public View getItemView() {
            return itemView;
        }


        @Override
        public void onClick(View v) {//点击
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition(),id);
            }
        }

        @Override
        public boolean onLongClick(View v) {//长按
            if (mOnLongItemClickListener != null) {
                mOnLongItemClickListener.onLongItemClick(v, getAdapterPosition(),id);
            }
            return true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recyclerview_item, null),mOnItemClickListener,mOnLongItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;

        ItemData itemData = (ItemData) itemList.get(position);
        id = itemData.getDataid();
        vh.getTv_title().setText(itemData.getTitle());
        vh.getTv_date().setText(itemData.getData());
        vh.getTv_size().setText(itemData.getSize());
        vh.getTv_content().setText(itemData.getContent());

        if (MySharedPreferences.getString(context,"ivSync",null) != null){
            vh.getIv_sync().setImageResource(R.drawable.synced);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



    private OnRecyclerViewItemClickListener mOnItemClickListener = null;//item 点击

    private OnRecyclerViewLongItemClickListener mOnLongItemClickListener = null;//item 长按

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int position, String id);
    }

    public static interface OnRecyclerViewLongItemClickListener {
        void onLongItemClick(View view, int position, String id);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnRecyclerViewLongItemClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    public static JSONObject MyJson(){

        JSONObject JsonOb = JsonObject;

        return JsonOb;
    }
}
