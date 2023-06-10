package com.oskip.bitapp;

import java.util.Date;

/**
 * Created by Njugush on 8/14/2017.
 */

 public class Upload5  {
    public Upload5() {
    }
        private String thumbimage;
        private String uId;
        private String email;
        private String name;
        private String country;
        private String online;
        public Date timestamp;
        public Upload5(String uId,String online,String email,String name,
                      String country,Date timestamp,String thumbimage ) {
        this.uId =uId;
        this.email =email;
        this.name =name;
        this.online=online;
        this.country =country;
        this.timestamp =timestamp;
        this.thumbimage =thumbimage;
    }
    public String getonline(){
        return online;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getCountry() {
        return country;
    }
    public String getuId() {
        return uId;
    }
    public String getThumb_image() {
        return thumbimage;
    }
}