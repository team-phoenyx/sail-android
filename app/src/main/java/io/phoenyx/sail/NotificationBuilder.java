package io.phoenyx.sail;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Terrance on 3/20/2017.
 */

public class NotificationBuilder {
    private Context context;
    private int month, day, year;
    private String title, content;

    public NotificationBuilder(Context context, int month, int day, int year, String title, String content) {
        this.context = context;
        this.month = month;
        this.day = day;
        this.year = year;
        this.title = title;
        this.content = content;
    }

    public void buildNotification() throws IllegalArgumentException {
        if (context == null || month == 0 || day == 0 || year == 0) {
            throw new IllegalArgumentException("Notification parameters not initialized");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month parameter out of bounds");
        }
        if (day < 1 || month > 31) {
            throw new IllegalArgumentException("Day parameter out of bounds");
        }

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle(title);
        notificationCompatBuilder.setContentText(content);
        notificationCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = notificationCompatBuilder.build();

        Intent notificationIntent = new Intent("io.phoenyx.sail.ACTION_EVENT_TIME");
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        Calendar current = Calendar.getInstance();
        current.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar notifTime = Calendar.getInstance();
        notifTime.set(year, month, day);

        long futureInMillis = SystemClock.elapsedRealtime() + notifTime.getTimeInMillis() - current.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
