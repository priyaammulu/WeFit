package wefit.com.wefit.datamodel;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.firebaseadapters.EventFirebaseAdapter;


/**
 * Created by lorenzo on 10/28/17.
 */

public class EventModelImpl implements EventModel {
    private DatabaseReference mEventStore;
    private Location currentLocation;

    // todo remove in the end
    private DatabaseReference mUserStore;


    public EventModelImpl(FirebaseDatabase firebase) {
        mEventStore = firebase.getReference("events");
        mUserStore = firebase.getReference("users");
    }

    @Override
    public Flowable<List<Event>> getEvents() {
        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> topEmitter) throws Exception {


                // creation of a user in the database
                String userKey = mUserStore.push().getKey();
                final User user = new User();
                user.setUserId(userKey);

                mUserStore.child(userKey).setValue(user);


                userKey = mUserStore.push().getKey();
                User partecipant1 = new User();
                partecipant1.setUserId(userKey);
                mUserStore.child(userKey).setValue(partecipant1);

                userKey = mUserStore.push().getKey();
                User partecipant2 = new User();
                partecipant2.setUserId(userKey);
                mUserStore.child(userKey).setValue(partecipant2);


                // TODO populate db (to remove)
                String newEventKey = mEventStore.push().getKey();
                Event testEvent = new Event();
                Calendar calendar = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                Location testLocation = new Location();

                List<User> partecipants = new LinkedList<>();


                testEvent.setDescription("lorem ipsum");
                testEvent.setExpire(new Date());
                testEvent.setTitle("test tile");
                testLocation.setName("location name");
                testEvent.setLocation(testLocation);

                testEvent.setPublished(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 6);
                testEvent.setExpire(calendar.getTime());
                testEvent.setImage("https://firebasestorage.googleapis.com/v0/b/wefit-project.appspot.com/o/nws-st-jaguar.jpg?alt=media&token=98fae503-72a4-4da5-99e0-8a79c7b550c5");


                testEvent.setCreator(user);
                partecipants.add(partecipant1);
                partecipants.add(partecipant2);

                testEvent.setParticipants(partecipants);

                final EventFirebaseAdapter event = new EventFirebaseAdapter(testEvent);

                // save in the db
                mEventStore.child(newEventKey).setValue(event);
                // todo check what happens in case of initial error.

                Log.i("CREATO", "dovrebbe aver creato");


                mEventStore.orderByChild("expiration")
                        .startAt(today.toString())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                List<EventFirebaseAdapter> firebaseEvents = new ArrayList<>();
                                final List<Event> applicationEvents = new ArrayList<>();

                                final Set<String> userIds = new HashSet<>();
                                final Set<User> userInfos = new HashSet<>();

                                //List<Flowable<List<User>>> promises = new ArrayList<>();
                                //int numPremises = 0;


                                // retrieve data of the events from the DB
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    firebaseEvents.add(snapshot.getValue(EventFirebaseAdapter.class));
                                }


                                // retrieve all the required user IDs
                                for (EventFirebaseAdapter event : firebaseEvents) {

                                    userIds.add(event.getEventCreatorUserId());
                                    userIds.addAll(event.getPartecipantsUserIds());

                                }

                                List<Flowable<User>> promises = new ArrayList<>();


                                for (final String id : userIds) {

                                    promises.add(Flowable.create(new FlowableOnSubscribe<User>() {
                                        @Override
                                        public void subscribe(final FlowableEmitter<User> flowableEmitter) throws Exception {

                                            Log.i("id", id);
                                            mUserStore.orderByKey().equalTo(id)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Log.i("retrivati", String.valueOf(dataSnapshot.hasChildren()));
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                userInfos.add(snapshot.getValue(User.class));
                                                                Log.i("prova", String.valueOf(userInfos.size()));
                                                            }

                                                            Log.i("prova", "retrievato");
                                                            flowableEmitter.onNext(new User());
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                    }, BackpressureStrategy.BUFFER));


                                }

                                Log.i("prova", "sono qui");

                                //final List<Subscription> subscriptions = new ArrayList<>();

                                for (Flowable<User> promise : promises) {

                                    Log.i("prova", "mi registro");

                                    promise.subscribe(new Consumer<User>() {
                                        @Override
                                        public void accept(User user) throws Exception {
                                            Log.i("prova", "poffarbacco");
                                            Log.i("N Utenti da scaricare", String.valueOf(userIds.size()));
                                            Log.i("N Utenti gia scar", String.valueOf(userInfos.size()));
                                            if (userIds.size() == userInfos.size()) {

                                                // TODO dai agli eventi le loro info utente corrette
                                                Log.i("Utenti", userIds.toString());
                                                Log.i("INFO", userInfos.toString());

                                                topEmitter.onNext(applicationEvents);
                                            }
                                        }
                                    });
                                }







                                /*
                                for (EventFirebaseAdapter fEvent :
                                        firebaseEvents) {

                                    Event event1 = new Event();


                                    event1.setDescription(fEvent.getDescription());
                                    event1.setExpire(fEvent.getExpiration());
                                    event1.setTitle("test tile");
                                    //testLocation.setName("location name");
                                    event1.setLocation(fEvent.getLocation());

                                    event1.setPublished(fEvent.getPublication());
                                    event1.setExpire(fEvent.getExpiration());
                                    event1.setImage(fEvent.getImageUrl());

                                    User creator = new User();
                                    creator.setUserId(fEvent.getEventCreatorUserId());

                                    List<User> partecipants = new ArrayList<>();

                                    for (String userid :
                                            fEvent.getPartecipantsUserIds()) {
                                        User partecipant = new User();
                                        partecipant.setUserId(userid);
                                        partecipants.add(partecipant);
                                    }

                                    event1.setParticipants(partecipants);
                                    event1.setCreator(creator);

                                    applicationEvents.add(event1);





                                }
                                */


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                topEmitter.onError(databaseError.toException());
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
        // TODO use a strategy pattern here
    }


}
