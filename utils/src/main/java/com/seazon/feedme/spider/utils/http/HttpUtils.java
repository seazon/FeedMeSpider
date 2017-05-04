package com.seazon.feedme.spider.utils.http;

import android.annotation.SuppressLint;

import com.seazon.feedme.spider.utils.LogUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int CONNECT_TIMEOUT = 15000;
    public static final int READ_TIMEOUT = 15000;
    public static final int BUFFER_SIZE = 4096;
    private static String[] CHARSET_CHARACTER = {"content=\"text/html;charset=", "charset=\""};
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";

    /**
     * 此方法在使用完InputStream后会关闭它。
     *
     * @param is
     * @param charset
     * @param checked
     * @return
     * @throws Exception
     */
    public static String toString(InputStream is, String charset, boolean checked) throws Exception {
        if (checked) {
            LogUtils.debug("charset:" + charset);
        }
        List<byte[]> byteArrayList = new ArrayList<>();

        try {
            int realSize = -1;
            int totalSize = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] data;
            while ((realSize = is.read(buffer)) != -1) {
                totalSize += realSize;
                if (totalSize > 409600) {
                    String info = "this page is too large, size:" + totalSize + " byte";
                    LogUtils.warn(info);
                    return new String(info);
                }

                data = new byte[realSize];
                System.arraycopy(buffer, 0, data, 0, realSize);
                byteArrayList.add(data);

                if (!checked) {
                    charset = getCharset(new String(buffer, charset));
                    if (charset == null) {
                        charset = DEFAULT_CHARSET;
                    } else if (DEFAULT_CHARSET.equals(charset)) {
                        checked = true;
                    } else {
                        LogUtils.debug("charset:" + charset);
                        checked = true;
                        continue;
                    }
                }
            }

            return new String(sysCopy(totalSize, byteArrayList), charset);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 将多个byte数组合并成一个
     *
     * @param totalSize
     * @param byteArrayList
     * @return
     */
    private static byte[] sysCopy(int totalSize, List<byte[]> byteArrayList) {
        byte[] destArray = new byte[totalSize];
        int index = 0;
        for (byte[] byteArray : byteArrayList) {
            int realSize = byteArray.length;
            System.arraycopy(byteArray, 0, destArray, index, realSize);
            index += realSize;
        }
        return destArray;
    }

    @SuppressLint("DefaultLocale")
    public static String getCharset(String s) {
        s = s.replaceAll(" ", "");
        for (int i = 0; i < CHARSET_CHARACTER.length; ++i) {
            if (s.contains(CHARSET_CHARACTER[i])) {
                int start = s.indexOf(CHARSET_CHARACTER[i]) + CHARSET_CHARACTER[i].length();
                int end = s.indexOf("\"", start);
                return s.substring(start, end).toUpperCase();
            }
        }

        return null;
    }

    /**
     * Returns a String that is suitable for use as an <code>application/x-www-form-urlencoded</code>
     * list of parameters in an HTTP PUT or HTTP POST.
     *
     * @param parameters The parameters to include.
     * @param encoding   The encoding to use.
     */
    public static String format(
            final List<? extends NameValuePair> parameters,
            final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encode(parameter.getName(), encoding);
            final String value = parameter.getValue();
            final String encodedValue = value != null ? encode(value, encoding) : "";
            if (result.length() > 0)
                result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                    encoding != null ? encoding : DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    public static HttpCore getHttpManager( ) {
        return new HttpManager( );
    }

}
