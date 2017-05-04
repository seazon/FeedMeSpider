package com.seazon.feedme.spider.utils.http;

import java.util.List;
import java.util.Map;

public interface HttpCore {


    String execute(HttpMethod method, String url) throws HttpException;


    HttpResponse execute(HttpMethod method, String url, List<NameValuePair> params, Map<String, String> headers,
                         String body) throws HttpException;

    /**
     *
     * @param method
     * @param url
     * @param params
     * @param headers
     * @param body
     * @param json
     *            body是否为json格式
     * @return
     * @throws HttpException
     */
    HttpResponse execute(HttpMethod method, String url, List<NameValuePair> params, Map<String, String> headers,
                         String body, boolean json) throws HttpException;


}
