package com.example.leeso.themilkisian;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    Bitmap bitmap1,bitmap2;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String image = intent.getStringExtra("imageurl");
        bitmap2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.unnamed);
        final String title = intent.getStringExtra("title");
        final String message = intent.getStringExtra("message");
        final String anotherActivity = intent.getStringExtra("anotherActivity");
        byte[] byteArray = intent.getByteArrayExtra("image");
        bitmap1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        sendNotification(context,title,message,bitmap1,bitmap2,anotherActivity);

    }
    private void sendNotification(Context context,String title, String body , Bitmap image1 , Bitmap image2, String AnotherActivity) {
        Intent intent;
        if (AnotherActivity.equals("promo")) {
            intent = new Intent(context, PromoActivity.class);
        }else
            intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //Set sound for notification

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.support.v4.app.NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle()
                .bigPicture(image1)
                .setSummaryText(body);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.unnamed)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setLargeIcon(image2);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}

