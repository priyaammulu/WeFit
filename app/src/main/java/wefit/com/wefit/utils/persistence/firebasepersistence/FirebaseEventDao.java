package wefit.com.wefit.utils.persistence.firebasepersistence;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.persistence.RemoteEventDao;

/**
 * Created by gioacchino on 22/11/2017.
 * Firebase implementation of the event DAO
 * OVERRIDDEN METHOD COMMENTS in the interface
 */

public class FirebaseEventDao implements RemoteEventDao {


    /**
     * Description field in the event store
     */
    private static final String DESCRIPTION_FIELD = "description";

    /**
     * Holds the ref to the data store
     */
    private DatabaseReference mEventStorage;

    /**
     * Constructor
     * @param firebaseDatabase reference to the firebase server
     * @param eventStoreName name of the event document in firebase
     */
    public FirebaseEventDao(FirebaseDatabase firebaseDatabase, String eventStoreName) {

        // access to the remote user store
        this.mEventStorage = firebaseDatabase.getReference(eventStoreName);

    }


    @Override
    public Flowable<List<Event>> loadNewEvents(int numResults, int anchorID) {
        return Flowable.create(new LoadEventsAsync(numResults, anchorID), BackpressureStrategy.BUFFER);
    }

    @Override
    public Event save(Event eventToStore) {

        String eventID;

        eventID = eventToStore.getId();

        if (eventID == null) {

            // get the key from the store and assign to the event
            eventID = this.mEventStorage.push().getKey();
            eventToStore.setId(eventID);

            // save in firebase
            this.mEventStorage.child(eventID).setValue(eventToStore);

        } else {
            this.updateEvent(eventToStore);
        }


        return eventToStore;
    }

    private void updateEvent(Event eventToUpdate) {

        // you can just edit the description of an event
        this.mEventStorage.child(eventToUpdate.getId()).child(DESCRIPTION_FIELD).setValue(eventToUpdate.getDescription());

    }

    @Override
    public Flowable<Event> loadEventByID(final String eventID) {
        return Flowable.create(new FlowableOnSubscribe<Event>() {
            @Override
            public void subscribe(final FlowableEmitter<Event> flowableEmitter) throws Exception {

                //Log.i("ALMOND", eventID);
                mEventStorage
                        .orderByKey() // It's necessary to access the children
                        .equalTo(eventID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Event retrieved;
                                try {
                                    // there is only one value
                                    DataSnapshot eventWrapper = dataSnapshot.getChildren().iterator().next();
                                    retrieved = eventWrapper.getValue(Event.class);
                                } catch (NoSuchElementException e) {
                                    retrieved = new Event();
                                }

                                flowableEmitter.onNext(retrieved);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // required, but it cannot happen
                            }
                        });

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Event>> loadEventsByIDs(final List<String> eventIDs) {
        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

                final List<Event> retrievedEvents = new ArrayList<>();

                // make sure that the IDs are unique
                final Set<String> uniqueIDs = new HashSet<>(eventIDs);

                for (String eventID : uniqueIDs) {

                    // retrieve the user from the system async
                    loadEventByID(eventID).subscribe(new Consumer<Event>() {
                        @Override
                        public void accept(Event event) throws Exception {

                            retrievedEvents.add(event);

                            // if the retrieved user number equals the size of the requested IDs
                            // then the load is complete
                            if (retrievedEvents.size() == uniqueIDs.size()) {
                                flowableEmitter.onNext(retrievedEvents);
                            }

                        }
                    });

                }

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Event>> loadEventsByAdmin(final String adminID) {
        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

                //Log.i("ALMOND", eventID);
                mEventStorage
                        .orderByChild("adminID") // It's necessary to access the children
                        .equalTo(adminID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                List<Event> retrieved = new ArrayList<>();
                                // retrieve data of the events from the DB
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Event wrapper = snapshot.getValue(Event.class);
                                    retrieved.add(wrapper);
                                }

                                flowableEmitter.onNext(retrieved);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // required, but it cannot happen
                            }
                        });

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void setAttendanceState(String eventID, String userID, boolean state) {
        this.mEventStorage.child(eventID).child("attendingUsers").child(userID).setValue(state);
    }

    @Override
    public void addAttendee(String eventID, String userID) {
        this.setAttendanceState(eventID, userID, false);
    }

    @Override
    public void removeAttendee(String eventID, String userID) {
        this.mEventStorage.child(eventID).child("attendingUsers").child(userID).removeValue();
    }

    /**
     * This inner class is used as callback function in order to handle the retrieved events
     */
    private class LoadEventsAsync implements FlowableOnSubscribe<List<Event>> {

        /**
         * Number of requested results
         */
        private int mNumberResults = 0;

        /**
         * Value of the anchor
         * This is an offset from the document start
         */
        private int mAnchor;

        /**
         * Creation of the data retrieve callback
         * @param numResults number of requested results
         * @param anchor pagination anchor
         */
        LoadEventsAsync(int numResults, int anchor) {
            this.mNumberResults = numResults;
            this.mAnchor = anchor;
        }

        @Override
        public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

            mEventStorage.orderByKey();

            if (mAnchor != 0) {
                mEventStorage.limitToFirst(mAnchor);
            } else {
                mEventStorage.limitToFirst(mNumberResults);
            }

            mEventStorage
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            List<Event> firebaseEvents = new ArrayList<>();

                            // retrieve data of the events from the DB
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Event wrapper = snapshot.getValue(Event.class);
                                firebaseEvents.add(wrapper);
                            }

                            // NOTE workaround because firebase do not support pagination
                            if (mAnchor != 0) {
                                if (mAnchor > firebaseEvents.size()) {
                                    mAnchor = firebaseEvents.size();
                                }

                                firebaseEvents = firebaseEvents.subList(mAnchor, firebaseEvents.size());
                            }

                            flowableEmitter.onNext(firebaseEvents);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // required, but it cannot happen
                        }
                    });
        }
    }
}
