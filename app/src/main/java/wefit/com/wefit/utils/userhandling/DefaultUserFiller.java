package wefit.com.wefit.utils.userhandling;

import android.content.Context;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 26/11/2017.
 */

public class DefaultUserFiller {
    private static final DefaultUserFiller ourInstance = new DefaultUserFiller();

    private Context sysContext;

    public static DefaultUserFiller getInstance() {
        return ourInstance;
    }

    private DefaultUserFiller() {
    }

    public void setSysContext(Context sysContext) {
        this.sysContext = sysContext;
    }

    public User fillNewUserWithDefaultValues(User newUser) {
        
        String defaultImage = sysContext.getString(R.string.user_default_image);
        String defaultBio = sysContext.getString(R.string.user_bio_default);

        newUser.setPhoto(defaultImage);
        newUser.setBiography(defaultBio);

        return newUser;

    }
}
