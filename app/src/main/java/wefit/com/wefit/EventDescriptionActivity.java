package wefit.com.wefit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.eventutils.wheater.Weather;
import wefit.com.wefit.utils.eventutils.wheater.WeatherForecast;
import wefit.com.wefit.utils.eventutils.wheater.WeatherIconFactory;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

import static wefit.com.wefit.mainscreen.fragments.MainFragment.EVENT;

public class EventDescriptionActivity extends AppCompatActivity {

    /**
     * We have weather forecast for just 5 days ahead
     */
    public static final double FIVE_DAYS_DISTANCE_MILLIS = 4.32e+8;

    private Event showedEvent;

    // view components
    private ImageView mEventImage;
    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mEventPublishDate;
    private TextView mEventDate;
    private TextView mEventTime;
    private TextView mEventPlace;
    private TextView mEventCity;
    private ImageView mWeatherForecast;
    private ListView mAttendees;

    // viewmodels
    private UserViewModel mUserViewModel;
    private EventViewModel mEventViewModel;

    // utils
    private WeatherForecast weatherForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // retrieve the view-models
        mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        mEventViewModel = ((WefitApplication) getApplication()).getEventViewModel();

        // retrieve utils
        weatherForecast = ((WefitApplication) getApplication()).getWeatherForecast();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        ActionBar mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        // bind the views to the variables
        this.bindViews();

        // retrieve the event from the intent
        String eventID = this.getIntent().getStringExtra(EVENT);

        Log.i("evento", eventID);

        // download the update event from the server
        mEventViewModel.getEvent(eventID).subscribe(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {

                // store the retrieved infos
                showedEvent = event;

                // fill the activity with the new data
                fillActivity(event);

            }
        });

    }

    private void bindViews() {

        this.mEventImage = (ImageView) findViewById(R.id.event_main_img);
        this.mEventName = (TextView) findViewById(R.id.event_title_txt);
        this.mEventDescription = (TextView) findViewById(R.id.event_description_txt);
        this.mEventPublishDate = (TextView) findViewById(R.id.event_date_posted);
        this.mEventDate = (TextView) findViewById(R.id.event_date);
        this.mEventTime = (TextView) findViewById(R.id.event_time);
        this.mEventPlace = (TextView) findViewById(R.id.place_name_txt);
        this.mEventCity = (TextView) findViewById(R.id.city_name_txt);
        this.mWeatherForecast = (ImageView) findViewById(R.id.weather_shower_pic);
        this.mAttendees = (ListView) findViewById(R.id.attendees_listview);

    }

    private void fillActivity(final Event retrievedEvent) {

        this.mEventName.setText(retrievedEvent.getName());
        this.mEventImage.setImageBitmap(this.decodeBase64BitmapString(retrievedEvent.getImage()));
        this.mEventDescription.setText(retrievedEvent.getDescription());
        this.mEventPublishDate.setText(getDate(new Date(retrievedEvent.getPublicationDate())));
        this.mEventDate.setText(getDate(new Date(retrievedEvent.getEventDate())));
        this.mEventTime.setText(getTime(new Date(retrievedEvent.getEventDate())));
        this.mEventPlace.setText(retrievedEvent.getEventLocation().getName().split(",")[0].trim());
        this.mEventCity.setText(retrievedEvent.getEventLocation().getName().split(",")[1].trim());

        // hide weather forecast
        this.mWeatherForecast.setVisibility(View.INVISIBLE);

        // retrieve weather if the date is closer than 5 days
        if ((retrievedEvent.getEventDate() - new Date().getTime()) < FIVE_DAYS_DISTANCE_MILLIS) {
            this.weatherForecast.getForecast(retrievedEvent).subscribe(new Consumer<Weather>() {
                @Override
                public void accept(Weather weather) throws Exception {

                    // retrieve weather icon and set it
                    int weatherIcon = WeatherIconFactory.getInstance().getWeatherIconByID(weather);
                    mWeatherForecast.setImageResource(weatherIcon);

                    // show the icon on screen
                    mWeatherForecast.setVisibility(View.VISIBLE);
                }
            });
        }

        // retrieve attendees and owner
        final List<String> attendeeIDs = new ArrayList<>();
        attendeeIDs.add(retrievedEvent.getAdminID());
        attendeeIDs.addAll(retrievedEvent.getAttendingUsers().keySet());

        this.mEventViewModel.getAttendees(attendeeIDs).subscribe(new Consumer<Map<String, User>>() {
            @Override
            public void accept(Map<String, User> attendees) throws Exception {

                Log.i("attendees", attendees.toString());

                // add users to the list of attendees
                List<Pair<User, Boolean>> coupleAttendances = new ArrayList<>();

                // add the admin as firs element
                coupleAttendances.add(new Pair<>(attendees.get(retrievedEvent.getAdminID()), Boolean.TRUE));

                // add the other attendees
                for (String attendeeID : retrievedEvent.getAttendingUsers().keySet()) {
                    coupleAttendances.add(
                            new Pair<>(attendees.get(attendeeID), retrievedEvent.getAttendingUsers().get(attendeeID))
                    );
                }

                EventAttendeeAdapter attendeeAdapter = new EventAttendeeAdapter(
                        getApplicationContext(),
                        coupleAttendances,
                        retrievedEvent.getAdminID()
                );

                mAttendees.setAdapter(attendeeAdapter);




            }
        });


        // check if the user is the admin of the event
        //TODO remove
        if (retrievedEvent.getAdminID().equals(this.mUserViewModel.retrieveCachedUser().getId())) {
            Log.i("Admin", "si");
        }


    }

    private Bitmap decodeBase64BitmapString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }

    private String getTime(Date date) {
        Locale locale = Locale.ITALIAN;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }


}
