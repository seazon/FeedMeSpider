package com.seazon.feedme.spider.loader;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seazon.feedme.spider.SpiderItem;

import java.text.SimpleDateFormat;
import java.util.Date;

import kale.adapter.item.AdapterItem;

public class Item implements AdapterItem<SpiderItem>, View.OnClickListener {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    MainActivity activity;
    TextView title;
    TextView publishTime;
    TextView author;
    TextView content;
    ImageView thumbnail;
    View layout;
    SpiderItem item;

    public Item(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_layout;
    }

    @Override
    public void bindViews(View view) {
        layout = view.findViewById(R.id.layout);
        title = (TextView) view.findViewById(R.id.title);
        publishTime = (TextView) view.findViewById(R.id.publishTime);
        author = (TextView) view.findViewById(R.id.author);
        content = (TextView) view.findViewById(R.id.content);
        content = (TextView) view.findViewById(R.id.content);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    }

    @Override
    public void setViews() {
        layout.setOnClickListener(this);
    }

    @Override
    public void handleData(SpiderItem item, int i) {
        this.item = item;

        title.setText(item.title);
        publishTime.setText(sdf.format(new Date(item.publishTime)));
        author.setText(item.author);
        content.setText(item.content);
//        Glide.with(activity).load(item.thumbnail).into(thumbnail);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(item.url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
