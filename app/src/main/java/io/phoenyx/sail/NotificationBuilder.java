package io.phoenyx.sail;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

class NotificationBuilder {
    private Context context;
    private int month, day, year, id;
    private String title, content, type;

    NotificationBuilder(Context context, int month, int day, int year, String title, String content, int id, String type) {
        this.context = context;
        this.month = month;
        this.day = day;
        this.year = year;
        this.title = title;
        this.content = content;
        this.type = type;
        this.id = id;
    }

    NotificationBuilder(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    void buildNotification() throws IllegalArgumentException {
        if (context == null || month == 0 || day == 0 || year == 0) {
            throw new IllegalArgumentException("Notification parameters not initialized");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month parameter out of bounds");
        }
        if (day < 1 || month > 31) {
            throw new IllegalArgumentException("Day parameter out of bounds");
        }

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_notification)
                .setAutoCancel(true);

        Intent resultIntent;
        if (type.equals("goal")) {
            resultIntent = new Intent(context, EditGoalActivity.class);
            resultIntent.putExtra("goal_id", id);
        } else {
            resultIntent = new Intent(context, EditPromiseActivity.class);
            resultIntent.putExtra("promise_id", id);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationCompatBuilder.setContentIntent(resultPendingIntent);

        Notification notification = notificationCompatBuilder.build();

        Intent notificationIntent = new Intent("io.phoenyx.sail.ACTION_EVENT_TIME");
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        Calendar current = Calendar.getInstance();
        current.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        Calendar notifTime = Calendar.getInstance();
        notifTime.set(year, month, day);

        long futureInMillis = SystemClock.elapsedRealtime() + notifTime.getTimeInMillis() - current.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    void deleteNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle(title);
        notificationCompatBuilder.setContentText(content);
        notificationCompatBuilder.setSmallIcon(R.mipmap.ic_notification);

        Notification notification = notificationCompatBuilder.build();

        Intent notificationIntent = new Intent("io.phoenyx.sail.ACTION_EVENT_TIME");
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
