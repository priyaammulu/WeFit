package wefit.com.wefit.utils.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.ExtrasLabels;

/**
 * Created by gioacchino on 21/11/2017.
 */

public class LocalEventNotifierImpl implements LocalEventNotifier {

    //private final NotificationManager notificationManager;
    private Context context;

    @Override
    public void notifyEvent(Event eventToNotify) {

        /*
        if (eventToNotify.isPrivateEvent()) {
            Intent openEventActivity = new Intent(context, EventDescriptionActivity.class);
            openEventActivity.putExtra(ExtrasLabels.EVENT, eventToNotify.getId());
            int notificationID = Integer.parseInt(eventToNotify.getId());

            PendingIntent openPendingEvent = PendingIntent.getActivity(context, notificationID, openEventActivity, 0);

            Notification notification = new Notification.Builder(this.context)
                    .setContentTitle(eventToNotify.getName())
                    .setContentText("53264532")
                    .setSmallIcon(R.drawable.wefitlogo_extended)
                    .setContentIntent(openPendingEvent)
                    .setAutoCancel(true)
                    .build();
            //.seIn

        }
        */

        /*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.context)
                        //.setSmallIcon(R.drawable.landmark_icon) // TODO modify with the icon of the app
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        // Sets an ID for the notification
        String mNotificationId = eventToNotify.getId();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId.hashCode(), mBuilder.build());
        */




    }

    public LocalEventNotifierImpl(Context context) {
        this.context = context;

        //this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }
}
