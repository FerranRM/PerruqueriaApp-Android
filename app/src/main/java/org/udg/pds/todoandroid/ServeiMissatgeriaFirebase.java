package org.udg.pds.todoandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.udg.pds.todoandroid.activity.ActivitatReserva;

import java.util.Random;

public class ServeiMissatgeriaFirebase extends FirebaseMessagingService {

    private static final String TAG = "ServeiMissatgeriaFirebase";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        super.onMessageReceived(remoteMessage);

        mostrarNotificacio(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    public void mostrarNotificacio(String titol, String missatge) {
        NotificationManager notificacio = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "org.udg.pds.todoandroid.test";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificacioChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notificación",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificacioChannel.setDescription("Descripción");
            notificacioChannel.enableLights(true);
            notificacioChannel.setLightColor(Color.BLUE);
            notificacioChannel.setVibrationPattern(new long[]{0,10,5,10});
            notificacioChannel.enableLights(true);
            notificacio.createNotificationChannel(notificacioChannel);
        }

        NotificationCompat.Builder notificacioBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificacioBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_iconaapp)
                .setContentTitle(titol)
                .setContentText(missatge);

        notificacio.notify(new Random().nextInt(), notificacioBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


}
