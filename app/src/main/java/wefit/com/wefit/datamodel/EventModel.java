package wefit.com.wefit.datamodel;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 */

public interface EventModel {
    Flowable<List<Event>> getEvents();

    void setLocation(EventLocation location);

    Flowable<List<Event>> getUserEvents();

    void createEvent(Event event);

    EventLocation getUserLocation();

    Flowable<Event> getEvent(String eventID);

    Flowable<Map<String, User>> getAttendees(List<String> attendeeIDs);

    void confirmAttendee(String eventID, String userID);

    void deleteAttendee(String eventID, String userID);

    Event getLocalEvent(String eventID);
}
