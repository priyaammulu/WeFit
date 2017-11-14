package wefit.com.wefit.utils.persistence.mysqlpersistence;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.utils.persistence.LocalEventDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class LocalSQLiteEventDao implements LocalEventDao {


    @Override
    public Event save(Event eventToStore) {
        return null;
    }

    @Override
    public Flowable<Event> loadEventByID(String eventID) {
        return null;
    }


    @Override
    public void connect() {

    }

    @Override
    public void create() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void wipe() {

    }

    @Override
    public Flowable<List<Event>> getEvents(int numResults, int startOffset) {
        return null;
    }
}
