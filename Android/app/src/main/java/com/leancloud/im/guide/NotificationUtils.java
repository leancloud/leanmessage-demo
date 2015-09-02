package com.leancloud.im.guide;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.avos.avospush.notification.NotificationCompat;

import java.util.Random;

/**
 * Created by wli on 15/8/26.
 */
public class NotificationUtils {

  public static void showNotification(Context context, String title, String content) {

  }

  public static void showNotification(Context context, String content) {
    int notificationId = (new Random()).nextInt();
    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.notification_icon)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
        .setContentText(content);
    NotificationManager manager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = mBuilder.build();
    manager.notify(notificationId, notification);
  }

  public static void showNotification(Context context, String title, String content, Class<?> className) {
    showNotification(context, title, content, null, className);
  }

  public static void showNotification(Context context, String title, String content, String sound, Class<?> className) {
    Intent intent = new Intent();
    ComponentName cn = new ComponentName(context, className);
    intent.setComponent(cn);
    showNotification(context, title, content, sound, intent);
  }

  public static void showNotification(Context context, String title, String content, String sound, Intent intent) {
    int notificationId = (new Random()).nextInt();
    PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, intent, 0);
    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
        .setContentText(content);
    NotificationManager manager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = mBuilder.build();
    if (sound != null && sound.trim().length() > 0) {
      notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
    }
    manager.notify(notificationId, notification);
  }
}
