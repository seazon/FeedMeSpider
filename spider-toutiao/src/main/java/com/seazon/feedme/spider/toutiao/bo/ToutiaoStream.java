package com.seazon.feedme.spider.toutiao.bo;

import com.google.gson.Gson;

import java.util.List;

public class ToutiaoStream {
    public ToutiaoStreamNext next;
    public List<ToutiaoStreamItem> data;

    public static ToutiaoStream parse(String json) {
        return new Gson().fromJson(json, ToutiaoStream.class);
    }
}
