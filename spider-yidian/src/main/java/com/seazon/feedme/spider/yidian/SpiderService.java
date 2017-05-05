package com.seazon.feedme.spider.yidian;

import com.google.gson.Gson;
import com.seazon.feedme.spider.BaseSpiderService;
import com.seazon.feedme.spider.SpiderFeed;
import com.seazon.feedme.spider.SpiderItem;
import com.seazon.feedme.spider.SpiderStream;
import com.seazon.feedme.spider.utils.LogUtils;
import com.seazon.feedme.spider.utils.http.HttpMethod;
import com.seazon.feedme.spider.utils.http.HttpUtils;
import com.seazon.feedme.spider.yidian.bo.YidianItem;
import com.seazon.feedme.spider.yidian.bo.YidianStream;

import java.text.SimpleDateFormat;

public class SpiderService extends BaseSpiderService {

    private static final int COUNT = 20;
    private static final String REQUEST_ITEM_URL = "http://www.yidianzixun.com/home/q/news_list_for_channel?channel_id=m%1$s&cstart=%2$s&cend=%3$s";
    private static final String REQUEST_FEED_URL = "http://www.yidianzixun.com/home/q/news_list_for_channel?channel_id=m%1$s&cstart=0&cend=1";
    private static final String REQUEST_IMAGE_URL = "http://i1.go2yd.com/image.php?type=thumbnail_336x216&url=%s";
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public SpiderFeed getFeed(String url) {
        LogUtils.debug("getFeed, url:" + url);
        try {
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_FEED_URL, url));
            return clawFeed(r, url);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    @Override
    public SpiderStream getItems(String url, String continuation) {
        LogUtils.debug("getItems, url:" + url + ", continuation:" + continuation);
        if (continuation == null) {
            continuation = "0";
        }
        try {
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_ITEM_URL, url, continuation, Integer.parseInt(continuation) + COUNT));
            return claw(r, continuation);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    private SpiderFeed clawFeed(String r, String url) {
        YidianStream stream = YidianStream.parse(r);
        SpiderFeed feed = new SpiderFeed();
        feed.title = stream.channel_media.name;
        return feed;
    }

    private SpiderStream claw(String r, String continuation) {
        YidianStream toutiaoStream = YidianStream.parse(r);
        for (YidianItem item : toutiaoStream.result) {
            try {
                item.publishTime = sdf1.parse(item.publishTimeOri).getTime();
            } catch (Exception e1) {
                item.publishTime = System.currentTimeMillis();
            }
            item.thumbnail = String.format(REQUEST_IMAGE_URL, item.thumbnail);
        }

        SpiderStream localRssStream = new SpiderStream();
        if (toutiaoStream.result.size() < COUNT) {
            localRssStream.continuation = null;
        } else {
            localRssStream.continuation = String.valueOf(Integer.parseInt(continuation) + COUNT);
        }
        localRssStream.items = SpiderItem.parseList(new Gson().toJson(toutiaoStream.result));
        return localRssStream;
    }

}
