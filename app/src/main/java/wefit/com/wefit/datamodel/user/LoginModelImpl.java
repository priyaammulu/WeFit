package wefit.com.wefit.datamodel.user;

import android.util.Log;

import com.google.gson.Gson;


/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginModelImpl implements LoginModel {

    private UserModel user;

    public LoginModelImpl() {
        user = new UserModel();
    }

    @Override
    public void associateUser(String authKey,
                              String userID,
                              String gender,
                              String name,
                              String email) {

        Log.i("INFO", "set associated");
        user.setAuthKey(authKey);
        user.setUserId(userID);
        user.setName(name);
        user.setGender(gender);
        user.setEmail(email);

        Log.i("INFO", user.getAuthKey() + "\n" + user.getUserId());
    }

    @Override
    public boolean isAuth() {

        boolean authenticated = false;

        // retrieve user from shared preferences

        String auth = user.getAuthKey();

        if (auth != null) {
            authenticated = true;
        }

        return authenticated;
    }
}
