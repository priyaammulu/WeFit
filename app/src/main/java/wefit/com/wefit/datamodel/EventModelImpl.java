package wefit.com.wefit.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.eventutils.location.DistanceManager;
import wefit.com.wefit.utils.persistence.LocalEventDao;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;


/**
 * Created by lorenzo on 10/28/17.
 * This class is an implementation of EventModel. #refer to it for methods explanation.
 */

public class EventModelImpl implements EventModel {
    /**
     * The filter used to reduce events
     */
    private static final int DISTANCE_FILTER = 10;

    /**
     * The number of events requested
     */
    private static final int NUMBER_EVENT_REQUESTED = 20;

    /**
     * The coordinates of Dublin
     */
    private static final EventLocation DUBLIN_POSITION_CENTER = new EventLocation(53.3498, -6.2603);

    /**
     * The location of the user
     */
    private EventLocation currentLocation;

    /**
     * Dao to operates on the Events stored on the remote database
     */
    private RemoteEventDao remoteEventDao;

    /**
     * Dao to operates on the Users stored on the remote database
     */
    private RemoteUserDao remoteUserDao;

    /**
     * Model to operates on Users
     */
    private UserModel userModel;

    /**
     * Dao to operates on the Events stored locally
     */
    private LocalEventDao localEventDao;

    /**
     * object responsible to sort events based on the distance
     */
    private DistanceManager distanceSorter;

    /**
     * Anchor of the current list of events
     */
    private int anchorID = 0;


    public EventModelImpl(RemoteEventDao eventPersistence,
                          RemoteUserDao userPersistence,
                          LocalEventDao localEventDao,
                          UserModel userModel,
                          DistanceManager sorter) {

        this.remoteUserDao = userPersistence;
        this.remoteEventDao = eventPersistence;
        this.localEventDao = localEventDao;
        this.distanceSorter = sorter;
        this.userModel = userModel;

    }

