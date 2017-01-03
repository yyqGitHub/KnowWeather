package com.example.maxenia.knowweather.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.maxenia.knowweather.R;
import com.example.maxenia.knowweather.adapter.ListAdapter;

import java.util.ArrayList;

public class SettingActivity extends BaseActivity {

    private ListView mListView;
    private ListAdapter mListAdapter;
    private ArrayList<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setMlist();
        mListAdapter = new ListAdapter(mList, this);
        mListView = (ListView) findViewById(R.id.LV_setting);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        //点击版本号显示
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                        dialog.setTitle(R.string.version_code)
                                .setMessage(getVersionCode())
                                .create()
                                .show();
                        break;
                    case 1:
                        //点击意见反馈显示
                        startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
                        break;
                    case 2:
                        //点击关于显示
                        startActivity(new Intent(SettingActivity.this,AboutActivity.class));
                        break;
                }
            }
        });

        // initView();
    }

    //设置列表值
    private void setMlist() {
        mList.add(getString(R.string.version) + getVersion());
        mList.add(getString(R.string.feedback));
        mList.add(getString(R.string.about));
    }

    private String getVersion() {

        PackageManager PM = getPackageManager();
        try {
            PackageInfo info = PM.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.cannot_get_version);
        }
    }

    private String getVersionCode() {

        PackageManager PM = getPackageManager();
        try {
            PackageInfo info = PM.getPackageInfo(getPackageName(), 0);
            return info.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getString(R.string.cannot_get_version_code);
        }
    }

}
