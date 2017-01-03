package com.example.maxenia.knowweather.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.example.maxenia.knowweather.R;
import com.example.maxenia.knowweather.adapter.ListAdapter;

import java.util.ArrayList;

public class SuggestActivity extends BaseActivity{

    private ListView mListView;
    private ArrayList<String> mList;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        mList = getIntent().getStringArrayListExtra("suggest"); //接收Intent传过来的List数组
        mAdapter = new ListAdapter(mList,this);

        mListView = (ListView) findViewById(R.id.LV_suggest);
        mListView.setAdapter(mAdapter);

    }
}
