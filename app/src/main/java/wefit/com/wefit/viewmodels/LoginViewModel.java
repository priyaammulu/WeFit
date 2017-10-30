package wefit.com.wefit.viewmodels;

import java.util.Map;

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

    public void configure(Map<Configuration, Object> configuration) {

        this.mLoginModel.configure(configuration);

    }

    public enum Configuration {
        FB_CONFIG,
        G_COFIG
    }
    public enum Handlers {

        FACEBOOK_HANDLER,
        GOOGLE_HANDLERS
    }
}
