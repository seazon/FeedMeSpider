package com.seazon.feedme.spider.yidian.bo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YidianItem {
    public String title;
    public String url;
    public long publishTime;
    @Expose(deserialize = false, serialize = true)
    @SerializedName(value = "publishTimeOri", alternate = {"date"})
    public String publishTimeOri;// "2016-09-06 15:12:17"
    @SerializedName(value = "content", alternate = {"summary"})
    public String content;
    @SerializedName(value = "thumbnail", alternate = {"image"})
    public String thumbnail;
}
