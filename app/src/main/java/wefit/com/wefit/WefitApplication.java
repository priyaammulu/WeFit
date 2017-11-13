package wefit.com.wefit;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

import wefit.com.wefit.datamodel.FirebaseModel;
import wefit.com.wefit.datamodel.LoginModel;
import wefit.com.wefit.datamodel.EventModel;
import wefit.com.wefit.datamodel.EventModelImpl;
import wefit.com.wefit.viewmodels.LoginViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {
    private LoginModel mLoginModel;
    private EventModel mEventModel;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO remove in the end (local storage debugging)
        Stetho.initializeWithDefaults(this);

        // initialise loginModel
        mLoginModel = new FirebaseModel(this);
        mEventModel = new EventModelImpl(FirebaseDatabase.getInstance());
    }

    public LoginViewModel getLoginViewModel() {
        return new LoginViewModel(getLoginModel());
    }

    private LoginModel getLoginModel() {
        return mLoginModel;
    }

    public MainViewModel getMainViewModel() {
        return new MainViewModel(getMainModel());
    }

    private EventModel getMainModel() {
        return mEventModel;
    }
}
