package wefit.com.wefit.datamodel;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
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
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;

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
                prova.setExpire(new Date());
                prova.setTitle("kdlerkfm");
                Location loc = new Location();
                loc.setName("prova");
                prova.setLocation(loc);
                Calendar cal = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                prova.setPublished(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, 6);
                prova.setExpire(cal.getTime());
                prova.setImage("https://firebasestorage.googleapis.com/v0/b/wefit-project.appspot.com/o/nws-st-jaguar.jpg?alt=media&token=98fae503-72a4-4da5-99e0-8a79c7b550c5");
                User user = new User();
                user.setName("Lorenzo");
                prova.setUser(user);
                mEventsReference.child(key).setValue(prova);
                // todo check what happens in case of initial error.
                mEventsReference.orderByChild("expire").startAt(today.toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mEvents.clear();
                        for (DataSnapshot a : dataSnapshot.getChildren()) {
                            mEvents.add(a.getValue(Event.class));
                        }
                        if (currentLocation != null)
                            sortByLocation();
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

    @Override
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    private void sortByLocation() {
        // todo, sort events by location distance
    }
}
