package wefit.com.wefit.utils.notification;

import wefit.com.wefit.pojo.Event;

/**
 * Created by gioacchino on 21/11/2017.
 */

public interface LocalEventNotifier {

    void notifyEvent(Event eventToNotify);

}
