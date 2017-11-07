package wefit.com.wefit.datamodel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;


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
        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {
                // populate db (to remove)
                String key = mEventsReference.push().getKey();
                Event prova = new Event();
                prova.setDescription("jncdjnvj");
                prova.setDate(new Date());
                prova.setTitle("kdlerkfm");
                Location loc = new Location();
                loc.setName("prova");
                prova.setLocation(loc);
                prova.setPublished(new Date());
                User user = new User();
                user.setName("Lorenzo");
                prova.setUser(user);
                mEventsReference.child(key).setValue(prova);
                // todo check what happens in case of initial error.
                mEventsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mEvents.clear();
                        for (DataSnapshot a : dataSnapshot.getChildren()) {
                            mEvents.add(a.getValue(Event.class));
                        }
                        flowableEmitter.onNext(mEvents);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        flowableEmitter.onError(databaseError.toException());
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);

    }
}
