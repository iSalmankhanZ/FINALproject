package com.carwash.carwash50street.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.carwash.carwash50street.Booking.MainActivity;
import com.carwash.carwash50street.Booking.OrderStatus;
import com.carwash.carwash50street.Common.Common;
import com.carwash.carwash50street.Helper.NotificationHelper;
import com.carwash.carwash50street.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI(remoteMessage);
            else
                sendNotification(remoteMessage);
        }

        private void sendNotificationAPI(RemoteMessage remoteMessage) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String content = notification.getBody();

            Intent intent = new Intent(this, OrderStatus.class);
            intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationHelper helper = new NotificationHelper(this);
            android.app.Notification.Builder builder = helper.getStreetNotification(title,content,pendingIntent,defaultSoundUri);

            helper.getManager().notify(new Random().nextInt(),builder.build());
        }

        private void sendNotification(RemoteMessage remoteMessage) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            noti.notify(0,builder.build());
        }
    }
