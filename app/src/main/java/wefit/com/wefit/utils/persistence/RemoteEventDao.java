package wefit.com.wefit.utils.persistence;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface RemoteEventDao {

    @Deprecated
    Flowable<List<Event>> loadNewEvents(int numResults, int startOffset, @Nullable EventLocation centralPosition);

    Flowable<List<Event>> loadNewEvents(int numResults, @Nullable String anchorID);

    Flowable<Event> loadEventByID(String eventID);

    Flowable<List<Event>> loadEventsByIDs(List<String> eventIDs);

    Flowable<List<Event>> loadEventsByAdmin(String adminID);

    Event save(Event eventToStore);

    void setAttendanceState(String eventID, String userID, boolean state);

    void addAttendee(String eventID, String userID);
    void removeAttendee(String eventID, String userID);
    void deleteEvent(String eventID);

}
