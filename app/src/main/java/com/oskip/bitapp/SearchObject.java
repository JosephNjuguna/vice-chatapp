package com.oskip.bitapp;

import java.util.Date;

/**
 * Created by Njugush on 8/14/2017.
 */
public class SearchObject {
    public SearchObject() {
    }
    private String thumbimage;
    String name;
    public SearchObject( String name,String thumbimage ) {
        this.name =name;
        this.thumbimage =thumbimage;
    }
    public String getName() {
        return name;
    }
    public String getThumbimage() {
        return thumbimage;
    }
}