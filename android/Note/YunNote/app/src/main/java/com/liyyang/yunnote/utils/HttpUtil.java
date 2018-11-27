package com.liyyang.yunnote.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.view.DocFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leeyang on 16/7/19.
 */
public class HttpUtil {

    Context context;
    int unZipState = 0;//解压状态
    boolean flag = false; //解压状态
    ProgressDialog dialog;

    static List<Cookie> cookies;
    final static String url = "http://1.yunnotep.applinzi.com/upload";//服务器端地址
//    final static String url = "http://192.168.2.153:8080/upload";

    String urlDownload;

    String userId;
    public void httpUtil(Context context){
        this.context = context;
    }

    public void uploadFile(final JSONObject jb, final Context context, String state, final String s)
    {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setConnectTimeout(5);
        InputStream is = null;
        if (jb != null){
             is = new ByteArrayInputStream(jb.toString().getBytes());
        }

        final RequestParams param = new RequestParams();
        userId = MySharedPreferences.getString(context,"userId",null);//空指针

        if (state.equals("upload")){//判断是否上传或同步
            showProgress(state);
            param.put("nameid",userId);
            param.put("unote", is);
        }else if (state.equals("sync")){
            showProgress(state);
            param.put("nameid",userId);
            param.put("snote", "sync");
        }

        httpClient.post(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //将responseBody转为string
                    InputStream is = new ByteArrayInputStream(responseBody);
                    BufferedReader bf=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    StringBuffer buffer=new StringBuffer();
                    String line="";
                    while((line=bf.readLine())!=null){
                        buffer.append(line);
                    }
                    bf.close();

                    if (httpClient == null) {//存储cookie
                        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                        httpClient.setCookieStore(myCookieStore);
                    }
                    if(buffer.toString().equals("success001")) {
                        uploadFileZip(context,s);
                    }else if (buffer.toString().length()> 10){//用长度判断是否返回数据
                        //TODO 服务器返回的数据 连接超时？
                        sqlFromService(context,buffer.toString());//写入数据库
                        System.out.println("准备写入数据库"+buffer.toString());
                        loadFileZip(context);
                        //TODO 删除已经被移除笔记的本地视频图片？
                        FileWriter fw = new FileWriter(context.getFilesDir()+"serviceJson.txt");//写入本地文件
                        fw.write(buffer.toString());
                        fw.flush();
                        fw.close();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(headers);
                System.out.println(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });

    }

    private void showProgress(String state) {
        dialog = new ProgressDialog(context);
//        dialog.setCancelable(false);
        if (state.equals("upload")){
            dialog.setMessage("正在上传...");
        }else {
            dialog.setMessage("正在同步...");
        }
        dialog.show();
    }

    private void uploadFileZip(final Context context, String path) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setConnectTimeout(5000);
        File zipFile = new File(path);
        RequestParams param = new RequestParams();
        try {
            param.put("zipFile",zipFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        httpClient.post(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String s = new String(responseBody,"utf-8");
                    if (s.equals("success")){
                        MySharedPreferences.setString(context, "ivSync", "Y");
                        Toast.makeText(context, R.string.uploadOk,Toast.LENGTH_SHORT).show();
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void loadFileZip(final Context context) {//下载zip

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                urlDownload = url +"/"+userId+".zip";
                String dirName = "";
                dirName = Environment.getExternalStorageDirectory()+"/yunnote/dload";
                File f = new File(dirName);
                if(!f.exists()){
                    f.mkdir();
                }

                String newFilename = dirName+"/"+userId+".zip";
                System.out.println(newFilename);
                File file = new File(newFilename);
                if(file.exists()){
                    file.delete();
                }
                try {
                    URL url = new URL(urlDownload);
                    URLConnection con = url.openConnection();
                    //int contentLength = con.getContentLength();
                    InputStream is = con.getInputStream();
                    byte[] b = new byte[1024];
                    int len;
                    OutputStream os = new FileOutputStream(newFilename);
                    while ((len = is.read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                    os.close();
                    is.close();
                    File unZipFile = new File(Environment.getExternalStorageDirectory()+"/yunnote/dload/"+userId+".zip");
                    if (unZipFile.exists()){
                        unZipState = Zip4jUtil.unzip(unZipFile,Environment.getExternalStorageDirectory()+"/yunnote/cache/","asdf");
                        if (unZipState == 1 && flag == false){
                            syncState();
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * 登录
     * @param name
     * @param password
     * @param context
     */
    public void login(final String name, String password, final Context context){

        FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
        AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();

        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);

        RequestParams param = new RequestParams();

        param.put("loginname", name);
        param.put("loginpass", password);

        client.get(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String login = new String(responseBody,"utf-8");
                    if (login.equals("login")){
                        MySharedPreferences.setString(context,"state","login");
                        MySharedPreferences.setString(context,"userId",name);
                        Toast.makeText(context, R.string.loginOK, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, R.string.loginNo, Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, R.string.loginNo, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 注册
     * @param name
     * @param password
     * @param context
     */
    public void reg(String name, String password, final Context context){

        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setConnectTimeout(5);

        RequestParams param = new RequestParams();

        param.put("regname", name);
        param.put("regpass", password);

        httpClient.get(url, param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String reg = new String(responseBody,"utf-8");
                    if (reg.equals("regok")){
                        MySharedPreferences.setString(context,"reg","regok");
                        Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                    }else if (reg.equals("regOver")){
                        Toast.makeText(context, "使用其他用户名", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class FinalAsyncHttpClient {

        AsyncHttpClient client;

        public FinalAsyncHttpClient() {
            client = new AsyncHttpClient();
            client.setConnectTimeout(5);//5s超时
            //TODO cookie 清空??
//            if (MySharedPreferences.getString(context,"state",null) == null){
//                cookies.clear();
//            }
            if ((cookies != null ? cookies : new ArrayList<Cookie>()) != null) {
                BasicCookieStore bcs = new BasicCookieStore();
                bcs.addCookies((cookies != null ? cookies : new ArrayList<Cookie>()).toArray(
                        new Cookie[(cookies != null ? cookies : new ArrayList<Cookie>()).size()]));
                client.setCookieStore(bcs);
            }
        }
        public AsyncHttpClient getAsyncHttpClient(){
            return this.client;
        }
    }

    /*
     *将服务器传回的数据存储到sqlite
     */
    private void sqlFromService(Context context, String service){
        String serviceData = service;
        try{
            JSONObject jObj = new JSONObject(serviceData);
            JSONArray data = jObj.getJSONArray("note");

            for(int i=0;i<data.length();i++){

                JSONObject e = data.getJSONObject(i);

                SQLiteUtil databaseHelper = new SQLiteUtil(context,"note.db3", null, 1);

                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(SQLiteUtil.TITLE, e.getString("title"));
                cv.put(SQLiteUtil.CONTENT, e.getString("content"));
                cv.put(SQLiteUtil.DATA, e.getString("data"));
                cv.put(SQLiteUtil.SIZE, e.getString("size"));
                cv.put(SQLiteUtil.DATAID, e.getString("dataid"));

                //db.insert("note",null, cv);
                //db.insertWithOnConflict("note", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                //db.update("note",cv,"?","note");
                db.replace("note", null, cv);//TODO 写入数据库，数据重复？
                db.close();
                loadFileZip(context);
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
        }
    }


    private void syncState() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @SuppressLint("HandlerLeak")//?????
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    if (unZipState == 1){
                        if (dialog != null){
                            dialog.dismiss();
                        }
                        Toast.makeText(context, R.string.syncOk, Toast.LENGTH_SHORT).show();
                        MySharedPreferences.setString(context, "ivSync", "Y");
                        Intent i = new Intent(DocFragment.ReRceiver.ACTION);
                        i.putExtra("msg","refreshView");
                        context.sendBroadcast(i);
                        flag = true;
                        break;
                    }
                    syncState();
                    break;
                default:
                    break;
            }
        }
    };
}
