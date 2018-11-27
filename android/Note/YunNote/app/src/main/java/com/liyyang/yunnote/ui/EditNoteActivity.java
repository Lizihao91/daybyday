package com.liyyang.yunnote.ui;

import java.util.Date;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.format.Time;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.domain.MessageSpan;
import com.liyyang.yunnote.method.LinkMovementMethodExt;
import com.liyyang.yunnote.utils.CopyFile;
import com.liyyang.yunnote.utils.MySharedPreferences;
import com.liyyang.yunnote.utils.SQLiteUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 编辑界面
 * Created by leeyang on 16/7/4.
 */
public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, View.OnFocusChangeListener {

    private static final int PHOTO_SUCCESS = 1;
    private static final int CAMERA_SUCCESS = 2;
    private static final int CLIP_SUCCESS = 3;

    HandlerThread thread;
    Handler mHandler;

    private HashMap<ImageSpan, String> iSpan = new HashMap<>();

    Button rbImage, rbClip, rbEdit;
    EditText etTile, etContent;
    TextView tvComplete, tvContent;
    ScrollView slView;

    SQLiteDatabase dbread;

    String title, content;
    String id, iView;
    String fileUri;

    int addFileSize = 0;

    static boolean viewM = true;
    boolean sInstanceS = false;

    Uri imgOutputUri;

    Integer i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        initView();
        initHandler();
        initToolbar();
    }


    private void initView() {

        //创建压缩文件夹
        File fileFolderY = new File(Environment.getExternalStorageDirectory() + "/yunnote/");
        File fileFolderZ = new File(Environment.getExternalStorageDirectory() + "/yunnote/zip4j/");
        if (!fileFolderY.exists()) {
            fileFolderY.mkdir();
        }
        if (!fileFolderZ.exists()) {
            fileFolderZ.mkdir();
        }

        etTile = (EditText) findViewById(R.id.et_title);
        etContent = (EditText) findViewById(R.id.et_content);
        rbImage = (Button) findViewById(R.id.rb_image);
        rbClip = (Button) findViewById(R.id.rb_clip);
        rbEdit = (Button) findViewById(R.id.rb_view_edit);
        tvComplete = (TextView) findViewById(R.id.tv_complete);
        tvContent = (TextView) findViewById(R.id.tv_content);
        slView = (ScrollView) findViewById(R.id.slView);

        slView.setOnTouchListener(this);
        rbEdit.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvContent.setOnClickListener(this);
        rbImage.setOnClickListener(this);
        rbClip.setOnClickListener(this);
        etTile.setOnFocusChangeListener(this);
        etContent.setOnFocusChangeListener(this);

        Intent i = getIntent();
        //判断intent是否有值
        id = i.getStringExtra("id");
        iView = i.getStringExtra("viewM");
        if (iView != null) {
            initData();

            setViewModel();
            viewM = false;
            initHandler();
            Spanned s = etContent.getText();
//            ImageSpan[] imageSpen = s.getSpans(0, s.length(), ImageSpan.class);
        } else if (id != null) {
            initData();
            viewM = true;
        }
    }

    /**
     * 浏览界面
     */
    private void setViewModel() {
        etTile.setEnabled(false);
        etContent.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);//??未显示
        rbClip.setVisibility(View.GONE);
        rbImage.setVisibility(View.GONE);
        tvComplete.setVisibility(View.GONE);
        rbEdit.setVisibility(View.VISIBLE);
        System.out.println("------------------------------------->>>>>>>");
    }

    /**
     * 编辑界面
     */

    private void setEditModel() {
        etTile.setEnabled(true);
//        etContent.setEnabled(true);
        etContent.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        rbClip.setVisibility(View.VISIBLE);
        rbImage.setVisibility(View.VISIBLE);
        tvComplete.setEnabled(true);
        tvComplete.setVisibility(View.VISIBLE);
        rbEdit.setVisibility(View.GONE);
    }

    private void insertSQLite() {

        //获取当前时间
        Time t = new Time();
        t.setToNow();
        String time = String.valueOf(t.year + "." + t.month + "." + t.monthDay + " " + t.hour + ":" + t.minute + ":" + t.second);
        //获取文本长度
        //TODO 获取不到uri？
        String content = etContent.getText().toString();
        Pattern p = Pattern.compile("/storage(.*?)jpg");
        Matcher m = p.matcher(content);
        Pattern p1 = Pattern.compile("/storage(.*?)mp4");
        Matcher m1 = p1.matcher(content);
//        iSpan.clear();
        while (m.find()) {
            String uri1 = "/storage" + m.group(1) + "jpg";
            addFileSize += new File(uri1).length();
            System.out.println("加入的文件大小"+addFileSize);
        }
        while (m1.find()) {
            String uri = "/storage" + m1.group(1) + "mp4";
            addFileSize += new File(uri).length();
        }



        String size = Formatter.formatFileSize(this, etTile.length() + etContent.length() + addFileSize);

        SQLiteUtil SQLite = new SQLiteUtil(this, "note.db3", null, 1);
        SQLiteDatabase database = SQLite.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String dataIDT = time + " " + t.second;
        String dataID = String.valueOf(System.currentTimeMillis());
        //空标题自动添加
        if (etTile.getText().length() == 0) {
            cv.put("title", "无标题笔记" + dataIDT);
        } else {
            cv.put("title", String.valueOf(etTile.getText()));
        }
        cv.put("content", String.valueOf(etContent.getText()));
        cv.put("data", time);
        cv.put("size", size);
        cv.put("dataid", dataID);

//        database.insert("note", null, cv);
        database.replace("note", null, cv);//更新
        SQLite.close();
    }

    private void initToolbar() {
        Toolbar toolbar_edit = (Toolbar) findViewById(R.id.toolbar_edit);
        toolbar_edit.setTitle("");
        setSupportActionBar(toolbar_edit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar_edit.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSQLite();
                finish();
            }
        });
    }

    //读取数据库
    private void initData() {
        SQLiteUtil db = new SQLiteUtil(this, "note.db3", null, 1);
        dbread = db.getReadableDatabase();
        //删除编辑前的数据
        Cursor cursor = dbread.query("note", null, "dataid=?", new String[]{id}, null, null, null);
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
        }
        etTile.setText(title);

        //用span插入图片视频截图

        SpannableString ss = new SpannableString(content);
        Pattern p = Pattern.compile(getString(R.string.jpg));
        Matcher m = p.matcher(content);
        Pattern p1 = Pattern.compile("/storage(.*?)mp4");
        Matcher m1 = p1.matcher(content);
        Bitmap rbm = null;
        Bitmap bm = null;
        Bitmap bitmap1 = null;
        Bitmap originalBitmap2 = null;
        iSpan.clear();
        while (m.find()) {
            String uri1 = "/storage" + m.group(1) + "jpg";
            bm = BitmapFactory.decodeFile(uri1);
            System.out.println(m.group(1));
            new File(uri1).length();
            rbm = resizeImage(bm, 200, 200);
            ImageSpan span = new ImageSpan(this, rbm);
            iSpan.put(span, uri1);
            ss.setSpan(span, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            etContent.setText(ss);
            tvContent.setText(ss);
        }
        while (m1.find()) {//插入视频？？
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            String uri = "/storage" + m1.group(1) + "mp4";
            media.setDataSource(uri);

            originalBitmap2 = media.getFrameAtTime();
            bitmap1 = resizeImage(media.getFrameAtTime(), 200, 150);
            ImageSpan imageSpan = new ImageSpan(this, bitmap1);
            String imgUri = uri;
            iSpan.put(imageSpan, uri);
//            ss.setSpan(imageSpan, 0, imgUri.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(imageSpan, m1.start(), m1.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            etContent.setText(ss);
            tvContent.setText(ss);
        }

        if (rbm == null && originalBitmap2 == null) {
            etContent.setText(content);
        }
//        dbread.execSQL("delete from note where dataid = ?", new Object[]{id});
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rb_image:
                final CharSequence[] items = {"相册", "相机"};
                new AlertDialog.Builder(this).setTitle("选择图片来源").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which] == "相机") {
                            Date date = new Date();//获取时间？方法
                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                            File fileFolder = new File(Environment.getExternalStorageDirectory() + "/yunnote/cache/");
                            if (!fileFolder.exists()) {
                                fileFolder.mkdir();
                            }
                            File file = new File(fileFolder, format.format(date) + ".jpg");
                            imgOutputUri = Uri.fromFile(file);

                            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, imgOutputUri);
                            startActivityForResult(getImageByCamera, CAMERA_SUCCESS);
                        } else {
//                            Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                            Intent getImage = new Intent(Intent.ACTION_PICK);//adk 4.4以下
                            getImage.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            getImage.addCategory(Intent.CATEGORY_OPENABLE);
                            getImage.setType("image/*");
                            startActivityForResult(getImage, PHOTO_SUCCESS);
                        }
                    }
                }).create().show();
                break;

            case R.id.rb_clip:
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                File fileFolderclip = new File(Environment.getExternalStorageDirectory() + "/yunnote/cache/");
                if (!fileFolderclip.exists()) {
                    fileFolderclip.mkdir();
                }
                File clipFile = new File(fileFolderclip, format.format(date) + ".mp4");
