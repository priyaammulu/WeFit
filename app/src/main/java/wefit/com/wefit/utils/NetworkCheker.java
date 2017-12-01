package wefit.com.wefit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by gioacchino on 14/11/2017.
 * This class is responsible to check if the mobile has internet connection
 */

public class NetworkCheker {
    private static final NetworkCheker ourInstance = new NetworkCheker();

    public static NetworkCheker getInstance() {
        return ourInstance;
    }

    private NetworkCheker() {
    }

    /**
     * Returns true if the mobile phone has internet connection, false otherwise
     */
    public boolean isNetworkAvailable(Context context) {

        boolean isAvailable = false;

        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Network is present and connected
                isAvailable = true;
            }
        }

        return isAvailable;
    }
}
