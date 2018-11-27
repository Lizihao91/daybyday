package com.liyyang.yunnote.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.adapter.RecyclerVAdapter;
import com.liyyang.yunnote.ui.EditNoteActivity;
import com.liyyang.yunnote.domain.ItemData;
import com.liyyang.yunnote.utils.Db;
import com.liyyang.yunnote.utils.MySharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by leeyang on 16/7/4.
 */
public class DocFragment extends Fragment implements RecyclerVAdapter.OnRecyclerViewItemClickListener, RecyclerVAdapter.OnRecyclerViewLongItemClickListener {

    private ArrayList<Object> itemList;
    RecyclerVAdapter recyclerVAdapter;
    RecyclerView rv;
    SQLiteDatabase dbread;
    static JSONObject jb;
    ReRceiver reRceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_doc, null);

        initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        if (MySharedPreferences.getString(getActivity(), "ivSync", null) == "Y") {//更改同步图标
            ImageView ivSync = (ImageView) rootView.findViewById(R.id.iv_sync);
            if (ivSync != null) {
                ivSync.setImageResource(R.drawable.synced);
            }
        }
        itemDataFromSQL();
        initRecyclerView(rootView);

        jb = recyclerVAdapter.MyJson();

        initReceiver();
    }

    private void itemDataFromSQL() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        } else {
            itemList.clear();
        }
        Db db = new Db();
        dbread = db.itemDataFromSQL(itemList, getActivity());
    }

    private void initRecyclerView(View rootView) {
        rv = (RecyclerView) rootView.findViewById(R.id.rvDoc);
        LinearLayoutManager linearLayoutManager = null;

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            linearLayoutManager = new LinearLayoutManager(getContext());
//        }

        linearLayoutManager = new LinearLayoutManager(getActivity());

        rv.setLayoutManager(linearLayoutManager);

        recyclerVAdapter = new RecyclerVAdapter(itemList, getActivity());
        rv.setAdapter(recyclerVAdapter);
        recyclerVAdapter.setOnItemClickListener(this);
        recyclerVAdapter.setOnLongItemClickListener(this);
        recyclerVAdapter.MyJson();
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }


    @Override
    public void onItemClick(View view, final int position, final String dataID) {
        ItemData itemData = (ItemData) itemList.get(position);
        final String strr = itemData.getDataid();
//        Toast.makeText(getActivity(), "点击", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), EditNoteActivity.class);
        i.putExtra("id", strr);
        i.putExtra("viewM", "ok");
        startActivity(i);
    }

    @Override
    public void onLongItemClick(View view, int position, String id) {

        ItemData itemData = (ItemData) itemList.get(position);
        final String str = itemData.getDataid();
        //长按，删除编辑对话框
        final CharSequence[] items = {"删除", "编辑"};
        new AlertDialog.Builder(getActivity()).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which] == "删除") {

                    System.out.println(str);
                    dbread.execSQL("delete from note where dataid = ?", new Object[]{str});
                    //刷新
                    refreshView();
                } else {
                    Intent i = new Intent(getActivity(), EditNoteActivity.class);
                    i.putExtra("id", str);
                    startActivity(i);
                }
            }
        }).create().show();

    }

    public void refreshView() {
        itemDataFromSQL();//刷新
        recyclerVAdapter.notifyDataSetChanged();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter(ReRceiver.ACTION);
        intentFilter.addAction("refresh");
        reRceiver = new ReRceiver();
        getActivity().registerReceiver(reRceiver, intentFilter);
    }

    //广播接收
    public class ReRceiver extends BroadcastReceiver {
        public static final String ACTION = "com.liyyang.yunnote.adapter.intent.action.ReReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接到的广播"+intent.getStringExtra("msg"));
            refreshView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(reRceiver);
    }
}
