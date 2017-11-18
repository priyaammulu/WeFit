package wefit.com.wefit.utils.persistence.firebasepersistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.events.Event;
import wefit.com.wefit.utils.persistence.RemoteEventDao;

/**
 * Created by gioacchino on 16/11/2017.
 */

public class RestructuredFirebaseEventDao implements RemoteEventDao {

    private DatabaseReference mEventStorage;

    @Override
    public Flowable<List<Event>> getEvents(int numResults, int startOffset, @Nullable Location centralPosition) {
        return null;
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

    public RestructuredFirebaseEventDao(FirebaseDatabase firebaseDatabase, String eventStoreName) {

        // access to the remote event store
        this.mEventStorage = firebaseDatabase.getReference(eventStoreName);
    }

    private class EventAsyncProvider implements FlowableOnSubscribe<Event> {

        private String eventID;

        private EventAsyncProvider(String eventID) {

            this.eventID = eventID;

        }

        @Override
        public void subscribe(final FlowableEmitter<Event> flowableEmitter) throws Exception {
            mEventStorage
                    .orderByKey()
                    .equalTo(eventID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            try {
                                // this snapshot contains the required data
                                DataSnapshot snapshot = dataSnapshot
                                        .getChildren()
                                        .iterator()
                                        .next();

                                // retrieve event content
                                EventWrapper wrapper = snapshot.getValue(EventWrapper.class);

                                if (wrapper != null) {
                                    flowableEmitter.onNext(wrapper.unwrapEvent());
                                }

                            } catch (Exception e) {
                                // TODO trow exception
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO gestire errore
                        }
                    });
        }
    }
}
