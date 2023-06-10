package com.oskip.bitapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by jose on 11/21/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            String notification_title = remoteMessage.getNotification().getTitle();
            String notification_message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();
            String from_user_id = remoteMessage.getData().get("from_user_id");
            sendNotification(notification_title, notification_message, click_action, from_user_id);
        }
    }
    private void sendNotification(String notification_title, String notification_message, String click_action, String from_user_id) {
        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("from_user_id", from_user_id);
        PendingIntent resultPendingIntent =PendingIntent.getActivity(this, 0, resultIntent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.noticon)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);
        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotifyMgr != null){
            mNotifyMgr.notify(mNotificationId,mBuilder.build());
        }
    }
}
































