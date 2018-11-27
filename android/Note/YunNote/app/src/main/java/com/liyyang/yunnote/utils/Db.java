package com.liyyang.yunnote.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liyyang.yunnote.domain.ItemData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leeyang on 16/7/26.
 */
public class Db {

    public void db(String state, Context context){
    }

    public JSONObject jsonFromSQL(String state, Context context) {
        ArrayList<Object> itemList = new ArrayList<>();
        itemList.clear();
        SQLiteUtil db = new SQLiteUtil(context,"note.db3",null,1);
        SQLiteDatabase dbread = db.getReadableDatabase();
        Cursor cursor = dbread.query("note",null, null, null, null, null, null);
        MyJsonUtil j = new MyJsonUtil();
        JSONObject jsonObject = new JSONObject();
        JSONArray a = new JSONArray();
        JSONArray b = new JSONArray();
        while (cursor.moveToNext()){
            a = j.mJson(b,cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("size")),
                    cursor.getString(cursor.getColumnIndex("dataid")),
                    cursor.getString(cursor.getColumnIndex("data")));
            jsonObject = j.tJson(a,jsonObject);
        }
        db.close();
        return jsonObject;
    }

    public SQLiteDatabase itemDataFromSQL(ArrayList itemList, Context context){
        if (itemList == null){
            itemList = new ArrayList<>();
        }else {
            itemList.clear();
        }

        SQLiteUtil db = new SQLiteUtil(context,"note.db3",null,1);
        SQLiteDatabase dbread = db.getReadableDatabase();
        Cursor cursor = dbread.query("note",null, null, null, null, null, null);

        while (cursor.moveToNext()){

            ItemData itemData = new ItemData(cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("data")),
                    cursor.getString(cursor.getColumnIndex("size")),
                    cursor.getString(cursor.getColumnIndex("dataid")));
            itemList.add(itemData);
        }
        Collections.sort(itemList);//list排序
        return dbread;
    }
}
