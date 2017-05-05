package com.seazon.feedme.spider.baijia.bo;

import com.google.gson.Gson;

public class BaijiaStream {
    public BaijiaData data;

    public static BaijiaStream parse(String json) {
        return new Gson().fromJson(json, BaijiaStream.class);
    }
}
