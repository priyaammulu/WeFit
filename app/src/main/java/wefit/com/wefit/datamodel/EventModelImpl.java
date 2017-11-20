package wefit.com.wefit.datamodel;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.EventDao;
import wefit.com.wefit.utils.persistence.UserDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseEventDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseUserDao;


/**
 * Created by lorenzo on 10/28/17.
 */

public class EventModelImpl implements EventModel {

    private Location currentLocation;

    private EventDao eventDao;
    private UserDao userDao;


    public EventModelImpl(EventDao eventPersistence, UserDao userPersistence) {
        this.userDao = userPersistence;
        this.eventDao = eventPersistence;
    }

    @Override
    public Flowable<List<Event>> getEvents() {
        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {
                Flowable<List<Event>> promise = eventDao.getEvents(5, 0, null);
                promise.subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(List<Event> events) throws Exception {
                        // todo messaggio ricevuto
                        Log.i("PROMISE GETEVENT", events.toString());
                        Log.i("PROMISE RESPEcet", "rispettato");
                        // TODO qui posso continuare con la computazione perché ho tutti gli eventi
                        flowableEmitter.onNext(events);
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
        // TODO non so che farci
        return null;
    }

    private void sortByLocation() {
        // todo, sort events by location distance
        // TODO use a strategy pattern here
    }


}
