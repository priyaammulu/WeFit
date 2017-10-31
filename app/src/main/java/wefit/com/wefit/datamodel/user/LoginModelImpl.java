package wefit.com.wefit.datamodel.user;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.Map;

import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginModelImpl implements LoginModel {

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
    }

    @Override
    public boolean isAuth() {
        return false;
    }
}
