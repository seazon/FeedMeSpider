package com.seazon.feedme.spider.baijia;

import com.google.gson.Gson;
import com.seazon.feedme.spider.BaseSpiderService;
import com.seazon.feedme.spider.SpiderFeed;
import com.seazon.feedme.spider.SpiderItem;
import com.seazon.feedme.spider.SpiderStream;
import com.seazon.feedme.spider.baijia.bo.BaijiaItem;
import com.seazon.feedme.spider.baijia.bo.BaijiaStream;
import com.seazon.feedme.spider.utils.LogUtils;
import com.seazon.feedme.spider.utils.http.HttpMethod;
import com.seazon.feedme.spider.utils.http.HttpUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpiderService extends BaseSpiderService {

    private static final int COUNT = 20;
    private static final String REQUEST_ITEM_URL = "http://baijia.baidu.com/ajax/authorlatestarticle?page=%2$s&authorid=%1$s&prevarticalid=1&pagesize=" + COUNT;
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");

    @Override
    public SpiderFeed getFeed(String url) {
        LogUtils.debug("getFeed, url:" + url);
        try {
            return clawFeed(url);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    @Override
    public SpiderStream getItems(String url, String continuation) {
        LogUtils.debug("getItems, url:" + url + ", continuation:" + continuation);
        if (continuation == null) {
            continuation = "1";
        }
        try {
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_ITEM_URL, url, continuation));
            return claw(r, Integer.parseInt(continuation));
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    private SpiderFeed clawFeed(String url) {
        SpiderFeed feed = new SpiderFeed();
        feed.title = url;
        return feed;
    }

    private SpiderStream claw(String r, int page) {
        String day = sdf2.format(new Date());
        String year = sdf3.format(new Date());
        BaijiaStream stream = BaijiaStream.parse(r);
        for (BaijiaItem item : stream.data.list) {
            try {
                if (item.publishTimeOri.matches("^\\d{1,2}(\\:)\\d{1,2}$")) {
                    item.publishTime = sdf1.parse(day + " " + item.publishTimeOri).getTime();
                } else if (item.publishTimeOri.matches("^\\d{1,2}(\\-)\\d{1,2}$")) {
                    item.publishTime = sdf2.parse(year + "-" + item.publishTimeOri).getTime();
                } else if (item.publishTimeOri.matches("^\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}$")) {
                    item.publishTime = sdf2.parse(item.publishTimeOri).getTime();
                }
            } catch (Exception e1) {
                item.publishTime = System.currentTimeMillis();
            }
        }

        SpiderStream localRssStream = new SpiderStream();
        if (stream.data.list.size() < COUNT) {
            localRssStream.continuation = null;
        } else {
            localRssStream.continuation = String.valueOf(page + 1);
        }

        localRssStream.items = SpiderItem.parseList(new Gson().toJson(stream.data.list));
        return localRssStream;
    }

}
