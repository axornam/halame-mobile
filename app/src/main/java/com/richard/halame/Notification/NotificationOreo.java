package com.richard.halame.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class NotificationOreo extends ContextWrapper {

    public static final String CHANNEL_ID = "com.richard.halame";
    public static final String CHANNEL_NAME = "halame";

    private NotificationManager notificationManager;



    /////////////////////////////////////////////
    /// Oreo Notification Class Constructor
    public NotificationOreo(Context base){
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }



    ////////////////////////////////////////////
    /// Create Notification Channel
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel  channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getNotificationManager().createNotificationChannel(channel);
    }

    ////////////////////////////////////////////
    ///
    public NotificationManager getNotificationManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    //////////////////////////////////////////////////////////
    ///
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri soundUri,
                                                    String icon ){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSound(soundUri)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }

}
