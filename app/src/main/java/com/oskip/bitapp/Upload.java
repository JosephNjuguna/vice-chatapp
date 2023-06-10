package com.oskip.bitapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jose on 2/23/2017.
 */
@IgnoreExtraProperties
public class Upload{
    public Upload() {
    }
    public String caption;
    public String userthought;
    public long likes_count;
    public String timestamp;
    public long comments_count;
    public long hearts_count;
    public String name;
     String id;
     String type ;
     String thumb;
     String thumbimage;

    public Upload(String caption, String userthought, long likes_count, String timestamp, long comments_count, long hearts_count, String name, String id, String type, String thumb, String thumbimage) {
        this.caption = caption;
        this.userthought = userthought;
        this.likes_count = likes_count;
        this.timestamp = timestamp;
        this.comments_count = comments_count;
        this.hearts_count = hearts_count;
        this.name = name;
        this.id = id;
        this.type = type;
        this.thumb = thumb;
        this.thumbimage = thumbimage;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public String getUserthought() {
        return userthought;
    }
    public void setUserthought(String userthought) {
        this.userthought = userthought;
    }
    public long getLikes_count() {
        return likes_count;
    }
    public void setLikes_count(long likes_count) {
        this.likes_count = likes_count;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public long getComments_count() {
        return comments_count;
    }
    public void setComments_count(long comments_count) {
        this.comments_count = comments_count;
    }
    public long getHearts_count() {
        return hearts_count;
    }
    public void setHearts_count(long hearts_count) {
        this.hearts_count = hearts_count;
    }
    public String getUsername() {
        return name;
    }
    public void setUsername(String username) {
        this.name = username;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getThumb() {
        return thumb;
    }
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public String getThumb_image() {
        return thumbimage;
    }
    public void setThumb_image(String thumb_image) {
        this.thumbimage = thumb_image;
    }
}

