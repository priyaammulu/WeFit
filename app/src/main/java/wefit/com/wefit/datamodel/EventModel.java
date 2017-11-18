package wefit.com.wefit.datamodel;

import java.util.List;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.events.Event;
import wefit.com.wefit.pojo.Location;

/**
 * Created by lorenzo on 10/28/17.
 */

public interface EventModel {

   Flowable<List<Event>> getEvents();

    void setLocation(Location location);

    Flowable<List<Event>> getUserEvents();
}
