package wefit.com.wefit.utils.persistence;

import java.util.List;

import wefit.com.wefit.pojo.Event;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface LocalEventDao {

    void wipe();

    List<Event> getEvents(int numResults, int startOffset);

    Event save(Event eventToStore);

    void update(Event eventToStore);

    Event loadEventByID(String eventID);
}