package wefit.com.wefit.utils.persistence;

import java.util.List;

import wefit.com.wefit.pojo.Event;

/**
 * Created by gioacchino on 13/11/2017.
 * Ths class follows the DAO (Data Access Object) pattern.
 * It allows his client to perform operations on Events stored locally
 */

public interface LocalEventDao {

    /**
     * it deletes every event stored
     */
    void wipe();

    /**
     * it returns a list of events
     */
    List<Event> getEvents(int numResults, int startOffset);

    /**
     * It saves a new event
     */
    Event save(Event eventToStore);

    /**
     * It loads a new event based on its id
     */
    Event loadEventByID(String eventID);
}
