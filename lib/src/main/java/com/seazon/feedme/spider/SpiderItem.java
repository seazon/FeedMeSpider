package com.seazon.feedme.spider;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SpiderItem extends Entity {
    public String title;
    public String url;
    public long publishTime;
    public String content;
    public String thumbnail;
    public String author;

    public static List<SpiderItem> parseList(String json) throws JsonSyntaxException {
        return new Gson().fromJson(json, new TypeToken<List<SpiderItem>>() {
        }.getType());
    }
}
