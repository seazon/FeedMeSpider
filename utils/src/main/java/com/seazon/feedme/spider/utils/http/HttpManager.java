package com.seazon.feedme.spider.utils.http;

import com.seazon.feedme.spider.utils.LogUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpManager implements HttpCore {

    public static final MediaType JSON = MediaType.parse("application/json; charset=" + HttpUtils.DEFAULT_CHARSET);
    public static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");

    private OkHttpClient client;
    private boolean proxy;
    private String host;
    private int port;
    // 一次同步的总请求数，如果太大则需要优化
    public static int requestCnt;

    public HttpManager() {
        client = new OkHttpClient();
        client.setConnectTimeout(HttpUtils.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        client.setReadTimeout(HttpUtils.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(HttpUtils.READ_TIMEOUT, TimeUnit.MILLISECONDS);

//        updateProxy(core);
    }

//    public void updateProxy(Core core) {
//        proxy = core.getMainPreferences().sync_proxy;
//        host = core.getMainPreferences().sync_proxy_host;
//        port = core.getMainPreferences().sync_proxy_port;
//        setProxy();
//    }

    private void setProxy() {
        if (proxy && host != null && port != 0) {
            SocketAddress sa = new InetSocketAddress(host, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
            client.setProxy(proxy);
        } else {
            client.setProxy(null);
        }
    }

    public String execute(HttpMethod method, String url) throws HttpException {
        return execute(method, url, null, null, null).getBody();
    }

    @Override
    public HttpResponse execute(HttpMethod method, String url, List<NameValuePair> params, Map<String, String> headers,
                                String body) throws HttpException {
        return execute(method, url, params, headers, body, true);
    }

    public HttpResponse execute(HttpMethod method, String url, List<NameValuePair> params, Map<String, String> headers,
                                String body, boolean json) throws HttpException {

        try {
            ++requestCnt;
            HttpLog.log(method, url, params, headers, body);

            String paramString = "";
            if (params != null) {
                paramString = "?" + HttpUtils.format(params, HttpUtils.DEFAULT_CHARSET);
            }

            Request.Builder build = new Request.Builder().url(url + paramString);

            if (headers != null) {
                for (String key : headers.keySet()) {
                    build.addHeader(key, headers.get(key));
                }
            }

            RequestBody requestBody = null;
            if (body != null) {
                if (json) {
                    requestBody = RequestBody.create(JSON, body);
                } else {
                    requestBody = RequestBody.create(FORM, body);
                }
            }
            build = build.method(method.toString(), requestBody);

            Request request = build.build();
            Response response = client.newCall(request).execute();
            MediaType contentType = response.body().contentType();
            String content;
            if (contentType != null && contentType.charset() != null) {
                content = response.body().string();
            } else {
                content = HttpUtils.toString(response.body().byteStream(), HttpUtils.DEFAULT_CHARSET, false);
            }
            LogUtils.debug("response, code:" + response.code() + ", body:" + content);
            return new HttpResponse(response.code(), content, convertHeaders(response));

        } catch (Exception e) {
            throw HttpException.getInstance(e);
        }
    }

    private Map<String, String> convertHeaders(Response response) {
        Map<String, String> headers = new HashMap<>();
        try {
            for (String name : response.headers().names()) {
                headers.put(name, response.header(name));
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return headers;
    }


}
