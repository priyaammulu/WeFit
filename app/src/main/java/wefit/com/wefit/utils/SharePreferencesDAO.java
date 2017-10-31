package wefit.com.wefit.utils;

import com.google.gson.Gson;

/**
 * Created by gioacchino on 31/10/2017.
 */

public class SharePreferencesDAO {
    private static final SharePreferencesDAO ourInstance = new SharePreferencesDAO();

    public static SharePreferencesDAO getInstance() {
        return ourInstance;
    }

    private final Gson marshaller;

    private SharePreferencesDAO() {
        marshaller = new Gson();
    }

}
