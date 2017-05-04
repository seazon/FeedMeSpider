package com.seazon.feedme.spider.utils.http;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HttpException extends Exception {

    private static final long serialVersionUID = 1L;

    private Type type;

    public HttpException(Type type, String s, Throwable t) {
        super(s, t);
        this.type = type;
    }

    public HttpException(Type type, Throwable t) {
        super(t);
        this.type = type;
    }

    public HttpException(Type type, String s) {
        super(s);
        this.type = type;
    }

    public HttpException(Type type) {
        super();
        this.type = type;
    }

    public String getHumanMessage() {
        switch (type) {
            case EAUTHFAILED:
                if (getMessage() == null) {
                    return "Auth Failed";
                } else {
                    return getMessage();
                }
            case ECONNRESET:
                return "Connection reset by peer";
            case EASSOHOST:
                return "No address associated with hostname";
            case ETIMEDOUT:
            case ESKTTOEX:
                return "Connection timed out";
            case ENULLRESP:
                return "Response is null";
            case ELOCAL:
                return "Local error";
            case ESKTEX:
                return "Socket error";
            default:
                return "Network error";
        }
    }

    public static HttpException getInstance(Exception e) {
        if (e instanceof HttpException) {
            return (HttpException) e;
        } else if (e instanceof SocketException) {
            if (e.getMessage() != null && e.getMessage().contains(HttpException.Type.ETIMEDOUT.name())) {
                return new HttpException(HttpException.Type.ETIMEDOUT, e);
            } else if (e.getMessage() != null && e.getMessage().contains(HttpException.Type.ECONNRESET.name())) {
                return new HttpException(HttpException.Type.ECONNRESET, e);
            } else {
                return new HttpException(HttpException.Type.ESKTEX, e);
            }
        } else if (e instanceof SocketTimeoutException) {
            return new HttpException(HttpException.Type.ESKTTOEX, e);
        } else if (e instanceof UnknownHostException) {
            return new HttpException(HttpException.Type.EASSOHOST, e);
        } else {
            return new HttpException(HttpException.Type.EOTHERS, e);
        }
    }

    public enum Type {
        /**
         * Auth failed
         */
        EAUTHFAILED,
        /**
         * Connection timed out
         */
        ETIMEDOUT,
        /**
         * No address associated with hostname
         */
        EASSOHOST,
        /**
         * Connection reset by peer
         */
        ECONNRESET,
        /**
         * Response is null
         */
        ENULLRESP,
        /**
         * Other SocketException
         */
        ESKTEX,
        /**
         * SocketTimeoutException
         */
        ESKTTOEX,
        /**
         * Local error
         * 1. json error
         * 2. encoding error
         */
        ELOCAL,
        /**
         * Other error
         */
        EOTHERS;
    }

}
