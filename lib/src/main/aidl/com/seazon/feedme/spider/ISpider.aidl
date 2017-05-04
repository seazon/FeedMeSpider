package com.seazon.feedme.spider;

interface ISpider {

    String getFeed(String url);
    String getItems(String url, String continuation);

}
