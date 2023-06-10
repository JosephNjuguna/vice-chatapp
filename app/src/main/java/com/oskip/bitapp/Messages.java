package com.oskip.bitapp;

import java.util.Date;

/**
 * Created by jose on 11/9/17.
 */

public class Messages {
    private String messages ;
    private String type ;
    private boolean seen ;
    public String time;
    private String from;
    public Messages() {
    }
    public Messages(String messages, boolean seen,String time,String type, String from) {
        this.messages = messages;
        this.seen = seen;
        this.type = type;
        this.time = time;
        this.from = from;
    }
    public String getMessages() {
        return messages;
    }
    public boolean getSeen() {
        return seen;
    }
    public String getFrom() {
        return from;
    }
    public String getType() {
        return type;
    }
    public String getTimestamp() {
        return time;
    }
}
