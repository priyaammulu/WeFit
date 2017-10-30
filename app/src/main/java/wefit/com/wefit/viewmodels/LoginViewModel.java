package wefit.com.wefit.viewmodels;

import android.content.Intent;
import android.view.View;

import java.util.Map;

import wefit.com.wefit.datamodel.user.LoginModel;

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

    public void passActivityResults(int requestCode, int resultCode, Intent data) {
        mLoginModel.passActivityResults(requestCode, resultCode, data);
    }

    public void setHandlers(Map<LoginViewModel.Handlers, View> handlers) {
        this.mLoginModel.setHandlers(handlers);
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
