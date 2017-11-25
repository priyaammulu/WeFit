package wefit.com.wefit.datamodel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.eventutils.location.DistanceSorter;
import wefit.com.wefit.utils.persistence.LocalEventDao;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;


/**
 * Created by lorenzo on 10/28/17.
 */

public class EventModelImpl implements EventModel {

    private Location currentLocation;

    private RemoteEventDao remoteEventDao;
    private RemoteUserDao remoteUserDao;

    private UserModel userModel;

    private LocalEventDao localEventDao;

    private DistanceSorter distanceSorter;

    //private Event event;
    private Location dublin = new Location(53.3498, 6.2603);


    public EventModelImpl(RemoteEventDao eventPersistence,
                          RemoteUserDao userPersistence,
                          LocalEventDao localEventDao,
                          UserModel userModel,
                          DistanceSorter sorter) {

        this.remoteUserDao = userPersistence;
        this.remoteEventDao = eventPersistence;
        this.localEventDao = localEventDao;
        this.distanceSorter = sorter;
        this.userModel = userModel;

    }

    @Override
    public Flowable<List<Event>> getEvents() {

        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {
                remoteEventDao
                        .loadNewEvents(20, null)
                        .subscribe(new Consumer<List<Event>>() {
                            @Override
                            public void accept(final List<Event> events) throws Exception {

                                Log.i("event_not_s", events.toString());

                                // TODO this location CANNOT be always dublin
                                // sort the events from the current location
                                distanceSorter.sortByDistanceFromLocation(dublin, events);

                                // associate each event to its creator
                                List<String> creatorIDs = new ArrayList<>();
                                for (Event retrievedEvent : events) {
                                    creatorIDs.add(retrievedEvent.getAdminID());
                                }
                                remoteUserDao.loadByIDs(creatorIDs).subscribe(new Consumer<Map<String, User>>() {
                                    @Override
                                    public void accept(Map<String, User> stringUserMap) throws Exception {
                                        for (Event retrieved : events) {
                                            retrieved.setAdmin(stringUserMap.get(retrieved.getAdminID()));
                                        }

                                        // send the notification of ended loading
                                        flowableEmitter.onNext(events);

                                    }
                                });
                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    @Override
    public Flowable<List<Event>> getUserEvents() {

        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

                // retrieve the events that belong to the user
                remoteEventDao
                        .loadEventsByAdmin(userModel.getLocalUser().getId())
                        .subscribe(new Consumer<List<Event>>() {
                            @Override
                            public void accept(final List<Event> retrievedEvents) throws Exception {

                                // if the user has some attendances, download them
                                if (!userModel.getLocalUser().getAttendances().get(0).equals("")) {
                                    remoteEventDao
                                            .loadEventsByIDs(userModel.getLocalUser().getAttendances())
                                            .subscribe(new Consumer<List<Event>>() {
                                                @Override
                                                public void accept(List<Event> attendances) throws Exception {

                                                    retrievedEvents.addAll(attendances);

                                                    flowableEmitter.onNext(retrievedEvents);

                                                }
                                            });
                                }
                                else {
                                    flowableEmitter.onNext(retrievedEvents);
                                }



                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void createEvent(Event event) {
        remoteEventDao.save(event);
    }

    @Override
    public Location getUserLocation() {
        if (currentLocation != null)
            return currentLocation;
        return dublin;
    }

    @Override
    public Flowable<Event> getEvent(String eventID) {
        return remoteEventDao.loadEventByID(eventID);
    }

    @Override
    public Flowable<Map<String, User>> getAttendees(List<String> attendeeIDs) {

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
}
