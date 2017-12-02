package wefit.com.wefit.utils.persistence.sharedpreferencepersistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.LocalUserDao;

/**
 * Created by gioacchino on 14/11/2017.
 * SQLite implementation of the logged user cache
 * OVERRIDDEN METHOD COMMENTS in the interface
 */

public class LocalUserDaoImpl implements LocalUserDao {

    /**
     * Name of the user preferences
     */
    private static final String USER_PREFERENCE = "user_shared_preferences";

    /**
     * User fields
     */
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String CONTACT_FIELD = "contact";
    private static final String IMAGE_FIELD = "image";
    private static final String BIO_FIELD = "bio";
    private static final String BIRTH_FIELD = "birthdate";
    private static final String EVENTS_FIELD = "my_events";

    private SharedPreferences sharedPreferences;


    @Override
    public void save(User userToSave) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save the user fields
        editor.putString(ID_FIELD, userToSave.getId());
        editor.putString(NAME_FIELD, userToSave.getFullName());
        editor.putString(CONTACT_FIELD, userToSave.getEmail());
        editor.putString(IMAGE_FIELD, userToSave.getPhoto());
        editor.putString(BIO_FIELD, userToSave.getBiography());
        editor.putLong(BIRTH_FIELD, userToSave.getBirthDate());

        Set<String> events = new HashSet<>();


        List<String> partecipations = userToSave.getAttendances();
        if (partecipations != null) {
            events.addAll(userToSave.getAttendances());
            editor.putStringSet(EVENTS_FIELD, events);
        }


        // commit in background
        editor.apply();

    }

    @Override
    public User load() {

        User localStoredUser = new User();

        // retrieve the user from the local store
        localStoredUser.setId(sharedPreferences.getString(ID_FIELD, null));
        localStoredUser.setFullName(sharedPreferences.getString(NAME_FIELD, null));
        localStoredUser.setEmail(sharedPreferences.getString(CONTACT_FIELD, null));
        localStoredUser.setPhoto(sharedPreferences.getString(IMAGE_FIELD, null));
        localStoredUser.setBiography(sharedPreferences.getString(BIO_FIELD, null));
        localStoredUser.setBirthDate(sharedPreferences.getLong(BIRTH_FIELD, 0));

        List<String> events = new ArrayList<>();
        events.addAll(sharedPreferences.getStringSet(EVENTS_FIELD, new HashSet<String>()));
        localStoredUser.setAttendances(events);

        return localStoredUser;
    }

    @Override
    public void wipe() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public LocalUserDaoImpl(Context context) {

        // private shared preferences
        sharedPreferences = context.getSharedPreferences(USER_PREFERENCE, 0);



    }
}
