package wefit.com.wefit.viewmodels;

import wefit.com.wefit.datamodel.user.LoginModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {
    private LoginModel mLoginModel;

    public LoginViewModel(LoginModel LoginModel) {
        this.mLoginModel = LoginModel;
    }

    public void associateUser(String authKey,
                              String userID,
                              String gender,
                              String birthday,
                              String email) {
        mLoginModel.associateUser(authKey, userID, gender, birthday, email);
    }

    public boolean isAuth() {
        return mLoginModel.isAuth();
    }


}
