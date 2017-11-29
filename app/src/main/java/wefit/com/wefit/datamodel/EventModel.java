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

    Flowable<List<Event>> getNewEvents();


    void setLocation(EventLocation location);

    Flowable<List<Event>> getUserEvents();

    void storeEvent(Event event);

    EventLocation getUserLocation();

    Flowable<Event> getEventByID(String eventID);

    Flowable<Map<String, User>> retrieveAttendees(List<String> attendeeIDs);

    void confirmAttendee(String eventID, String userID);

    void deleteAttendee(String eventID, String userID);

    Event getLocalEvent(String eventID);

    void joinEvent(String eventID);


}
