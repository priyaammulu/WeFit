package wefit.com.wefit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.mainscreen.PassingExtraEvent;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.eventutils.wheater.Weather;
import wefit.com.wefit.utils.eventutils.wheater.WeatherForecast;
import wefit.com.wefit.utils.eventutils.wheater.WeatherIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

public class EventDescriptionActivity extends AppCompatActivity {

    /**
     * We have weather forecast for just 5 days ahead
     */
    public static final double FIVE_DAYS_DISTANCE_MILLIS = 4.32e+8;

    /**
     * One hour in millis
     */
    public static final double ONE_HOUR_MILLIS = 3.6e+6;

    /**
     * Showed event
     */
    private Event mRetrievedEvent;

    /**
     * True if the event is private
     */
    private boolean isPrivateEvent;


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
    //private ListView mAttendees;
    private LinearLayout mAttendees;
    private LinearLayout mOpenMapButton;
    private LinearLayout mAddCalendarButton;

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
        String eventID = this.getIntent().getStringExtra(PassingExtraEvent.EVENT);

        // check if the event is public or local (default public)
        this.isPrivateEvent = this.getIntent().getBooleanExtra(PassingExtraEvent.IS_PRIVATE, false);

        if (!isPrivateEvent) {

            // download the update event from the server
            mEventViewModel.getEvent(eventID).subscribe(new Consumer<Event>() {
                @Override
                public void accept(Event event) throws Exception {

                    mRetrievedEvent = event;
                    // fill the activity with the new data
                    fillActivity(event);

                }
            });
        } else {

            fillActivity(mEventViewModel.getPrivateEvent(eventID));

        }

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

        this.mAttendees = (LinearLayout) findViewById(R.id.attendees_list);

        this.mOpenMapButton = (LinearLayout) findViewById(R.id.open_map_btn);
        this.mAddCalendarButton = (LinearLayout) findViewById(R.id.calendar_add_btn);
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


        // open maps to the location
        this.mOpenMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventLocation eventLocation = retrievedEvent.getEventLocation();

                double latitude = eventLocation.getLatitude();
                double longitude = eventLocation.getLongitude();

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:" + longitude + "," + latitude + "?q=" + longitude + "," + latitude);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);

            }
        });

        // add to calendar button
        this.mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");

                intent.putExtra(CalendarContract.Events.TITLE, retrievedEvent.getName());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, retrievedEvent.getEventLocation().getName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, retrievedEvent.getDescription());

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        retrievedEvent.getEventDate());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        retrievedEvent.getEventDate() + ONE_HOUR_MILLIS); // set for one hour

                startActivity(intent);
            }
        });

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
        final List<Pair<User, Boolean>> coupleAttendances = new ArrayList<>();


        if (!isPrivateEvent) {
            // public event
            // check if the user is the admin of the event
            if (checkIsCurrentUserAdmin()) {
                coupleAttendances.add(new Pair<>(this.mUserViewModel.retrieveCachedUser(), Boolean.TRUE));
            } else {
                attendeeIDs.add(retrievedEvent.getAdminID());

            }

            // if there are other attendees, retrieve them
            if (retrievedEvent.getAttendingUsers().size() != 0 || attendeeIDs.size() != 0) {
                attendeeIDs.addAll(retrievedEvent.getAttendingUsers().keySet());

                this.mEventViewModel.getAttendees(attendeeIDs).subscribe(new Consumer<Map<String, User>>() {
                    @Override
                    public void accept(Map<String, User> attendees) throws Exception {

                        Log.i("attendees", attendees.toString());

                        // add users to the list of attendees

                        if (!checkIsCurrentUserAdmin()) {
                            // add the admin as firs element
                            coupleAttendances.add(new Pair<>(attendees.get(retrievedEvent.getAdminID()), Boolean.TRUE));
                        }

                        // add the other attendees
                        for (String attendeeID : retrievedEvent.getAttendingUsers().keySet()) {
                            coupleAttendances.add(
                                    new Pair<>(attendees.get(attendeeID), retrievedEvent.getAttendingUsers().get(attendeeID))
                            );
                        }

                        showAttendaces(coupleAttendances);
                    }
                });
            } else {
                showAttendaces(coupleAttendances);
            }

        } else { // private event
            mAttendees.setVisibility(View.INVISIBLE); // there are no attendees

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

    private void showAttendaces(List<Pair<User, Boolean>> attendances) {

        // TODO spostare
        final String ADMIN_LABEL = "admin";
        final String CONFIRMED_LABEL = "confirmed";
        final String NOT_CONFIRMED_LABEL = "NOT confirmed";

        // this will add rows the list of attendees
        final LayoutInflater inflater = LayoutInflater.from(this);

        // check if the current logged user is the admin of the event
        boolean isUserAdmin = this.checkIsCurrentUserAdmin();

        for (final Pair<User, Boolean> attendance : attendances) {

            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.attendee_list_item, mAttendees, false);
            // set item content in view

            ImageView userPic = (ImageView) view.findViewById(R.id.attendee_user_pic);
            TextView userName = (TextView) view.findViewById(R.id.attendee_username_txt);
            TextView userStatus = (TextView) view.findViewById(R.id.attendee_status_txt);

            userPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(attendance.first.getPhoto()));
            userName.setText(attendance.first.getFullName());


            // assign status labels
            if (attendance.first.getId().equals(mRetrievedEvent.getAdminID())) {
                // the owner
                userStatus.setText(ADMIN_LABEL);
            } else {
                if (attendance.second) {
                    userStatus.setText(CONFIRMED_LABEL);
                } else {

                    userStatus.setText(NOT_CONFIRMED_LABEL);

                    // give the possibility to confirm or delete the user
                    if (isUserAdmin) {

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showAttendeeConfirmationDialog(view, attendance.first.getFullName(), attendance.first.getId());
                            }
                        });

                    }
                }

            }

            // add to the list
            mAttendees.addView(view);

        }

    }

    private boolean checkIsCurrentUserAdmin() {

        boolean isAdmin = false;

        if (mRetrievedEvent.getAdminID().equals(this.mUserViewModel.retrieveCachedUser().getId())) {
            isAdmin = true;
        }

        return isAdmin;
    }

    public void showAttendeeConfirmationDialog(final View attendeeRow, final String userName, final String attendeeID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO take the texts from the resources

        String title = "Action on attendee";
        String message = "What do you want to do?";
        String positive = "Confirm";
        String negative = "Reject";


        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.i("confermato", userName);
                mEventViewModel.confirmAttendee(mRetrievedEvent.getId(), attendeeID);

                // show confirmed TODO stessa cosa del testo
                ((TextView) attendeeRow.findViewById(R.id.attendee_status_txt)).setText("confirmed");
                // remove on click listener for the row
                attendeeRow.setOnClickListener(null);
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEventViewModel.deleteAttendee(mRetrievedEvent.getId(), attendeeID);
                attendeeRow.setVisibility(View.INVISIBLE);
            }
        });

        builder.show();
    }


}
