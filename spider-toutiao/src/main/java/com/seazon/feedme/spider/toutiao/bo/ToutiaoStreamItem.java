package com.seazon.feedme.spider.toutiao.bo;

import com.google.gson.annotations.SerializedName;

public class ToutiaoStreamItem {
    public String title; // 国产全新CR-V上市时间曝光：1.5T/混动齐发,
    @SerializedName(value = "url", alternate = {"display_url"})
    public String url; // http:/./m.huanqiu.com/r/MV8wXzEwNTc3OTA4XzQ4XzE0OTM3MTE3MjU=,
    @SerializedName(value = "publishTime", alternate = {"behot_time"})
    public long publishTime;// 1493711725,
    @SerializedName(value = "content", alternate = {"abstract"})
    public String content; // 在上海车展上,东风本田正式首发了国产全新一代CR-V车型。目前最新的消息显示,新车将于7月份在国内正式上市,而且混动版会同步发售。外观方面,国产全新CR-V在外形方面和海外版高度一致,相比现款来说整体造型也不是很明显,只是换装了全新的家族化前脸,并在细节方面进行了优化。,
    @SerializedName(value = "thumbnail", alternate = {"image_url"})
    public String thumbnail;// http:/./p3.pstatp.com/list/190x124/1e190007ccceeb7e34ee,
}