//                clipOutputUri = Uri.fromFile(clipFile);
                Intent getClip = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(getClip, CLIP_SUCCESS);
                break;


            case R.id.tv_complete:
                insertSQLite();//不生效
                finish();
                break;

            case R.id.rb_view_edit:
                setEditModel();
                viewM = true;
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewM) {
                new AlertDialog.Builder(this).setTitle("是否保存")
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertSQLite();
                        finish();
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            }else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.slView:
                //编辑文本点击任意区域框获取焦点？？输入法弹出
                if (etContent.isEnabled()) {//编辑状态弹出
                    etContent.setFocusable(true);
                    etContent.setFocusableInTouchMode(true);
                    etContent.requestFocus();
                    etContent.setCursorVisible(true);
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//显示则关闭，关闭则显示
//                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    imm.showSoftInput(etContent, 0);
                }
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_SUCCESS:
                    //获得图片的uri
                    Uri originalUri = data.getData();

                    String[] proj = {MediaStore.Images.Media.DATA};
                    CursorLoader loader = new CursorLoader(this, originalUri, proj, null, null, null);
                    Cursor cursor = loader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);

                    Time t = new Time();//当前时间？过时
                    t.setToNow();//复制文件到指定目录，使用原文件名
                    String time = String.valueOf(t.year + "." + t.month + "." + t.monthDay + " " + t.hour + ":" + t.minute + ":" + t.second);
                    new CopyFile().CopyFile(path, Environment.getExternalStorageDirectory() + "/yunnote/cache/");
                    File nFile = new File(path);
                    String newPath = Environment.getExternalStorageDirectory() + "/yunnote/cache/" + nFile.getName();

                    System.out.println("获取到图库图片的uri" + path);
                    Bitmap bitmap = null;
                    Bitmap originalBitmap = null;
                    try {
                        originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse("file:///" + newPath)));
                        bitmap = resizeImage(originalBitmap, 200, 200);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (originalBitmap != null) {
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(this, bitmap);
                        //创建一个SpannableString对象,插入用ImageSpan对象封装的图像
                        String imgUri = newPath;
                        SpannableString spannableString = new SpannableString(imgUri);
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, imgUri.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        insertIntoEt(spannableString);
                    } else {
                        Toast.makeText(this, R.string.faildgetImage, Toast.LENGTH_SHORT).show();
                    }
                    break;

                // TODO: 权限问题，6.0+不能获取图片
                case CAMERA_SUCCESS:
