package wefit.com.wefit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gioacchino on 31/10/2017.
 */

public class LocalKeyObjectStoreDAO {

    private final static String PREFERENCES_CONTAINER_NAME = "WeFitPreferences";

    private static final LocalKeyObjectStoreDAO ourInstance = new LocalKeyObjectStoreDAO();

    public static LocalKeyObjectStoreDAO getInstance() {
        return ourInstance;
    }

    private Gson marshaller;

    private LocalKeyObjectStoreDAO() {
        marshaller = new Gson();
    }

    public void save(Context currentContext, String key, Object content) {

        SharedPreferences.Editor editor = currentContext.getSharedPreferences(PREFERENCES_CONTAINER_NAME, MODE_PRIVATE).edit();
        editor.putString(key, marshaller.toJson(content));
        editor.apply();

    }

    public Object load(Context currentContext, String key, Class prototypeClass) {

        Object retrieved = null;
        String defaultvaule = "";

        SharedPreferences preferences= currentContext.getSharedPreferences(PREFERENCES_CONTAINER_NAME, MODE_PRIVATE);
        //editor.putString(key, marshaller.toJson(content));
        String jsonObject = preferences.getString(key, defaultvaule);

        if (!jsonObject.equals(defaultvaule)) {
            retrieved = marshaller.fromJson(jsonObject, prototypeClass);
        }

        return retrieved;

    }


}
