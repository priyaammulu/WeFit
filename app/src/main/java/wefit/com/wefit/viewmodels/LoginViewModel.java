package wefit.com.wefit.viewmodels;


import wefit.com.wefit.datamodel.user.LoginModel;
import wefit.com.wefit.datamodel.user.UserModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {

    public static final String ACCESS_STORED_USER = "user_key";

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