//                    Bundle extras = data.getExtras();
//                    Bitmap originalBitmap1 = (Bitmap) extras.get("data");
                    Bitmap originalBitmap1 = null;
                    try {
                        System.out.println(imgOutputUri);
                        originalBitmap1 = BitmapFactory.decodeStream(resolver.openInputStream(imgOutputUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (originalBitmap1 != null) {
                        bitmap = resizeImage(originalBitmap1, 200, 200);
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(this, bitmap);
                        //创建一个SpannableString对象,插入用ImageSpan对象封装的图像
                        String imgUri = imgOutputUri.getPath();
                        SpannableString spannableString = new SpannableString(imgUri);
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, imgUri.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        insertIntoEt(spannableString);
                        int No = ++i;
                        MySharedPreferences.setString(this, "imgNo", String.valueOf(No));
                    } else {
                        Toast.makeText(this, R.string.faildgetImage, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case CLIP_SUCCESS:
                    //TODO 录制视频
                    Uri clipUri = data.getData();
                    System.out.println(clipUri);
                    String[] proj1 = {MediaStore.Images.Media.DATA};
                    CursorLoader loader1 = new CursorLoader(this, clipUri, proj1, null, null, null);
                    Cursor cursor1 = loader1.loadInBackground();
                    int column_index1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor1.moveToFirst();
                    String path1 = cursor1.getString(column_index1);

                    Time t1 = new Time();//当前时间？过时
                    t1.setToNow();//复制文件到指定目录，重命名文件
                    String time1 = String.valueOf(t1.year + "." + t1.month + "." + t1.monthDay + " " + t1.hour + ":" + t1.minute + ":" + t1.second);
                    new CopyFile().CopyFile(path1, Environment.getExternalStorageDirectory() + "/yunnote/cache/");
                    File zFile = new File(path1);//获取原文件名
                    String newPathClip = Environment.getExternalStorageDirectory() + "/yunnote/cache/" + zFile.getName();

//                    System.out.println("获取到图库视频的uri" + path1);
                    MediaMetadataRetriever media = new MediaMetadataRetriever();//获取缩略图
                    media.setDataSource(path1);

                    Bitmap bitmap1 = null;
                    Bitmap originalBitmap2 = null;
                    // originalBitmap2 = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse("file:///"+path1)));
                    originalBitmap2 = media.getFrameAtTime();
                    bitmap1 = resizeImage(media.getFrameAtTime(), 200, 150);
                    if (originalBitmap2 != null) {
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(this, bitmap1);
                        //创建一个SpannableString对象,插入用ImageSpan对象封装的图像
                        String imgUri = newPathClip;
                        SpannableString spannableString = new SpannableString(imgUri);
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, imgUri.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        insertIntoEt(spannableString);
                    } else {
                        Toast.makeText(this, R.string.faildgetClip, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void insertIntoEt(SpannableString spannableString) {
        //将选择的图片视频追加到EditText中光标所在位置
        int index = etContent.getSelectionStart(); //获取光标所在位置
        Editable edit_text = etContent.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append("\r");
            edit_text.append(spannableString);
            edit_text.append("\r");
        } else {
            edit_text.insert(index, spannableString);
        }
    }

    private Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        //计算宽、高缩放率
        float scanleWidth = (float) newWidth / width;
        float scanleHeight = (float) newHeight / height;
        //创建操作图片用的matrix对象 Matrix
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scanleWidth, scanleHeight);
        //旋转图片 动作
        //matrix.postRotate(45);
        // 创建新的图片Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_title://标题不能插入图片
                rbImage.setEnabled(false);
                rbClip.setEnabled(false);
                break;

            case R.id.et_content:
                rbImage.setEnabled(true);
                rbClip.setEnabled(true);
                break;
        }
    }

    private void initHandler() {//处理点击
        thread = new HandlerThread("Mythread");
        thread.start();
        mHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == 200) {
                    MessageSpan ms = (MessageSpan) msg.obj;
                    Object[] spans = (Object[]) ms.getObj();
                    for (Object span : spans) {
                        if (span instanceof ImageSpan) {

//                            Set<Map.Entry<ImageSpan,String>> entrySet = iSpan.entrySet();
//                            for(Map.Entry<ImageSpan, String> entry: entrySet){
//                            }

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            fileUri = iSpan.get(span);
                            if (fileUri != null && fileUri.contains(".jpg")) {
                                i.setDataAndType(Uri.parse("file://" + fileUri), "image/*");
                            } else if (fileUri != null && fileUri.contains(".mp4")) {
                                i.setDataAndType(Uri.parse("file://" + fileUri), "video/mp4");
                            }
                            startActivity(i);
                        }
                    }
                }
            }
        };
        //TODO Imagespan点击？
        tvContent.setMovementMethod(LinkMovementMethodExt.getInstance(mHandler, ImageSpan.class));
        sInstanceS = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.quit();
    }
}
