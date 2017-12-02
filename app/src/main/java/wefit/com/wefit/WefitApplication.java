package wefit.com.wefit;

import android.app.Application;

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
 * Applications class. Models and data providers are initialized here.
 */

public class WefitApplication extends Application {

    /**
     * Handlers of the low level models
     */
    private UserModel mUserModel;
    private EventModel mEventModel;

    /**
     * Weather utility
     */
    private WeatherForecast forecastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialise remote persistence
        FirebaseApp.initializeApp(this);
        RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), getString(R.string.firebase_user_store_name));
        RemoteEventDao remoteEventDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), getString(R.string.firebase_event_store_name));
        Auth20Handler loginHandler = new Auth20FirebaseHandlerImpl(FirebaseAuth.getInstance(), remoteUserDao);

        // initialise local persistence
        LocalEventDao localEventDao = new LocalSQLiteEventDao(this);
        LocalUserDao localUserDao = new LocalUserDaoImpl(this);

        // Utilities
        DefaultUserFiller.getInstance().init(this);
        DistanceManager distanceSorter = new DistanceManagerImpl();
        forecastManager = new OpenWeatherMapForecastImpl(getString(R.string.openweathermap_key));

        // initialise models
        mUserModel = new UserModelAsyncImpl(loginHandler, localUserDao, remoteUserDao);
        mEventModel = new EventModelImpl(remoteEventDao, remoteUserDao, localEventDao, mUserModel, distanceSorter);
    }

    /**
     * Retrieve the user ViewModel of the system
     * @return User ViewModel
     */
    public UserViewModel getUserViewModel() {
        return new UserViewModel(getUserModel());
    }

    /**
     * Retrieve the event ViewModel of the system
     * @return Event ViewModel
     */
    public EventViewModel getEventViewModel() {
        return new EventViewModel(getEventModel());
    }

    /**
     * Retrieve the weather Forecast utility of the system
     * @return Forecast Manager
     */
    public WeatherForecast getWeatherForecast() {
        return this.forecastManager;
    }


    /**
     * Event model getter
     * @return Event model
     */
    private EventModel getEventModel() {
        return mEventModel;
    }

    /**
     * USer model getter
     * @return User model
     */
    private UserModel getUserModel() {
        return mUserModel;
    }

}
