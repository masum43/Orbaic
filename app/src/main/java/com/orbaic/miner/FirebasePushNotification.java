package com.orbaic.miner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orbaic.miner.common.SpManager;

public class FirebasePushNotification
             extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "My Channel Description";
    private int notificationId = 107;
    private FirebaseAuth mAuth;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage removeMassage) {
        super.onMessageReceived(removeMassage);

        if (areNotificationsEnabled()) {
            System.out.println("message : "+removeMassage.getNotification().getBody());

            String title = removeMassage.getNotification().getTitle();
            String message = removeMassage.getNotification().getBody();
            System.out.println("title : " +title +" " + "message : "+ message);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            createNotificationChannel(notificationManager);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_logo_new)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent intent = new Intent(this, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            notificationManager.notify(notificationId, builder.build());
        }


    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        SpManager.init(MyApp.context);
        SpManager.saveString(SpManager.KEY_FCM_NEW_TOKEN, token);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference("usersToken");
            tokensRef.child(userId).child("fcmToken").setValue(token)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SpManager.saveString(SpManager.KEY_FCM_TOKEN, token);
                            SpManager.saveString(SpManager.KEY_FCM_NEW_TOKEN, token);
                        }
                    });
        }
    }



    private static void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean areNotificationsEnabled() {
        SpManager.init(this);
        return SpManager.getBoolean(SpManager.KEY_IS_NOTIFICATION_ENABLED, true);
    }

}
