package wefit.com.wefit.utils.persistence.sqlitelocalpersistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.persistence.LocalEventDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class LocalSQLiteEventDao implements LocalEventDao {

    /**
     * Save the application context to access the sql helper
     */
    private Context applicationContext;

    /**
     * Current DB version infos
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedReader.db";

    /**
     * Creation command for the table
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    EventEntry.COLUMN_EVENT_NAME + " TEXT," +
                    EventEntry.COLUMN_DESCRIPTION + " TEXT," +
                    EventEntry.COLUMN_IMAGE + " TEXT," +
                    EventEntry.COLUMN_LONGITUDE + " REAL," +
                    EventEntry.COLUMN_LATITUDE + " REAL," +
                    EventEntry.COLUMN_PLACE_NAME + " TEXT," +
                    EventEntry.COLUMN_EVENT_DATE + " LONG," +
                    EventEntry.COLUMN_CREATION_DATE + " LONG," +
                    EventEntry.COLUMN_CATEGORY + " TEXT)";

    /**
     * Deletion command for the whole store
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;


    /**
     * Columns required to retrieve an event
     */
    private static final String[] EVENT_PROJECTION = {
            EventEntry._ID,
            EventEntry.COLUMN_EVENT_NAME,
            EventEntry.COLUMN_DESCRIPTION,
            EventEntry.COLUMN_IMAGE,
            EventEntry.COLUMN_LONGITUDE,
            EventEntry.COLUMN_LATITUDE,
            EventEntry.COLUMN_PLACE_NAME,
            EventEntry.COLUMN_EVENT_DATE,
            EventEntry.COLUMN_CREATION_DATE,
            EventEntry.COLUMN_CATEGORY
    };

    public LocalSQLiteEventDao(Context context) {
        this.applicationContext = context;
    }

    @Override
    public void wipe() {

        SQLiteHelper helper = new SQLiteHelper(applicationContext);

        // retrieve wrapped db
        SQLiteDatabase localDB = helper.getWritableDatabase();

        localDB.execSQL(SQL_DELETE_ENTRIES);
        localDB.execSQL(SQL_CREATE_ENTRIES);

        helper.close();

    }

    @Override
    public List<Event> getEvents(int numResults, int startOffset) {

        SQLiteHelper helper = new SQLiteHelper(applicationContext);

        int endquery = startOffset + numResults;
        String limit = startOffset + "," + endquery;

        List<Event> events = new ArrayList<>();

        Cursor cursor = helper.getReadableDatabase().query(
                EventEntry.TABLE_NAME,                     // The table to query
                EVENT_PROJECTION,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                     // The sort order
                limit
        );

        while (cursor.moveToNext()) {

            events.add(this.retrieveEvent(cursor));

        }

        cursor.close();
        helper.close();

        return events;
    }

    @Override
    public Event save(Event eventToStore) {

        SQLiteHelper helper = new SQLiteHelper(applicationContext);

        long newid;

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(EventEntry.COLUMN_EVENT_NAME, eventToStore.getName());
        valuesToStore.put(EventEntry.COLUMN_DESCRIPTION, eventToStore.getDescription());
        valuesToStore.put(EventEntry.COLUMN_IMAGE, eventToStore.getImage());
        valuesToStore.put(EventEntry.COLUMN_LONGITUDE, eventToStore.getEventLocation().getLongitude());
        valuesToStore.put(EventEntry.COLUMN_LATITUDE, eventToStore.getEventLocation().getLatitude());
        valuesToStore.put(EventEntry.COLUMN_PLACE_NAME, eventToStore.getEventLocation().getName());
        valuesToStore.put(EventEntry.COLUMN_EVENT_DATE, eventToStore.getEventDate());
        valuesToStore.put(EventEntry.COLUMN_CREATION_DATE, eventToStore.getPublicationDate());
        valuesToStore.put(EventEntry.COLUMN_CATEGORY, eventToStore.getCategoryID());

        // store and retrieve ID
        newid = helper.getWritableDatabase().insert(EventEntry.TABLE_NAME, null, valuesToStore);

        // set the new id to the event
        eventToStore.setId(String.valueOf(newid));

        helper.close();

        return eventToStore;
    }

    @Override
    public void update(Event eventToStore) {

    }

    @Override
    public Event loadEventByID(String eventID) {

        Event retrievedEvent = null;

        SQLiteHelper helper = new SQLiteHelper(applicationContext);

        // Filter results WHERE "title" = 'My Title'
        String selection = EventEntry._ID + " = ?";
        String[] selectionArgs = {eventID};

        Cursor cursor = helper.getReadableDatabase().query(
                EventEntry.TABLE_NAME,                     // The table to query
                EVENT_PROJECTION,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        // go to the first element of the cursor
        cursor.moveToFirst();

        retrievedEvent = this.retrieveEvent(cursor);

        // free memory space
        cursor.close();
        helper.close();

        return retrievedEvent;
    }

    private Event retrieveEvent(Cursor cursor) {

        Event retrievedEvent = new Event();

        // indexes from column name
        int idC = cursor.getColumnIndex(EventEntry._ID);
        int nameC = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_NAME);
        int descrC = cursor.getColumnIndex(EventEntry.COLUMN_DESCRIPTION);
        int imgC = cursor.getColumnIndex(EventEntry.COLUMN_IMAGE);
        int lonC = cursor.getColumnIndex(EventEntry.COLUMN_LONGITUDE);
        int latC = cursor.getColumnIndex(EventEntry.COLUMN_LATITUDE);
        int placeNameC = cursor.getColumnIndex(EventEntry.COLUMN_PLACE_NAME);
        int eventDateC = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_DATE);
        int creationDateC = cursor.getColumnIndex(EventEntry.COLUMN_CREATION_DATE);
        int categoryC = cursor.getColumnIndex(EventEntry.COLUMN_CATEGORY);


        retrievedEvent.setId(cursor.getString(idC));
        retrievedEvent.setName(cursor.getString(nameC));
        retrievedEvent.setDescription(cursor.getString(descrC));
        retrievedEvent.setImage(cursor.getString(imgC));
        retrievedEvent.setName(cursor.getString(nameC));
        retrievedEvent.setName(cursor.getString(nameC));
        retrievedEvent.setCategoryID(cursor.getString(categoryC));

        // set location
        Location location = new Location();
        location.setLatitude(cursor.getDouble(latC));
        location.setLatitude(cursor.getDouble(lonC));
        location.setName(cursor.getString(placeNameC));
        retrievedEvent.setEventLocation(location);

        // date creation and event date
        //Date creationdate = new Date(cursor.getLong(creationDateC));
        retrievedEvent.setPublicationDate(cursor.getLong(creationDateC));
        //Date eventdate = new Date(cursor.getLong(eventDateC));
        retrievedEvent.setEventDate(cursor.getLong(eventDateC));

        return retrievedEvent;


    }

    /* Inner class that defines the table contents */
    public static class EventEntry implements BaseColumns {

        /**
         * Table
         */
        static final String TABLE_NAME = "event_table";

        /**
         * Columns
         */
        static final String COLUMN_EVENT_NAME = "name";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_IMAGE = "image";
        static final String COLUMN_LONGITUDE = "longitude";
        static final String COLUMN_LATITUDE = "latitude";
        static final String COLUMN_PLACE_NAME = "place";
        static final String COLUMN_EVENT_DATE = "event_date";
        static final String COLUMN_CREATION_DATE = "creation_date";
        static final String COLUMN_CATEGORY = "category";
    }

    private class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }


}
