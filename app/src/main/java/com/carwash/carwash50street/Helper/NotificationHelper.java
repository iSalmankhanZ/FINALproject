package com.carwash.carwash50street.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.carwash.carwash50street.R;

public class NotificationHelper extends ContextWrapper {
    private static final String CAR_WASH_ID = "com.carwash.carwash50street.Street";
    private static final String CAR_WASH_NAME = "50 Street";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //If api is 26 and above
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel streetChannel = new NotificationChannel(CAR_WASH_ID,
                CAR_WASH_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        streetChannel.enableLights(false);
        streetChannel.enableVibration(true);
        streetChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(streetChannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getStreetNotification(String title, String body, PendingIntent contentIntent,
                                                      Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),CAR_WASH_ID)
                .setContentIntent(contentIntent)
                .setContentText(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    /*@TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder streetChannelNotification(String title, String body, PendingIntent contentIntent,
                                                          Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),CAR_WASH_ID)
                .setContentIntent(contentIntent)
                .setContentText(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }*/
}
