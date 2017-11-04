package wefit.com.wefit.datamodel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;


/**
 * Created by lorenzo on 10/28/17.
 */

public class EventModelImpl implements EventModel {
    private DatabaseReference mEventsReference;
    private List<Event> mEvents;

    public EventModelImpl(FirebaseDatabase eventsReference) {
        mEvents = new ArrayList<>();
        mEventsReference = eventsReference.getReference("events");
    }

    @Override
    public Flowable<List<Event>> getEvents() {
        return Flowable.create(subscriber -> {
            // populate db (to remove)
            String key = mEventsReference.push().getKey();
            Event prova = new Event();
            prova.setDescription("jncdjnvj");
            mEventsReference.child(key).setValue(prova);
            // todo check what happens in case of initial error.
            mEventsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mEvents.clear();
                    for (DataSnapshot a : dataSnapshot.getChildren()) {
                        mEvents.add(a.getValue(Event.class));
                    }
                    subscriber.onNext(mEvents);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(databaseError.toException());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }
}
