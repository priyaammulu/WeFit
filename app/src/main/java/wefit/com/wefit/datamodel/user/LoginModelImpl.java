package wefit.com.wefit.datamodel.user;

import android.util.Log;


/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginModelImpl implements LoginModel {

    private UserModel user;

    @Override
    public void associateUser(UserModel loggedUser) {

        Log.i("INFO", "set associated");
        user = loggedUser;

        Log.i("INFO", user.getAuthKey() + "\n" + user.getUserId());


    }

    @Override
    public boolean isAuth() {

        boolean authenticated = true;

        if (user == null) {
            authenticated = false;
        }

        return authenticated;
    }
}
