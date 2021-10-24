package com.project.iway.Service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.iway.Activity.MainActivity;
import com.project.iway.R;

import java.util.Map;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class Service extends FirebaseMessagingService {

    private static final String TAG = "Firebase_MSG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getNotification(), remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }


private void sendNotification(RemoteMessage.Notification data, Map<String, String> remoteMessageData) {
    String channelId = "myNotificationChannel"; // Store channel ID as String or String resource

        NotificationChannel notificationChannel = new NotificationChannel(channelId , "Notify", NotificationManager.IMPORTANCE_HIGH);
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("longitude", remoteMessageData.get("longitude"));
    intent.putExtra("latitude", remoteMessageData.get("latitude"));
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);


    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.pet)
            .setContentTitle(data.getTitle())
            .setContentText(data.getBody())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);


    notificationManager.notify(1, notification.build());


}


}