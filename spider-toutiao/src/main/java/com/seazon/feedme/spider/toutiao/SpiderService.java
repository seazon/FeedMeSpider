package com.seazon.feedme.spider.toutiao;

import com.google.gson.Gson;
import com.seazon.feedme.spider.BaseSpiderService;
import com.seazon.feedme.spider.SpiderFeed;
import com.seazon.feedme.spider.SpiderItem;
import com.seazon.feedme.spider.SpiderStream;
import com.seazon.feedme.spider.toutiao.bo.MediaInfo;
import com.seazon.feedme.spider.toutiao.bo.ToutiaoStream;
import com.seazon.feedme.spider.toutiao.bo.ToutiaoStreamItem;
import com.seazon.feedme.spider.utils.FmPathUtils;
import com.seazon.feedme.spider.utils.LogUtils;
import com.seazon.feedme.spider.utils.http.HttpMethod;
import com.seazon.feedme.spider.utils.http.HttpUtils;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class SpiderService extends BaseSpiderService {

    private static final String PREFIX = "http://toutiao.com/m";
    private static final String REQUEST_ITEM_URL = "http://www.toutiao.com/c/user/article/?user_id=%1$s&max_behot_time=%2$s&count=20";
    private static final String REQUEST_FEED_URL = "http://www.toutiao.com/c/user/%s/";

    private String url = "5954781019";
    //    private String url = "4198268867";
//    private String url = "50037963924";
//    private String url = "54564710422";

    @Override
    public SpiderFeed getFeed(String url2) {
        LogUtils.debug("getFeed, url:" + url);
//            if (!url.startsWith(PREFIX)) {
//                return null;
//            }
        try {
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_FEED_URL, url));
            return clawFeed(r, url);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    @Override
    public SpiderStream getItems(String url2, String continuation) {
        LogUtils.debug("getItems, url:" + url);
        try {
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_ITEM_URL, url, continuation));
            return claw(r);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    private SpiderFeed clawFeed(String r, String url) {
        MediaInfo mediaInfo;
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode node = cleaner.clean(r);

            TagNode as = FmPathUtils.xpathSingle(node, "body/script[1]");
            String title = as.getText().toString();
            String c = "var userInfo = ";
            int start = title.indexOf(c);
            int end = title.indexOf("};", start);
            // end+1 为了把最后的 } 包括进来
            title = title.substring(start + c.length(), end + 1);
            mediaInfo = new Gson().fromJson(title, MediaInfo.class);
            LogUtils.debug("parse feed:" + mediaInfo.name);
        } catch (Exception e) {
            LogUtils.error(e);
            mediaInfo = new MediaInfo();
            mediaInfo.name = url.substring(PREFIX.length());
        }
        SpiderFeed feed = new SpiderFeed();
        feed.title = mediaInfo.name;
        return feed;
    }

    private SpiderStream claw(String r) {
        ToutiaoStream toutiaoStream = ToutiaoStream.parse(r);
        for (ToutiaoStreamItem item : toutiaoStream.data) {
            // 原值单位为秒
            item.publishTime *= 1000;
        }

        SpiderStream localRssStream = new SpiderStream();
        localRssStream.continuation = String.valueOf(toutiaoStream.next.continuation);
        localRssStream.items = SpiderItem.parseList(new Gson().toJson(toutiaoStream.data));
        return localRssStream;
    }

}
