package com.example.maxenia.knowweather.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.example.maxenia.knowweather.R;
import com.example.maxenia.knowweather.adapter.ListAdapter;

import java.util.ArrayList;

public class AboutActivity extends BaseActivity {

    private ListView mAboutListView;
    private ArrayList<String> mList = new ArrayList<>();
    private ListAdapter mAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAboutListView = (ListView) findViewById(R.id.LV_about);

        mList.add("Author:"+getString(R.string.author));
        mList.add("QQ：555974449");
        mList.add("Blog:http://blog.csdn.net/yyq_max");

        mAdapter = new ListAdapter(mList,this);

        mAboutListView.setAdapter(mAdapter);

    }

}
