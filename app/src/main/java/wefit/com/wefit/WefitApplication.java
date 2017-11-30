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
import wefit.com.wefit.utils.eventutils.location.DistanceManager;
import wefit.com.wefit.utils.eventutils.location.DistanceManagerImpl;
import wefit.com.wefit.utils.eventutils.wheater.OpenWeatherMapForecastImpl;
import wefit.com.wefit.utils.eventutils.wheater.WeatherForecast;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.LocalEventDao;
import wefit.com.wefit.utils.persistence.LocalUserDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseUserDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseEventDao;
import wefit.com.wefit.utils.persistence.sharedpreferencepersistence.LocalUserDaoImpl;
import wefit.com.wefit.utils.persistence.sqlitelocalpersistence.LocalSQLiteEventDao;
import wefit.com.wefit.utils.userhandling.DefaultUserFiller;
import wefit.com.wefit.viewmodels.UserViewModel;
import wefit.com.wefit.viewmodels.EventViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class WefitApplication extends Application {
    private UserModel mUserModel;
    private EventModel mEventModel;
    private WeatherForecast forecastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO remove in the end (local storage debugging)
        Stetho.initializeWithDefaults(this);

        // initialise remote persistence
        FirebaseApp.initializeApp(this);
        RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), "test_users");
        RemoteEventDao remoteEventDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store");
        Auth20Handler loginHandler = new Auth20FirebaseHandlerImpl(FirebaseAuth.getInstance(), remoteUserDao);

        // initialise local persistence
        LocalEventDao localEventDao = new LocalSQLiteEventDao(this);
        LocalUserDao localUserDao = new LocalUserDaoImpl(this);

        // utils
        DefaultUserFiller.getInstance().setSysContext(this);
        DistanceManager distanceSorter = new DistanceManagerImpl();
        forecastManager = new OpenWeatherMapForecastImpl("3f305e12883b15929de1b1b4a5c0c61d");

        // initialise models
        mUserModel = new UserModelAsyncImpl(loginHandler, localUserDao, remoteUserDao);
        mEventModel = new EventModelImpl(remoteEventDao, remoteUserDao, localEventDao, mUserModel, distanceSorter);
    }

    public UserViewModel getUserViewModel() {
        return new UserViewModel(getLoginModel());
    }

    private UserModel getLoginModel() {
        return mUserModel;
    }

    public EventViewModel getEventViewModel() {
        return new EventViewModel(getMainModel());
    }

    private EventModel getMainModel() {
        return mEventModel;
    }

    public WeatherForecast getWeatherForecast() {
        return this.forecastManager;
    }
}
