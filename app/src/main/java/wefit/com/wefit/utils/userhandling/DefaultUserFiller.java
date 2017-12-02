package wefit.com.wefit.utils.userhandling;

import android.content.Context;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 26/11/2017.
 * Utility class used to fill the default info of a just-registered user.
 * This was necessary as the team did not exploited the advanced features of Firebase
 * SINGLETON CLASS
 */

public class DefaultUserFiller {
    private static final DefaultUserFiller ourInstance = new DefaultUserFiller();

    private Context sysContext;

    public static DefaultUserFiller getInstance() {
        return ourInstance;
    }

    private DefaultUserFiller() {
    }

    /**
     * Set the system context to access all the required services
     * @param applicationContext System context
     */
    public void init(Context applicationContext) {
        this.sysContext = applicationContext;
    }

    /**
     * Filled the user with the default info
     * @param newUser empty user
     * @return filled user
     */
    public User fillNewUserWithDefaultValues(User newUser) {
        
        String defaultImage = sysContext.getString(R.string.user_default_image);
        String defaultBio = sysContext.getString(R.string.user_bio_default);

        newUser.setPhoto(defaultImage);
        newUser.setBiography(defaultBio);

        return newUser;

    }
}
