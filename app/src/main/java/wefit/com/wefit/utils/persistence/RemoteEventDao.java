package wefit.com.wefit.utils.persistence;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 13/11/2017.
 * Ths class follows the DAO (Data Access Object) pattern.
 * It allows his client to perform operations on Events stored remotely
 */

public interface RemoteEventDao {

    /**
     * It loads new events
     */
    Flowable<List<Event>> loadNewEvents(int numResults, int anchorID);

    /**
     * It load a new event based on its id
     */
    Flowable<Event> loadEventByID(String eventID);

    /**
     * It load all events having some ids
     */
    Flowable<List<Event>> loadEventsByIDs(List<String> eventIDs);

    /**
     * It loads many events having the admin id
     */
    Flowable<List<Event>> loadEventsByAdmin(String adminID);

    /**
     * It saves a new event
     */
    Event save(Event eventToStore);

    /**
     * It changes the state of an attendance
     */
    void setAttendanceState(String eventID, String userID, boolean state);

    /**
     * It adds a new attendance to an event
     */
    void addAttendee(String eventID, String userID);

    /**
     * It removes an attendance to an event
     */
    void removeAttendee(String eventID, String userID);

}
