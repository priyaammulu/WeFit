package wefit.com.wefit;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import wefit.com.wefit.datamodel.EventModel;
import wefit.com.wefit.datamodel.EventModelImpl;
import wefit.com.wefit.datamodel.UserModel;
import wefit.com.wefit.datamodel.UserModelAsyncImpl;
import wefit.com.wefit.utils.auth.Auth20FirebaseHandlerImpl;
import wefit.com.wefit.utils.auth.Auth20Handler;
import wefit.com.wefit.utils.persistence.EventDao;
import wefit.com.wefit.utils.persistence.LocalUserDao;
import wefit.com.wefit.utils.persistence.UserDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseEventDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseUserDao;
import wefit.com.wefit.utils.persistence.sharedpreferencepersistence.LocalUserDaoImpl;
import wefit.com.wefit.viewmodels.UserViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {
    private UserModel mLoginModel;
    private EventModel mEventModel;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO remove in the end (local storage debugging)
        Stetho.initializeWithDefaults(this);

        // initialize persistence
        FirebaseApp.initializeApp(this);
        UserDao userDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), "users");
        EventDao eventDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "events", userDao);
        Auth20Handler loginHandler = new Auth20FirebaseHandlerImpl(FirebaseAuth.getInstance(), userDao);
        LocalUserDao localUserDao = new LocalUserDaoImpl(this);


        // initialise loginModel
        //mLoginModel = new UserModelImpl(this, userDao); // TODO vedi come devi modificare questa implemetnazione
        mLoginModel = new UserModelAsyncImpl(loginHandler, localUserDao, userDao);
        mEventModel = new EventModelImpl(eventDao, userDao);
    }

    public UserViewModel getUserViewModel() {
        return new UserViewModel(getLoginModel());
    }

    private UserModel getLoginModel() {
        return mLoginModel;
    }

    public MainViewModel getMainViewModel() {
        return new MainViewModel(getMainModel());
    }

    private EventModel getMainModel() {
        return mEventModel;
    }
}
