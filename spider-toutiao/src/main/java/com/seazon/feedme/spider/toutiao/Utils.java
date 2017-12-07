package com.seazon.feedme.spider.toutiao;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import java.security.MessageDigest;

public class Utils {

    public static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @NonNull
    public static String md5(String data) {
        String result = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            byte bytes[] = md5.digest();
            for (int i = 0; i < bytes.length; i++) {
                result += Integer.toHexString((0x000000ff & bytes[i]) | 0xffffff00).substring(6);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
