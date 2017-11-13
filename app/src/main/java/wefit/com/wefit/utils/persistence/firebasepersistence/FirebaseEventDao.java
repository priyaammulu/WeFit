package wefit.com.wefit.utils.persistence.firebasepersistence;

import android.util.Log;

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
import wefit.com.wefit.utils.persistence.EventDao;
import wefit.com.wefit.utils.persistence.UserDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class FirebaseEventDao implements EventDao {

    private DatabaseReference mEventStorage;

    private UserDao mUserPersistence;

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

    public FirebaseEventDao(FirebaseDatabase firebaseDatabase, String eventStoreName, UserDao userPersistence) {

        // access to the remote event store
        this.mEventStorage = firebaseDatabase.getReference(eventStoreName);

        // access to the remote user store
        this.mUserPersistence = userPersistence;

    }

    private class EventListAsyncProvider implements FlowableOnSubscribe<List<Event>> {

        private Location centralPosition;
        private int initialOffset;
        private int resultRange;

        @Override
        public void subscribe(final FlowableEmitter<List<Event>> listeners) throws Exception {

            Log.i("SOTTOSCRITTO", "alala");

            mEventStorage.
                    orderByChild("expiration") // TODO rivedere l'ordinamento
                    .limitToFirst(resultRange)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final List<EventWrapper> firebaseEvents = new ArrayList<>();
                            final Set<String> userIDs = new HashSet<>();
                            final Map<String, User> retrievedUsers = new HashMap<>();

                            // retrieve data of the events from the DB
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                EventWrapper wrapper = snapshot.getValue(EventWrapper.class);

                                if (wrapper != null) { // always true tho
                                    wrapper.setId(snapshot.getKey());
                                }

                                firebaseEvents.add(wrapper);
                            }

                            for (EventWrapper wrapped : firebaseEvents) {

                                // collect all the user IDs for each event
                                userIDs.add(wrapped.getEventCreatorUserId());
                                userIDs.addAll(wrapped.getPartecipantsUserIds());

                            }

                            // fetch user infos for each user
                            for (String id : userIDs) {

                                mUserPersistence.loadByID(id).subscribe(new Consumer<User>() {

                                    @Override
                                    public void accept(User user) throws Exception {

                                        // collect the user in a id-data dictionary
                                        retrievedUsers.put(user.getUserId(), user);

                                        // if the last user is retrieved
                                        if (retrievedUsers.size() == userIDs.size()) {
                                            this.structureEvents();

                                        }

                                    }

                                    private void structureEvents() {

                                        List<Event> unwrappedEvents = new ArrayList<>();

                                        // all the information are now available
                                        // construct the events
                                        for (EventWrapper firebaseEvent : firebaseEvents) {

                                            Event newevent = firebaseEvent.unwrap();

                                            // retrieve user creator from the map
                                            newevent.setCreator(retrievedUsers.get(firebaseEvent.getEventCreatorUserId()));

                                            // fill all the partecipant to the event
                                            for (String partecipantID : firebaseEvent.getPartecipantsUserIds()) {
                                                newevent.addPatecipant(retrievedUsers.get(partecipantID));
                                            }

                                            unwrappedEvents.add(newevent);

                                        }

                                        // all is done, send the notification
                                        listeners.onNext(unwrappedEvents);
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

            mEventStorage.orderByKey().equalTo(requestedEventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // TODO qui ricevi l'evento e fai qualcosa
                    Event retrieved = new Event();

                    listeners.onNext(retrieved);

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


}
