package wefit.com.wefit.datamodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;


/**
 * Created by lorenzo on 10/28/17.
 */

public class EventModelImpl implements EventModel {

    private Location currentLocation;

    private RemoteEventDao eventDao;
    private RemoteUserDao userDao;
    private Event event;


    public EventModelImpl(RemoteEventDao eventPersistence, RemoteUserDao userPersistence) {
        this.userDao = userPersistence;
        this.eventDao = eventPersistence;
        event = new Event();
        //event.setId("-Kz47m2Qmn623ifrqLJO");
        User creator = new User();
        creator.setUserId("zbLOEjOmbjWMJCNETOhXkvyTwhi2");
        event.setCreator(creator);
        event.setTitle("Bellolevento");
        event.setDescription("locamente innamorado");
        event.setImage("https://media.mnn.com/assets/images/2016/08/Lion-Stalking-Kalahari-Desert.jpg.638x0_q80_crop-smart.jpg");
        event.setPublished(new Date(652432));
        event.setExpire(new Date(124534765));

        Location location = new Location();
        location.setLatitude(34565.4);
        location.setLongitude(324535.6);
        location.setName("guantanamera city");
        event.setLocation(location);
        Category category = new Category("category", R.drawable.ic_gym_weightlifting);
        event.setCategory(category);
        event.setCategoryName("category1");

        User part1 = new User();
        part1.setUserId("oMHgmaouzSPyxOVK0gcW3mPp7d42");
        User part2 = new User();
        part2.setUserId("IeCvyPwpL6aXbHMAQUdD4BFhcB43");

        List<User> parts = new ArrayList<>();
        parts.add(part1);
        parts.add(part2);

        event.setParticipants(parts);
    }

    @Override
    public Flowable<List<Event>> getEvents() {
        List<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event);
        events.add(event);
        return Flowable.just(events);

//        return Flowable.create(new FlowableOnSubscribe<List<Event>>() {
//            @Override
//            public void subscribe(final FlowableEmitter<List<Event>> flowableEmitter) throws Exception {
//                Flowable<List<Event>> promise = eventDao.getEvents(6, 0, null);
//                promise.subscribe(new Consumer<List<Event>>() {
//                    @Override
//                    public void accept(List<Event> events) throws Exception {
//                        sortByLocation(events);
//                        flowableEmitter.onNext(events);
//                    }
//                });
//            }
//        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void setLocation(Location location) {
        this.currentLocation = location;
    }

    @Override
    public Flowable<List<Event>> getUserEvents() {
        List<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event);
        events.add(event);
        return Flowable.just(events);
    }

    @Override
    public void createEvent(Event event) {
        eventDao.save(event);
    }

    private void sortByLocation() {
        // todo, sort events by location distance
        // TODO use a strategy pattern here
    }
}
