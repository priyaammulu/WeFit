package wefit.com.wefit.utils.persistence.mysqlpersistence;

import android.provider.BaseColumns;

import java.util.List;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.users.Event;
import wefit.com.wefit.utils.persistence.LocalEventDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class LocalSQLiteEventDao implements LocalEventDao {

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
    public Event getEvents(int numResults, int startOffset) {
        return null;
    }

    @Override
    public Event save(Event eventToStore) {
        return null;
    }

    @Override
    public Event loadEventByID(String eventID) {
        return null;
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "event_table";
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_PLACE_NAME = "place";
    }

    private String description;
    private String title;
    private String image;
    private Location location;
    private User user;
    private Date expire;
    private Date published;
    private Category category;


}
