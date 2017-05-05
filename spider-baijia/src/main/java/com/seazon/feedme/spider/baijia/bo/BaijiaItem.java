package com.seazon.feedme.spider.baijia.bo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaijiaItem {
    @SerializedName(value = "title", alternate = {"m_title"})
    public String title;
    @SerializedName(value = "url", alternate = {"m_display_url"})
    public String url;
    public long publishTime;
    @Expose(deserialize = false, serialize = true)
    @SerializedName(value = "publishTimeOri", alternate = {"m_create_time"})
    public String publishTimeOri;// 08:10, 05-03, 2016-10-11
    @SerializedName(value = "content", alternate = {"m_summary"})
    public String content;
    @SerializedName(value = "thumbnail", alternate = {"m_image_url"})
    public String thumbnail;
    @SerializedName(value = "author", alternate = {"m_writer_name"})
    public String author;
}
