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

import java.util.HashMap;
import java.util.Map;

public class SpiderService extends BaseSpiderService {

    private static final int COUNT = 20;
    private static final String REQUEST_ITEM_URL = "https://www.toutiao.com/pgc/ma/?page_type=1&max_behot_time=%3$s&uid=%1$s&media_id=%2$s&output=json&is_json=1&from=user_profile_app&version=2&as=%4$s&cp=%5$s&callback=json&count=" + COUNT;
    private static final String REQUEST_FEED_URL = "https://www.toutiao.com/c/user/%1$s/#mid=%2$s";

    private String AS, CP;

    @Override
    public SpiderFeed getFeed(String url) {
        LogUtils.debug("getFeed, url:" + url);
        String uid = url.split(",")[0];
        String mid = url.split(",")[1];
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", Utils.getUserAgent(this));
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_FEED_URL, uid, mid), null, null, null).getBody();
            return clawFeed(r, uid);
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

            TagNode as = FmPathUtils.xpathSingle(node, "body/script[3]");
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
            mediaInfo.name = url;
        }
        SpiderFeed feed = new SpiderFeed();
        feed.title = mediaInfo.name;
        return feed;
    }

    @Override
    public SpiderStream getItems(String url, String continuation) {
        LogUtils.debug("getItems, url:" + url + ", continuation:" + continuation);
        String uid = url.split(",")[0];
        String mid = url.split(",")[1];
        if (continuation == null) {
            continuation = String.valueOf(System.currentTimeMillis());
        }
        try {
            generateASCP();
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", Utils.getUserAgent(this));
            String r = HttpUtils.getHttpManager().execute(HttpMethod.GET, String.format(REQUEST_ITEM_URL, uid, mid, continuation, AS, CP), null, headers, null).getBody();
            // 返回json被json(xxx)包裹，在 REQUEST_ITEM_URL 的 callback 参数决定
            return claw(r.substring(5, r.length() - 1));
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    /**
     * 生成请求需要的AS和CP
     */
    private void generateASCP() {
        long cc = System.currentTimeMillis() / 1000;
        String i = Long.toHexString(cc).toUpperCase();
        String e = Utils.md5(cc + "").toUpperCase();
        if (8 != i.length()) {
            AS = "479BB4B7254C150";
            CP = "7E0AC8874BB0985";
            return;
        }

        String s = e.substring(0, 5), o = e.substring(e.length() - 5), n = "";
        for (int a = 0; 5 > a; a++) {
            n += s.substring(a, a + 1) + i.substring(a, a + 1);
        }

        String l = "";
        for (int r = 0; 5 > r; r++) {
            l += i.substring(r + 3, r + 4) + o.substring(r, r + 1);
        }

        AS = "A1" + n + i.substring(i.length() - 3);
        CP = i.substring(0, 3) + l + "E1";
    }

    private SpiderStream claw(String r) {
        ToutiaoStream toutiaoStream = ToutiaoStream.parse(r);
        for (ToutiaoStreamItem item : toutiaoStream.data) {
            // 原值单位为秒
            item.publishTime *= 1000;
        }

        SpiderStream localRssStream = new SpiderStream();
        if (toutiaoStream.next.continuation <= 0 || toutiaoStream.data.size() < COUNT) {
            localRssStream.continuation = null;
        } else {
            localRssStream.continuation = String.valueOf(toutiaoStream.next.continuation);
        }
        localRssStream.items = SpiderItem.parseList(new Gson().toJson(toutiaoStream.data));
        return localRssStream;
    }

}
