package wefit.com.wefit.viewmodels;

import java.util.Observer;

import wefit.com.wefit.datamodel.user.LoginModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {
    private LoginModel mLoginModel;

    public LoginViewModel(LoginModel LoginModel) {
        this.mLoginModel = LoginModel;
    }

    public void associateUser(String authKey, String userId) {
        mLoginModel.associateUser(authKey, userId);
    }

    public boolean isAuth() {
        return mLoginModel.isAuth();
    }

    public void registerLoginWait(Observer observer) {
        this.mLoginModel.addObserver(observer);
    }


}
