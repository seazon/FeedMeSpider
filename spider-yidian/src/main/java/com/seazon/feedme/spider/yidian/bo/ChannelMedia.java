package com.seazon.feedme.spider.yidian.bo;

import com.google.gson.annotations.SerializedName;

public class ChannelMedia {
    @SerializedName(value = "name", alternate = {"media_name"})
    public String name;
}
