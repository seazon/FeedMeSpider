package com.seazon.feedme.spider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

public abstract class BaseSpiderService extends Service {

    public static final String ACTION_EXTENSION = "com.seazon.feedme.spider.Extension";

    private IBinder iBinder = new SpiderBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public class SpiderBinder extends ISpider.Stub {

        @Override
        public String getFeed(String url2) throws RemoteException {
            SpiderFeed feed = BaseSpiderService.this.getFeed(url2);
            return new Gson().toJson(feed);
        }

        @Override
        public String getItems(String url, String continuation) throws RemoteException {
            SpiderStream stream = BaseSpiderService.this.getItems(url, continuation);
            return new Gson().toJson(stream);
        }

    }

    abstract public SpiderFeed getFeed(String url);

    abstract public SpiderStream getItems(String url, String continuation);
}
