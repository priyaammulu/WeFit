package wefit.com.wefit.viewmodels;

import android.content.Context;

import wefit.com.wefit.datamodel.user.LoginModel;
import wefit.com.wefit.datamodel.user.UserModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {
    private LoginModel mLoginModel;

    public LoginViewModel(LoginModel LoginModel) {
        this.mLoginModel = LoginModel;
    }

    public void associateUser(UserModel loggedUser) {
        mLoginModel.associateUser(loggedUser);
    }

    public boolean isAuth() {
        return mLoginModel.isAuth();
    }

}
