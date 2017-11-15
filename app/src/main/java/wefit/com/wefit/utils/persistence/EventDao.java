package wefit.com.wefit.utils.persistence;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.users.Event;
import wefit.com.wefit.pojo.Location;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface EventDao {

    Flowable<List<Event>> getEvents(int numResults, int startOffset, @Nullable Location centralPosition);

    Event save(Event eventToStore);

    Flowable<Event> loadEventByID(String eventID);

}
