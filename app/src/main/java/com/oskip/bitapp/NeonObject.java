package com.oskip.bitapp;

/**
 * Created by jose on 11/27/17.
 */

public class NeonObject {
    public String caption;
    public String  url;
    public String pagename;
    public String pagepic;
    public String type;
    public String timestamp;
    public long likes_count;
    public String from;
    public String pgtype;
    public String getFrom() {
        return from;
    }
    public NeonObject() {
    }
    public NeonObject(String caption,String pgtype,String timestamp,String from, String url, String pagename, String pagepic , long likes_count, String type) {
        this.caption= caption;
        this.url= url;
        this.pgtype = pgtype;
        this.timestamp= timestamp;
        this.pagename= pagename;
        this.likes_count=likes_count;
        this.pagepic= pagepic;
        this.type=type;
        this.from=from;
    }
    public String getPgtype() {
        return pgtype;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getNeoncaption() {
        return caption;
    }
    public String getUrlcaption() {
        return url;
    }
    public String getUsername() {
        return pagename;
    }
    public String getBlogger() {
        return pagepic;
    }
    public String getType() {
        return type;
    }
    public long getLikes_count() {
        return likes_count;
    }
}