package com.aampower.bloodbank.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.aampower.bloodbank.R;
import com.aampower.bloodbank.ui.BloodRequestActivity;
import com.aampower.bloodbank.ui.HomePageActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FirebaseNotificationService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
//        showNotification(remoteMessage.getData().get("message"));


        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String profile = remoteMessage.getData().get("profile");

        showNotification(title, message, profile);

    }

    private void showNotification(String title, String message, final String profile) {
        Intent i = new Intent(this, BloodRequestActivity.class);

//        Bundle bundle = new Bundle();
//        bundle.putString("Match_ID", matchID);
//        bundle.putString("live", "liveeee");
//        i.putExtras(bundle);


        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        int counter = pref.getInt("count", 0);
        counter++;

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("count", counter);
        editor.apply();


//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.cpl_logo)
//                .setContentTitle("The Lahore CPS Club")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(0, notificationBuilder.build());



        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = counter;
        String channelId = "cpl2-02";
        String channelName = "CPL2Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_first_screen)
                .setContentTitle(title)
//                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
//                .setContentIntent(pendingIntent);
//                .setContentIntent(pendingIntent);



        final Target mTarget;

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap == null) {
                    Log.w("TAG", "Null");
                } else {
                    Log.i("TAG", "Worked");

                    mBuilder.setLargeIcon(bitmap);

                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.w("TAG", "failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i("TAG", "Prepare");
            }
        };


        // Small image loads without resize
        // Picasso.get().load("http://www.theretirementmanifesto.com/wp-content/uploads/2016/08/Yoda-free-clip-art-680x410.jpg").into(mTarget);

        // Mega high res out of memory image

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                Picasso.get().load(profile).resize(100, 100).into(mTarget);
            }
        });





        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());


    }

}
