package com.oskip.bitapp;

/**
 * Created by jose on 11/14/17.
 */

public class CommentsObject {
    public CommentsObject() {
    }
    public String comment;
    public String userdp;
    public String thumb_image;
    public String username;
    public String timestamp;
    public CommentsObject(String comment,String timestamp, String userdp, String username , String thumb_image) {
        this.comment = comment;
        this.userdp = userdp;
        this.username = username;
        this.timestamp= timestamp;
        this.thumb_image = thumb_image;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUsercommentdp() {
        return userdp;
    }
    public String getCommentusername() {
        return username;
    }
    public String getThumb_image() {
        return thumb_image;
    }
}