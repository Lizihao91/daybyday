package com.liyyang.yunnote.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leeyang on 16/7/14.
 */
public class SQLiteUtil extends SQLiteOpenHelper {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String DATA = "data";
    public static final String SIZE = "size";
    public static final String DATAID = "dataid";

    public SQLiteUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table note(" +
                "_id INTEGER PRIMARY KEY," +
                "title text default \"\"," +
                "content text default \"\"," +
                "data text default \"\"," +
                "size text default \"\"," +
                "dataid text default \"\")");
        db.execSQL("create unique index title on note (title)");//唯一索引

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS note");
        onCreate(db);
    }
}
