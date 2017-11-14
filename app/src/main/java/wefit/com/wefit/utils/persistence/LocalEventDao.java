package wefit.com.wefit.utils.persistence;

import java.util.List;
import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface LocalEventDao {

    void connect();
    void create();
    void disconnect();
    void wipe();

    Flowable<List<Event>> getEvents(int numResults, int startOffset);

    Event save(Event eventToStore);

    Flowable<Event> loadEventByID(String eventID);
}
