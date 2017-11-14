package wefit.com.wefit.viewmodels;


import io.reactivex.Flowable;
import wefit.com.wefit.datamodel.UserModel;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginViewModel {

    //public static final String ACCESS_STORED_USER = "user_key";

    private UserModel mLoginModel;

    public LoginViewModel(UserModel LoginModel) {
        this.mLoginModel = LoginModel;
    }

    public Flowable<User> retrieveUser() {
        return mLoginModel.retrieveLoggedUser();
    }

    public boolean isAuth() {
        return mLoginModel.isAuth();
    }

    public void signOut() {
        mLoginModel.signOut();
    }

}
