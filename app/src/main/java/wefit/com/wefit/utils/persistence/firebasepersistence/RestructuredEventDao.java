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

import javax.annotation.Nullable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.utils.persistence.RemoteEventDao;

/**
 * Created by gioacchino on 22/11/2017.
 */

public class RestructuredEventDao implements RemoteEventDao {


    /**
     * Holds the ref to the data store
     */
    private DatabaseReference mEventStorage;

    @Override
    public Flowable<List<Event>> loadNewEvents(int numResults, int startOffset, @Nullable Location centralPosition) {
        // tODO deprecated remove
        return null;
    }

    @Override
    public Flowable<List<Event>> loadNewEvents(int numResults, @Nullable String anchorID) {
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

        }

        // save in firebase
        this.mEventStorage.child(eventID).setValue(eventToStore);


        return eventToStore;
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

                                //Log.i("ALMOND", retrieved.toString());
                                // send event user retrieved
                                flowableEmitter.onNext(retrieved);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // TODO what to do here?
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

                Log.i("ALMOND", uniqueIDs.toString());

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
        // TODO controllare se funziona
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
                                // TODO what to do here?
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

    @Override
    public void deleteEvent(String eventID) {
        this.mEventStorage.child(eventID).removeValue();
    }

    public RestructuredEventDao(FirebaseDatabase firebaseDatabase, String eventStoreName) {

        // access to the remote user store
        this.mEventStorage = firebaseDatabase.getReference(eventStoreName);

    }

    private class LoadEventsAsync implements FlowableOnSubscribe<List<Event>> {

        private int mNumberResults = 0;
        private String mAnchor;

        public LoadEventsAsync(int numResults, String anchorID) {
            this.mNumberResults = numResults;
            this.mAnchor = anchorID;
        }

        @Override
        public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {

            final List<Event> firebaseEvents = new ArrayList<>();

            mEventStorage.orderByKey();

            if (mAnchor != null) {
                mEventStorage.startAt(mAnchor); // TODO rivedere perché non funziona
            }

            mEventStorage
                    .limitToFirst(mNumberResults)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // retrieve data of the events from the DB
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Event wrapper = snapshot.getValue(Event.class);
                                firebaseEvents.add(wrapper);
                            }

                            flowableEmitter.onNext(firebaseEvents);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO what to do here?
                        }
                    });
        }
    }
}
