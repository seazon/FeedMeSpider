package com.seazon.feedme.spider.utils.http;

import com.seazon.feedme.spider.utils.LogUtils;

import java.util.List;
import java.util.Map;

public class HttpLog {

    public static void log(HttpMethod method, String url, List<NameValuePair> params, Map<String, String> headers,
            String body) {
        LogUtils.debug("[" + Thread.currentThread().getName() + "]" + "================");
        LogUtils.debug("method:" + method);
        LogUtils.debug("url:" + url);

        if (params != null) {
            LogUtils.debug("params:");
            for (NameValuePair np : params) {
                LogUtils.debug(np.getName() + ":" + np.getValue());
            }
        }

        if (headers != null) {
            LogUtils.debug("header:");
            for (String np : headers.keySet()) {
                LogUtils.debug(np + ":" + headers.get(np));
            }
        }

        if (body != null) {
            LogUtils.debug("body:" + body);
        }
    }

}
