package com.seazon.feedme.spider.toutiao.bo;

import com.google.gson.annotations.SerializedName;

public class ToutiaoStreamItem {
    public String title;
    @SerializedName(value = "url", alternate = {"display_url"})
    public String url;
    @SerializedName(value = "publishTime", alternate = {"behot_time"})
    public long publishTime; // 1493711725,
    @SerializedName(value = "content", alternate = {"abstract"})
    public String content;
    @SerializedName(value = "thumbnail", alternate = {"image_url"})
    public String thumbnail;
}
