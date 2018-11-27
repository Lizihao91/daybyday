package com.liyyang.yunnote.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.liyyang.yunnote.R;
import com.liyyang.yunnote.adapter.YunPagerAdapter;
import com.liyyang.yunnote.utils.Db;
import com.liyyang.yunnote.utils.HttpUtil;
import com.liyyang.yunnote.utils.MySharedPreferences;
import com.liyyang.yunnote.utils.Login;
import com.liyyang.yunnote.utils.Zip4jUtil;
import com.liyyang.yunnote.view.FabBehavior;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, Toolbar.OnMenuItemClickListener, View.OnClickListener {

    FloatingActionButton fab;
    LinearLayout llBottom;

    YunPagerAdapter yunPagerAdapter;
    ViewPager mViewPager;

    RadioButton rb_doc, rb_corp, rb_mine;
    int menu_position;

    Toolbar toolbar;
    ImageView toolbar_login;

    MenuInflater inflater;

    final static String u = "upload";
    final static String s = "sync";//下载服务器or上传


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    private void init() {

        setImageSize();

        initToolbar();

        initFloatingActionButton();

        initRadiobutton();

        initViewPager();

    }

    /**
     * 同步状态
     */
    private void setSyncState() {
        ImageView ivSync = (ImageView) findViewById(R.id.iv_sync);
        ivSync.setImageResource(R.drawable.synced);
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        toolbar.setTitle("");
        toolbar_login = (ImageView) findViewById(R.id.toolbar_login);
        toolbar_login.setOnClickListener(this);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        File f = new File(Environment.getExternalStorageDirectory() + "/yunnote/cache/1.txt");
        if (!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void initRadiobutton() {
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        RadioGroup rg = (RadioGroup) findViewById(R.id.rG);
        rg.setOnCheckedChangeListener(this);
    }

    private void initViewPager() {
        yunPagerAdapter = new YunPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(yunPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initFloatingActionButton() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        invalidateOptionsMenu();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.hide();

                Intent i = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivityForResult(i, 5);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        menu_position = position;
        switch (position) {
            case 0:
                llBottom.setVisibility(View.VISIBLE);
                fab.show();
                rb_doc.setChecked(true);
                break;
            case 1:
                fab.setVisibility(View.GONE);
                new FabBehavior(this, null).show(llBottom);//弹出底部标签
                rb_corp.setChecked(true);
                break;
            case 2:
                fab.setVisibility(View.GONE);
                rb_mine.setChecked(true);
                break;
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        inflater = this.getMenuInflater();

        tbImageShowHide();

        switch (menu_position) {

            case 0:
                inflater.inflate(R.menu.menu_main, menu);
                break;

            case 1:
                inflater.inflate(R.menu.menu_corp, menu);
                break;

            case 2:
                inflater.inflate(R.menu.menu_mine, menu);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     *   控制toolbar图标的显示
     */
    private void tbImageShowHide() {

        if (toolbar_login.getVisibility() == View.VISIBLE && menu_position == 2) {
            toolbar_login.setVisibility(View.INVISIBLE);
        } else {
            toolbar_login.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 调整RadioButton图片大小
     */

    private void setImageSize() {

        rb_doc = (RadioButton) findViewById(R.id.rb_doc);
        rb_corp = (RadioButton) findViewById(R.id.rb_corp);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);

        Drawable doc = getResources().getDrawable(R.drawable.btn_doc_selector);
        Drawable corp = getResources().getDrawable(R.drawable.btn_corp_selector);
        Drawable mine = getResources().getDrawable(R.drawable.btn_mine_selector);

        doc.setBounds(0, 0, 80, 75);
        corp.setBounds(0, 0, 80, 75);
        mine.setBounds(0, 0, 80, 75);

        rb_doc.setCompoundDrawables(null, doc, null, null);
        rb_corp.setCompoundDrawables(null, corp, null, null);
        rb_mine.setCompoundDrawables(null, mine, null, null);
    }

    private void getSQL(String state) {
        String userId = MySharedPreferences.getString(this,"userId",null);
        String filePath = Environment.getExternalStorageDirectory() + "/yunnote/cache/";
        String zipPath = Environment.getExternalStorageDirectory() + "/yunnote/zip4j/"+userId+".zip";
        if (state.equals(s)){
            JSONObject jb = new Db().jsonFromSQL(state, this);
            HttpUtil h = new HttpUtil();
            h.httpUtil(this);
            h.uploadFile(jb, this, state, zipPath);
        }else {
            //压缩图片视频
            Zip4jUtil.zip(filePath, zipPath, false,"asdf");//路径，密码

            JSONObject jb = new Db().jsonFromSQL(state, this);
            HttpUtil h = new HttpUtil();
            h.httpUtil(this);
            h.uploadFile(jb, this, state, zipPath);

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_doc:
                mViewPager.setCurrentItem(0);
                System.out.println(mViewPager.getCurrentItem());
                break;

            case R.id.rb_corp:
                mViewPager.setCurrentItem(1);
                System.out.println(mViewPager.getCurrentItem());
                break;

            case R.id.rb_mine:
                mViewPager.setCurrentItem(2);
                System.out.println(mViewPager.getCurrentItem());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                toolbar.inflateMenu(R.menu.menu_setting);
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(i, 6);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.action_sync:
                //TODO 同步
                if (loginState()) {//下载服务器内容到本地
                    getSQL(s);
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(this, R.string.noLogin, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_upload:
                //TODO 上传
                if (loginState()) {//覆盖服务器内容
                    getSQL(u);
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(this, R.string.noLogin, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 5) {
            fab.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.toolbar_login:
                if (loginState()) {//toolBar登录状态
                    startActivity(new Intent(this, LoggedActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    startActivity(new Login().Login(this));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
        }
    }

    public boolean loginState() {//登录状态
        if (MySharedPreferences.getString(this, "state", null) != null) {
            if (MySharedPreferences.getString(this, "state", null).equals("login")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