    @Override
    public Flowable<List<Event>> getEvents() {

        // refresh the anchor
        anchorID = 0;

        return Flowable.create(new FillEventsDetails(), BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Event>> getNewEvents() {
        return Flowable.create(new FillEventsDetails(), BackpressureStrategy.BUFFER);
    }

    @Override
    public void setLocation(EventLocation location) {
        this.currentLocation = location;
    }

    @Override
    public Flowable<List<Event>> getUserEvents() {

        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

                // retrieve personal events
                final List<Event> privateEvents = localEventDao.getEvents(NUMBER_EVENT_REQUESTED, 0);

                // retrieve the events that belong to the user
                remoteEventDao
                        .loadEventsByAdmin(userModel.getLocalUser().getId())
                        .subscribe(new Consumer<List<Event>>() {
                            @Override
                            public void accept(final List<Event> retrievedEvents) throws Exception {

                                retrievedEvents.addAll(privateEvents);

                                // if the user has some attendances, download them
                                if (userModel.getLocalUser().getAttendances().size() != 0 && !userModel.getLocalUser().getAttendances().get(0).equals("")) {
                                    remoteEventDao
                                            .loadEventsByIDs(userModel.getLocalUser().getAttendances())
                                            .subscribe(new Consumer<List<Event>>() {
                                                @Override
                                                public void accept(List<Event> attendances) throws Exception {

                                                    // check if the user has been confirmed
                                                    // if not, do not return the event
                                                    attendances = filterUnconfirmedAttendances(attendances);

                                                    attendances = filterOldEvents(attendances);

                                                    retrievedEvents.addAll(attendances);
                                                    Collections.reverse(retrievedEvents);

                                                    flowableEmitter.onNext(retrievedEvents);

                                                }
                                            });
                                } else {
                                    Collections.reverse(retrievedEvents);
                                    flowableEmitter.onNext(retrievedEvents);
                                }


                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void storeEvent(Event event) {
        if (event.isPrivateEvent()) {
            localEventDao.save(event);
        } else {
            remoteEventDao.save(event);
        }

    }

    @Override
    public EventLocation getUserLocation() {

        // to keep the system safe, put DUBLIN as default position
        EventLocation location = DUBLIN_POSITION_CENTER;

        if (currentLocation != null)
            location = currentLocation;

        return location;
    }

    @Override
    public Flowable<Event> getEventByID(String eventID) {
        return remoteEventDao.loadEventByID(eventID);
    }

    @Override
    public Flowable<Map<String, User>> retrieveAttendees(List<String> attendeeIDs) {

        return this.remoteUserDao.loadByIDs(attendeeIDs);
    }

    @Override
    public void confirmAttendee(String eventID, String userID) {

        remoteEventDao.setAttendanceState(eventID, userID, true);

    }

    @Override
    public void deleteAttendee(String eventID, String userID) {
        remoteEventDao.removeAttendee(eventID, userID);
    }

    @Override
    public Event getLocalEvent(String eventID) {
        return localEventDao.loadEventByID(eventID);
    }

    @Override
    public void joinEvent(String eventID) {
        User currentUser = userModel.getLocalUser();

        // update user attendances
        List<String> attendances = currentUser.getAttendances();
        attendances.add(eventID);
        currentUser.setAttendances(attendances);

        // save remotely
        remoteUserDao.save(currentUser);

        remoteEventDao.addAttendee(eventID, currentUser.getId());
    }

    @Override
    public void wipeLocalEvents() {
        localEventDao.wipe();
    }


    /**
     * Filter all the events that are still not confirmed for the currently logged user
     *
     * @param attendances events to filter
     * @return filtered events
     */
    private List<Event> filterUnconfirmedAttendances(List<Event> attendances) {

        List<Event> confirmedAttandances = new ArrayList<>();

        String currentUserID = userModel.getLocalUser().getId();

        for (Event singleAttendance : attendances) {

            Map<String, Boolean> attendanceStates = singleAttendance.getAttendingUsers();

            // if the user is confirmed
            if (attendanceStates.containsKey(currentUserID) && attendanceStates.get(currentUserID)) {
                confirmedAttandances.add(singleAttendance);
            }

        }

        return confirmedAttandances;

    }

    /**
     * Filter all the events that are "full"
     *
     * @param events events to filter
     * @return filtered events
     */
    private List<Event> filterFullEvents(List<Event> events) {

        List<Event> availableEvents = new ArrayList<>();

        for (Event singleAttendance : events) {

            int confirmedUsers = 0;

            Collection<Boolean> attendanceStates = singleAttendance.getAttendingUsers().values();

            for (Boolean attendance : attendanceStates) {

                if (attendance) {
                    confirmedUsers++;
                }

            }

            if (confirmedUsers < singleAttendance.getMaxAttendee()) {
                availableEvents.add(singleAttendance);
            }


        }

        return availableEvents;

    }

    /**
     * Filter all the events that belong to the current logged user
     *
     * @param events events to filter
     * @return filtered list
     */
    private List<Event> filterUserEvents(List<Event> events) {

        List<Event> availableEvents = new ArrayList<>();

        String currentUserID = userModel.getLocalUser().getId();

        for (Event singleAttendance : events) {

            if (!currentUserID.equals(singleAttendance.getAdminID())) {
                availableEvents.add(singleAttendance);
            }

        }

        return availableEvents;

    }

    /**
     * Filter all the events that are past (according to the system clock)
     *
     * @param events events to filter
     * @return filtered list
     */
    private List<Event> filterOldEvents(List<Event> events) {

        List<Event> availableEvents = new ArrayList<>();

        long currentMillis = new Date().getTime();

        for (Event singleAttendance : events) {

            if (currentMillis < singleAttendance.getEventDate()) {
                // event is NOT old

                availableEvents.add(singleAttendance);

            }

        }

        return availableEvents;

    }


    private class FillEventsDetails implements FlowableOnSubscribe<List<Event>> {

        @Override
        public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {
            remoteEventDao
                    .loadNewEvents(NUMBER_EVENT_REQUESTED, anchorID)
                    .subscribe(new Consumer<List<Event>>() {
                        @Override
                        public void accept(List<Event> events) throws Exception {

                            // if there are no events, simply send a empty list
                            if (events.size() != 0) {

                                // retrieve anchor to perform future calls
                                anchorID += NUMBER_EVENT_REQUESTED;

                                // filter events created by the user
                                // it makes no sens to show these events on the wall
                                events = filterUserEvents(events);

                                // sort the events from the current location
                                events = distanceSorter.sortByDistanceFromLocation(getUserLocation(), events, DISTANCE_FILTER);


                                if (events.size() != 0) {
                                    // associate each event to its creator
                                    List<String> creatorIDs = new ArrayList<>();
                                    for (Event retrievedEvent : events) {
                                        creatorIDs.add(retrievedEvent.getAdminID());
                                    }

                                    // filter full events
                                    final List<Event> filterFullEvents = filterFullEvents(events);

                                    remoteUserDao.loadByIDs(creatorIDs).subscribe(new Consumer<Map<String, User>>() {
                                        @Override
                                        public void accept(Map<String, User> stringUserMap) throws Exception {

                                            // assign the correct admin to each event
                                            for (Event retrieved : filterFullEvents) {
                                                retrieved.setAdmin(stringUserMap.get(retrieved.getAdminID()));
                                            }

                                            // send the notification of ended loading
                                            flowableEmitter.onNext(filterFullEvents);

                                        }
                                    });
                                } else {
                                    flowableEmitter.onNext(events);
                                }
                            } else {
                                flowableEmitter.onNext(events);
                            }

                        }
                    });

        }
    }
}
