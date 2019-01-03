package com.cg.autoshell;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;


/**
 * @author chengang
 * @describe 通知hepler
 */
public class NotificationHelper {
    private static final String PRIMARY_CHANNEL = "default";
    private static final String PRIMARY_CHANNEL_NO_SOUND = "no_sound";
    private static final String PRIMARY_CHANNEL_NAME = "normal";
    private static final String PRIMARY_CHANNEL_NAME_NO_SOUND = "permanent";

    private NotificationManager mManager;
    private static final int NOTIFICATION_SHOW_SHOW_AT_MOST = 50;
    public static final int NOTIFICATION_KCAL_TIME = 100;
    private static int notifyId = 20;

    public static NotificationHelper create() {
        return new NotificationHelper(BaseApp.getAppContext(), true);
    }

    private Context context;


    public NotificationHelper(Context ctx, boolean needSound) {
        this.context = ctx;

        NotificationChannel chan1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (needSound) {
                chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                        PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            } else {
                chan1 = new NotificationChannel(PRIMARY_CHANNEL_NO_SOUND,
                        PRIMARY_CHANNEL_NAME_NO_SOUND, NotificationManager.IMPORTANCE_LOW);
                chan1.setSound(null, null);
            }
            //默认都不开启震动
            chan1.enableVibration(false);
            chan1.setVibrationPattern(new long[]{0});
            getManager().createNotificationChannel(chan1);
        }
    }


    public void cancel(int notifyId) {
        getManager().cancel(notifyId);
    }

    public NotificationCompat.Builder getNotificationBuilder(String title, String body, PendingIntent intent) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(BitmapFactory.decodeResource(
                        BaseApp.getAppContext().getResources(), R.mipmap.ic_launcher))
                .setContentIntent(intent)
                .setAutoCancel(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification26Builder(String title, String body, PendingIntent intent) {
        return new Notification.Builder(context, PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(
                        BaseApp.getAppContext().getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(getSmallIcon())
                .setContentIntent(intent)
                .setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification26BuilderNoSound(String title, String body, PendingIntent intent) {
        return new Notification.Builder(context, PRIMARY_CHANNEL_NO_SOUND)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(
                        BaseApp.getAppContext().getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(getSmallIcon())
                .setContentIntent(intent)
                .setAutoCancel(true);
    }


    /**
     * 发送通知
     */
    public void notify(String title, String body, PendingIntent intent) {
        notifyId++;
        if (notifyId > NOTIFICATION_SHOW_SHOW_AT_MOST) {
            notifyId = 20;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().notify(notifyId, getNotification26Builder(title, body, intent).build());
        } else {
            getManager().notify(notifyId, getNotificationBuilder(title, body, intent).build());
        }


    }

    /**
     * 发送通知指定 id id需要50 以上
     */
    public void notify(String title, String body, PendingIntent intent, int notifyId) {

        if (notifyId <= 50) {
            notifyId = 51;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().notify(notifyId, getNotification26Builder(title, body, intent).build());
        } else {
            getManager().notify(notifyId, getNotificationBuilder(title, body, intent).build());
        }


    }

    /**
     * 发送通知指定 id id需要50 以上
     */
    public void notifyNoCancel(String title, String body, PendingIntent intent, int notifyId) {

        if (notifyId <= 50) {
            notifyId = 51;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().notify(notifyId, getNotification26Builder(title, body, intent).setAutoCancel(false).build());
        } else {
            getManager().notify(notifyId, getNotificationBuilder(title, body, intent).setAutoCancel(false).build());
        }


    }

    /**
     * 获取这个应用程序的小图标
     */
    private int getSmallIcon() {
        return R.mipmap.ic_launcher;
    }

    /**
     * 通知管理
     */
    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }


}
