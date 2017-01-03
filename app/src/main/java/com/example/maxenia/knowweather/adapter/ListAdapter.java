package com.example.maxenia.knowweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.maxenia.knowweather.R;

import java.util.ArrayList;

/**
 * Created by maxenia on 2016/12/28.
 * 列表适配器
 */
public class ListAdapter extends BaseAdapter {

    private ArrayList<String> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public ListAdapter(ArrayList<String> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item, null);
            holder.mText = (TextView) view.findViewById(R.id.TV_listItem);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.mText.setText(mList.get(i));

        return view;
    }

    class ViewHolder {
        public TextView mText;
    }
}
