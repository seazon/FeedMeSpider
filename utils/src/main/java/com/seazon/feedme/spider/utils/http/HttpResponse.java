package com.seazon.feedme.spider.utils.http;

import java.util.Map;

public class HttpResponse {

    private int code;
    private String body;
    private Map<String, String> headers;

    public HttpResponse(int code, String body, Map<String, String> headers) {
        this.code = code;
        this.body = body;
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

}
