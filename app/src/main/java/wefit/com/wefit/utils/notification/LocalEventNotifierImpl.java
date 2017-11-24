package wefit.com.wefit.utils.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;

/**
 * Created by gioacchino on 21/11/2017.
 */

public class LocalEventNotifierImpl implements LocalEventNotifier {

    private Context context;

    @Override
    public void notifyEvent(Event eventToNotify) {

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


    }

    public LocalEventNotifierImpl(Context context) {
        this.context = context;
    }
}
