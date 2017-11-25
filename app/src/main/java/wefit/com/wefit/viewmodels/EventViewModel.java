package wefit.com.wefit.viewmodels;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import wefit.com.wefit.datamodel.EventModel;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 11/3/17.
 */

public class EventViewModel {
    private EventModel mEventModel;

    public EventViewModel(EventModel eventModel) {
        this.mEventModel = eventModel;
    }

    public Flowable<List<Event>> getEvents() {
        return mEventModel.getEvents();
    }

    public Flowable<Event> getEvent(String eventID) {
        return mEventModel.getEvent(eventID);
    }

    public void setLocation(Location location) {
        mEventModel.setLocation(location);
    }

    public Flowable<List<Event>> getUserEvents() {
        return mEventModel.getUserEvents();
    }

    public void createNewEvent(Event newEvent) {
        mEventModel.createEvent(newEvent);
    }

    public Location getUserLocation() {
        return mEventModel.getUserLocation();
    }

    public Flowable<Map<String, User>> getAttendees(List<String> attendeeIDs) {
        return mEventModel.getAttendees(attendeeIDs);
    }

    public void confirmAttendee(String eventID, String userID) {
        mEventModel.confirmAttendee(eventID, userID);
    }

    public void deleteAttendee(String eventID, String userID) {
        mEventModel.deleteAttendee(eventID, userID);
    }

    public Event getPrivateEvent(String eventID) {
        return mEventModel.getLocalEvent(eventID);
    }
}
