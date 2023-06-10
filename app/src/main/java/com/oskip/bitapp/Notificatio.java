package com.oskip.bitapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jose on 2/23/2017.
 */
@IgnoreExtraProperties
public class Notificatio {
    public Notificatio() {
    }
    public String type;
    public Notificatio(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}