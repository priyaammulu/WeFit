package wefit.com.wefit;

import wefit.com.wefit.datamodel.LoginModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {
    private LoginModel mLoginModel;

    public LoginViewModel(LoginModel LoginModel) {
        this.mLoginModel = LoginModel;
    }

    public boolean isAuth() {
        return mLoginModel.isAuth();
    }
}
