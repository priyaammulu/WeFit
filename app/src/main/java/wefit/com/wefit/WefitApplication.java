package wefit.com.wefit;

import android.app.Application;

import wefit.com.wefit.datamodel.LoginModel;
import wefit.com.wefit.datamodel.LoginModelImpl;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {
    private final LoginModel mLoginModel;

    public WefitApplication() {
        mLoginModel = new LoginModelImpl();
    }

    public LoginViewModel getLoginViewModel() {
        return new LoginViewModel(getLoginModel());
    }

    private LoginModel getLoginModel() {
        return mLoginModel;
    }
}
