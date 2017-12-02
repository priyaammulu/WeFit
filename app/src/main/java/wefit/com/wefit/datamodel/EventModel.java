package wefit.com.wefit.datamodel;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;

/**
 * Author: Lorenzo Camaione
 * This interface provides operations that can be done on Event
 */
public interface EventModel {
    /**
     * Returns events the User may subscribe. It sorts them based on the user location.
     * @return a Flowable containing a List of Event
     */
    Flowable<List<Event>> getEvents();
    /**
     * Returns events the User may subscribe. It sorts them based on the user location
     * @return a Flowable containing a List of Event
     */
    Flowable<List<Event>> getNewEvents();

    /**
     * Set the location of the user
     * @param location the location to set
     */
    void setLocation(EventLocation location);

    /**
     * Returns events the User is subscribed
     * @return a Flowable containing a List of Event
     */
    Flowable<List<Event>> getUserEvents();

    /**
     * Save a new event
     * @param event the event to save
     */
    void storeEvent(Event event);

    /**
     * Retrieves the location of the user
     * @return the location of the user
     */
    EventLocation getUserLocation();

    /**
     * Returns the event selected by id
     * @param eventID the id of the event
     * @return a Flowable containing the Event selected
     */
    Flowable<Event> getEventByID(String eventID);

    /**
     * Returns the users subscribed to some events.
     * @return a Flowable containing a List of Event
     */
    Flowable<Map<String, User>> retrieveAttendees(List<String> attendeeIDs);

    /**
     * Confirms the participation of an User to anEvent
     * @param eventID id of the event
     * @param userID id of the user
     */
    void confirmAttendee(String eventID, String userID);

    /**
     * Unsubscribe the User from an Event
     * @param userID id of the user to unsubscribe
     * @param eventID id of the event the user wants to unsubscribe
     */
    void deleteAttendee(String eventID, String userID);

    /**
     * Returns the event selected by id
     * @param eventID the id of the event
     * @return a Flowable containing the Event selected
     */
    Event getLocalEvent(String eventID);

    /**
     * The user joins an event
     * @param eventID the id of the event the user wants to join
     */
    void joinEvent(String eventID);

    /**
     * Clear every events from the local memory
     */
    void wipeLocalEvents();
}
