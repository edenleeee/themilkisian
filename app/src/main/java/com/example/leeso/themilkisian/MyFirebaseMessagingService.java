package com.example.leeso.themilkisian;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.facebook.login.Login;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by leeso on 9/13/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap bitmap1,bitmap2;
    String anotherActivity;


    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String image = remoteMessage.getData().get("image");
        anotherActivity = remoteMessage.getData().get("anotheractivity");
        String waitTime = remoteMessage.getData().get("waitTime");

        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;


        PendingIntent pendingIntent;
        myIntent = new Intent(this,AlarmNotificationReceiver.class);



        bitmap1 = getBitmapfromUrl(image);
        bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.logothemilkisign);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        myIntent.putExtra("title",title);
        myIntent.putExtra("message",message);
        myIntent.putExtra("imageurl",image);
        myIntent.putExtra("anotherActivity",anotherActivity);
        myIntent.putExtra("image",byteArray);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);
        if(waitTime != null){
            if(waitTime.equals("0"))cal.add(Calendar.SECOND, 3);
            else cal.add(Calendar.SECOND, 30);
        }

        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),pendingIntent);

        sendNotification(title,message,bitmap1,bitmap2,anotherActivity);

    }
    private void sendNotification(String title, String body ,Bitmap image1 , Bitmap image2,String AnotherActivity) {
        Intent intent;
        if (AnotherActivity.equals("promo")) {
            intent = new Intent(this, PromoActivity.class);
        }else
            intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //Set sound for notification

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .bigPicture(image1)
                .setSummaryText(body);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
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
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
    private void startAlarm(boolean isNotification, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;


            myIntent = new Intent(this,AlarmNotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);

        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000,pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime()+3000,60*1000,pendingIntent);
    }
}
