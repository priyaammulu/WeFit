package wefit.com.wefit.utils.persistence.firebasepersistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class FirebaseEventDao implements RemoteEventDao {

    private DatabaseReference mEventStorage;

    private RemoteUserDao mUserPersistence;

    @Override
    public Flowable<List<Event>> getEvents(int numResults, int startOffset, @Nullable Location centralPosition) {
        return Flowable.create(new EventListAsyncProvider(startOffset, numResults, centralPosition), BackpressureStrategy.BUFFER);
    }

    @Override
    public Event save(Event eventToStore) {

        EventWrapper storeWrapper;
        String eventID;

        eventID = eventToStore.getId();

        if (eventID == null) {

            // get the key from the store and assign to the event
            eventID = this.mEventStorage.push().getKey();
            eventToStore.setId(eventID);

        }

        // wrap the event in the Firebase representation
        storeWrapper = new EventWrapper(eventToStore);

        // save in the db
        this.mEventStorage.child(eventID).setValue(storeWrapper);

        return eventToStore;
    }

    @Override
    public Flowable<Event> loadEventByID(String eventID) {
        return Flowable.create(new EventAsyncProvider(eventID), BackpressureStrategy.BUFFER);
    }

    public FirebaseEventDao(FirebaseDatabase firebaseDatabase, String eventStoreName, RemoteUserDao userPersistence) {

        // access to the remote event store
        this.mEventStorage = firebaseDatabase.getReference(eventStoreName);

        // access to the remote user store
        this.mUserPersistence = userPersistence;

    }

    private Flowable<Event> loadAssociatedUsers(EventWrapper event) {
        return Flowable.create(new LoadAssociateUserProvider(event), BackpressureStrategy.BUFFER);
    }

    private class EventListAsyncProvider implements FlowableOnSubscribe<List<Event>> {

        private Location centralPosition;
        private int initialOffset;
        private int resultRange;

        private List<EventWrapper> firebaseEvents = new ArrayList<>();
        private List<Event> retrievedEvents = new ArrayList<>();


        @Override
        public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

            // event request
            mEventStorage.
                    orderByChild("expiration") // TODO rivedere l'ordinamento
                    .limitToFirst(resultRange)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // retrieve data of the events from the DB
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                EventWrapper wrapper = snapshot.getValue(EventWrapper.class);
                                wrapper.setId(snapshot.getKey());
                                firebaseEvents.add(wrapper);

                            }

                            for (EventWrapper wrappedEvent : firebaseEvents) {

                                loadAssociatedUsers(wrappedEvent).subscribe(new Consumer<Event>() {
                                    @Override
                                    public void accept(Event event) throws Exception {

                                        retrievedEvents.add(event);

                                        if (retrievedEvents.size() == firebaseEvents.size()) {
                                            flowableEmitter.onNext(retrievedEvents);
                                        }

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO gestire errore
                        }
                    });

        }

        EventListAsyncProvider(int initialOffset, int resultRange, @Nullable Location centralPosition) {
            this.centralPosition = centralPosition;
            this.initialOffset = initialOffset;
            this.resultRange = resultRange;
        }
    }

    private class EventAsyncProvider implements FlowableOnSubscribe<Event> {

        private String requestedEventId;

        @Override
        public void subscribe(final FlowableEmitter<Event> listeners) throws Exception {

            mEventStorage
                    .orderByKey()
                    .equalTo(requestedEventId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // this snapshot contains the required data
                    DataSnapshot snapshot = dataSnapshot
                            .getChildren()
                            .iterator()
                            .next();

                    // retrieve event content
                    EventWrapper wrapper = snapshot.getValue(EventWrapper.class);
                    wrapper.setId(snapshot.getKey());

                    // retrieve all the user informations and send the event
                    loadAssociatedUsers(wrapper).subscribe(new Consumer<Event>() {
                        @Override
                        public void accept(Event event) throws Exception {
                            listeners.onNext(event);
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO gestire errore
                }
            });

        }

        EventAsyncProvider(String requestedEventId) {
            this.requestedEventId = requestedEventId;
        }
    }

    private class LoadAssociateUserProvider implements FlowableOnSubscribe<Event> {

        private EventWrapper firebaseEvent;
        private Map<String, User> userMap = new HashMap<>();

        public LoadAssociateUserProvider(EventWrapper firebaseEvent) {
            this.firebaseEvent = firebaseEvent;
        }

        @Override
        public void subscribe(final FlowableEmitter<Event> flowableEmitter) throws Exception {


            final Set<String> userIDs = new HashSet<>();
            userIDs.add(firebaseEvent.getEventCreatorUserId());
            userIDs.addAll(firebaseEvent.getPartecipantsUserIds());


            // fetch user infos for each user
            for (String id : userIDs) {

                mUserPersistence.loadByID(id).subscribe(new Consumer<User>() {

                    @Override
                    public void accept(User user) throws Exception {

                        // collect the user in a id-data dictionary
                        userMap.put(user.getUserId(), user);

                        // if the last user is retrieved
                        if (userMap.size() == userIDs.size()) {

                            Event event = this.structureResposne();
                            flowableEmitter.onNext(event);


                        }

                    }

                    private Event structureResposne() {

                        Event newevent = firebaseEvent.unwrapEvent();

                        // retrieve user creator from the map
                        newevent.setCreator(userMap.get(firebaseEvent.getEventCreatorUserId()));

                        // fill all the partecipant to the event
                        for (String partecipantID : firebaseEvent.getPartecipantsUserIds()) {
                            newevent.addPatecipant(userMap.get(partecipantID));
                        }

                        return newevent;

                    }
                });


            }


        }
    }


}
