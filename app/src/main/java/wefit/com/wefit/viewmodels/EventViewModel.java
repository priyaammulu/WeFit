package wefit.com.wefit.viewmodels;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import wefit.com.wefit.datamodel.EventModel;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
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
        return mEventModel.getEventByID(eventID);
    }

    public void setLocation(EventLocation location) {
        mEventModel.setLocation(location);
    }

    public Flowable<List<Event>> getUserEvents() {
        return mEventModel.getUserEvents();
    }

    public void createNewEvent(Event newEvent) {
        mEventModel.storeEvent(newEvent);
    }

    public EventLocation getUserLocation() {
        return mEventModel.getUserLocation();
    }

    public Flowable<Map<String, User>> getAttendees(List<String> attendeeIDs) {
        return mEventModel.retrieveAttendees(attendeeIDs);
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

    public void addAttendeeToEvent(String eventID) {

        mEventModel.joinEvent(eventID);

    }

    public void updateEvent(Event event) {

        mEventModel.storeEvent(event);

    }

    public Flowable<List<Event>> getNewEvents() {
        return mEventModel.getNewEvents();
    }

    public void wipeLocalEvents() {
        mEventModel.wipeLocalEvents();
    }
}
