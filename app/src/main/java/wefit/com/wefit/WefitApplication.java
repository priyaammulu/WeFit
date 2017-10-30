package wefit.com.wefit;

import android.app.Application;

import wefit.com.wefit.datamodel.user.LoginModel;
import wefit.com.wefit.datamodel.user.LoginModelFBGImpl;
import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {

    private final LoginModel mLoginModel;

    public WefitApplication() {
        //mLoginModel = new LoginModelImpl();

        // model for login handling with FB and
        mLoginModel = new LoginModelFBGImpl();

    }

    public LoginViewModel getLoginViewModel() {
        return new LoginViewModel(getLoginModel());
    }

    private LoginModel getLoginModel() {
        return mLoginModel;
    }
}
