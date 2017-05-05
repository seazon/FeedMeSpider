package com.seazon.feedme.spider.yidian.bo;

import com.google.gson.Gson;

import java.util.List;

public class YidianStream {
    public List<YidianItem> result;
    public ChannelMedia channel_media;

    public static YidianStream parse(String json) {
        return new Gson().fromJson(json, YidianStream.class);
    }
}
