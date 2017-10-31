package wefit.com.wefit.datamodel.user;

import android.util.Log;


/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginModelImpl extends LoginModel {

    private UserModel user;

    public LoginModelImpl() {
        user = new UserModel();
    }

    @Override
    public void associateUser(String authKey, String userID) {
        Log.i("INFO", "set associated");
        user.setAuthKey(authKey);
        user.setUserId(userID);
        Log.i("INFO", user.getAuthKey() + "\n" + user.getUserId());

        // notify the change
        this.setChanged();
        notifyObservers();
    }

    @Override
    public boolean isAuth() {
        return false;
    }
}
