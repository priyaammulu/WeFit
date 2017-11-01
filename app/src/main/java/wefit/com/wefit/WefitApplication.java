package wefit.com.wefit;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;

import wefit.com.wefit.datamodel.FirebaseModel;
import wefit.com.wefit.datamodel.LoginModel;
import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        // TODO remove
        Stetho.initializeWithDefaults(this);
        mLoginModel = new FirebaseModel();
    }

    private LoginModel mLoginModel;

    public WefitApplication() {
        //mLoginModel = new FirebaseModel();

        // model for login handling with FB and


    }

    public LoginViewModel getLoginViewModel() {
        return new LoginViewModel(getLoginModel());
    }

    private LoginModel getLoginModel() {
        return mLoginModel;
    }
}
