package wefit.com.wefit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.utils.calendar.CalendarFormatter;
import wefit.com.wefit.utils.eventutils.wheater.Weather;
import wefit.com.wefit.utils.eventutils.wheater.WeatherForecast;
import wefit.com.wefit.utils.eventutils.wheater.WeatherIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * Created by Gioacchino Castorio on 10/28/17.
 * This activity show the detail of an event
 */
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
     * RxJava listeners
     */
    private Subscription eventRetrieve;
    private Subscription weatherForecastRetrieve;
    private Subscription attendeesInfosRetrieve;


    /**
     * view components
     */
    private ImageView mEventImage;
    private TextView mEventName;
    private EditText mEventDescription;
    private TextView mEventPublishDate;
    private TextView mEventDate;
    private TextView mEventTime;
    private TextView mEventPlace;
    private TextView mEventCity;
    private ImageView mWeatherForecast;
    private LinearLayout mAttendees;
    private LinearLayout mOpenMapButton;
    private LinearLayout mAddCalendarButton;
    private Button mJoinEvent;
    private Button mAbandonEvent;
    private Button mModifyEvent;
    private Button mAcceptModifyEvent;
    private ProgressDialog popupDialogProgress;

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * Event View Model
     */
    private EventViewModel mEventViewModel;
    /**
     * Weather forecast
     */
    private WeatherForecast weatherForecast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retrieve the event from the intent
        String eventID = this.getIntent().getStringExtra(ExtrasLabels.EVENT);

        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupDialogProgress = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true);

        // retrieve the view-models
        this.mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        this.mEventViewModel = ((WefitApplication) getApplication()).getEventViewModel();

        // retrieve utils
        this.weatherForecast = ((WefitApplication) getApplication()).getWeatherForecast();

        if (NetworkCheker.getInstance().isNetworkAvailable(this)) {
            // check if the event is public or local (default public)
            if (!this.getIntent().getBooleanExtra(ExtrasLabels.IS_PRIVATE, false)) {

                // request the data from the remote store
                mEventViewModel.getEvent(eventID).subscribe(new FlowableSubscriber<Event>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {

                        subscription.request(Long.MAX_VALUE);
                        eventRetrieve = subscription;

                    }

                    @Override
                    public void onNext(Event event) {

                        // keep the retrieved event
                        mRetrievedEvent = event;

                        // show the layout
                        showLayout();

                        // fill the activity with the new data
                        fillActivityInformation(event);

                    }

                    @Override
                    public void onError(Throwable throwable) {

                        showRetrieveErrorPopupDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

            } else {

                // the event is private, retrieve from the local store
                mRetrievedEvent = mEventViewModel.getPrivateEvent(eventID);

                // show the layout
                showLayout();

                // can just fill the activity
                fillActivityInformation(mRetrievedEvent);

            }
        } else {
            showNoInternetConnectionPopup();
        }

    }

    /**
     * Inflate the layout in the activity
     */
    private void showLayout() {

        // remove the spinner dialog, as the download is ended
        popupDialogProgress.dismiss();

        // inflate the layout
        setContentView(R.layout.activity_event_description);

        // bind the layout components to the class properties
        this.bindComponents();
    }


    /**
     * Bind the layout components to the class properties
     */
    private void bindComponents() {

        this.mEventImage = (ImageView) findViewById(R.id.event_main_img);
        this.mEventName = (TextView) findViewById(R.id.event_title_txt);
        this.mEventDescription = (EditText) findViewById(R.id.event_description_txt);
        this.mEventPublishDate = (TextView) findViewById(R.id.event_date_posted);
        this.mEventDate = (TextView) findViewById(R.id.event_date);
        this.mEventTime = (TextView) findViewById(R.id.event_time);
        this.mEventPlace = (TextView) findViewById(R.id.place_name_txt);
        this.mEventCity = (TextView) findViewById(R.id.city_name_txt);
        this.mWeatherForecast = (ImageView) findViewById(R.id.weather_shower_pic);
        this.mAttendees = (LinearLayout) findViewById(R.id.attendees_list);
        this.mOpenMapButton = (LinearLayout) findViewById(R.id.open_map_btn);
        this.mAddCalendarButton = (LinearLayout) findViewById(R.id.calendar_add_btn);
        this.mJoinEvent = (Button) findViewById(R.id.join_event_btn);
        this.mAbandonEvent = (Button) findViewById(R.id.abandon_event_btn);
        this.mModifyEvent = (Button) findViewById(R.id.modify_event_btn);
        this.mAcceptModifyEvent = (Button) findViewById(R.id.accept_modification_btn);
        ImageView mBackButton = (ImageView) findViewById(R.id.event_description_backbutton);

        // set backbutton
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to main class
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    /**
     * Fill the activity with the retrieved informations
     *
     * @param retrievedEvent retrieved event
     */
    private void fillActivityInformation(final Event retrievedEvent) {

        // event infos filling
        this.mEventName.setText(retrievedEvent.getName());
        this.mEventImage.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(retrievedEvent.getImage()));
        this.mEventDescription.setText(retrievedEvent.getDescription());
        this.mEventPublishDate.setText(CalendarFormatter.getDate(retrievedEvent.getPublicationDate()));
        this.mEventDate.setText(CalendarFormatter.getDate(retrievedEvent.getEventDate()));
        this.mEventTime.setText(CalendarFormatter.getTime(retrievedEvent.getEventDate()));
        this.mEventPlace.setText(retrievedEvent.getEventLocation().getName().trim());
        this.mEventCity.setText(String.format(Locale.ENGLISH,
                "%4.5f, %4.5f",
                retrievedEvent.getEventLocation().getLatitude(),
                retrievedEvent.getEventLocation().getLongitude()));

        // show the action buttons depending on the ownership of the event
        setEventActionButtons();

        showMapAndCalendarButtons();

        handleWeatherForecast();

        retrieveAttendeesInformation();


    }

    /**
     * Retrieve the attendees' information in order to show them
     */
    private void retrieveAttendeesInformation() {

        // retrieve attendees and owner
        final List<String> attendeeIDs = new ArrayList<>();
        final List<Pair<User, Boolean>> coupleAttendances = new ArrayList<>();


        if (NetworkCheker.getInstance().isNetworkAvailable(this)) {
            if (!mRetrievedEvent.isPrivateEvent()) { // public event

                // check if the user is the admin of the event
                if (checkIsCurrentUserAdmin()) {
                    coupleAttendances.add(new Pair<>(this.mUserViewModel.retrieveCachedUser(), Boolean.TRUE));
                } else {
                    attendeeIDs.add(mRetrievedEvent.getAdminID());

                }

                // if there are other attendees, retrieve them
                if (mRetrievedEvent.getAttendingUsers().size() != 0 || attendeeIDs.size() != 0) {
                    attendeeIDs.addAll(mRetrievedEvent.getAttendingUsers().keySet());

                    this.mEventViewModel.getAttendees(attendeeIDs).subscribe(new FlowableSubscriber<Map<String, User>>() {
                        @Override
                        public void onSubscribe(Subscription subscription) {
                            subscription.request(Long.MAX_VALUE);
                            attendeesInfosRetrieve = subscription;
                        }

                        @Override
                        public void onNext(Map<String, User> attendees) {

                            // add users to the list of attendees

                            if (!checkIsCurrentUserAdmin()) {
                                // add the admin as firs element
                                coupleAttendances.add(new Pair<>(attendees.get(mRetrievedEvent.getAdminID()), Boolean.TRUE));
                            }

                            // add the other attendees
                            for (String attendeeID : mRetrievedEvent.getAttendingUsers().keySet()) {
                                coupleAttendances.add(
                                        new Pair<>(attendees.get(attendeeID), mRetrievedEvent.getAttendingUsers().get(attendeeID))
                                );
                            }

                            // show all the attending users
                            showAttendaces(coupleAttendances);

                        }

                        @Override
                        public void onError(Throwable throwable) {

                            showRetrieveErrorPopupDialog();

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                } else {
                    showAttendaces(coupleAttendances);
                }

            } else { // private event
                mAttendees.setVisibility(View.INVISIBLE); // there are no attendees

            }
        } else {
            showNoInternetConnectionPopup();
        }
    }

    /**
     * Set the map and calendar action buttons buttons
     * ref: https://stackoverflow.com/questions/4430088/get-selected-location-from-google-maps-activity
     * ref: https://stackoverflow.com/questions/4430088/get-selected-location-from-google-maps-activity
     */
    private void showMapAndCalendarButtons() {
        // open maps to the location
        this.mOpenMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventLocation eventLocation = mRetrievedEvent.getEventLocation();

                double latitude = eventLocation.getLatitude();
                double longitude = eventLocation.getLongitude();

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri googleMapsUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage(getString(R.string.android_map_request_name));

                // Attempt to start an activity that can handle the Intent
                try {
                    // try to add to gmaps
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), R.string.no_map_application_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // add to calendar button
        this.mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType(getString(R.string.android_map_request_name));

                intent.putExtra(CalendarContract.Events.TITLE, mRetrievedEvent.getName());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mRetrievedEvent.getEventLocation().getName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, mRetrievedEvent.getDescription());

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        mRetrievedEvent.getEventDate());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        mRetrievedEvent.getEventDate() + ONE_HOUR_MILLIS); // set for one hour


                try {
                    // try to add to calendar
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), R.string.calendar_not_intalled_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Show weather forecast on the screen
     */
    private void handleWeatherForecast() {

        // hide weather forecast if you cannot show the content
        this.mWeatherForecast.setVisibility(View.INVISIBLE);

        // retrieve weather if the date is closer than 5 days
        if ((mRetrievedEvent.getEventDate() - new Date().getTime()) < FIVE_DAYS_DISTANCE_MILLIS) {

            if (NetworkCheker.getInstance().isNetworkAvailable(this)) {
                this.weatherForecast.getForecast(mRetrievedEvent).subscribe(new FlowableSubscriber<Weather>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                        weatherForecastRetrieve = subscription;
                    }

                    @Override
                    public void onNext(Weather weather) {

                        // retrieve weather icon and set it
                        int weatherIcon = WeatherIconFactory.getInstance().getWeatherIconByID(weather);
                        mWeatherForecast.setImageResource(weatherIcon);

                        // show the icon on screen
                        mWeatherForecast.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                        Toast.makeText(getApplicationContext(), R.string.weather_error_toast_label, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet_no_forecast, Toast.LENGTH_LONG).show();
            }


        }


    }

    /**
     * Show the action buttons used to join, abandon or edit an event
     */
    private void setEventActionButtons() {

        // join event button
        mJoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventViewModel.addAttendeeToEvent(mRetrievedEvent.getId());
                showConfirmationNeedPopup();
            }
        });

        // abandon event button
        mAbandonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // event abandon confirm
                showEventAbandonDialog();
            }
        });

        mModifyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modificationButton) {
                modificationButton.setVisibility(View.GONE);

                // allow description modification
                mEventDescription.setEnabled(true);
                mEventDescription.requestFocus();
                mEventDescription.setSelection(mEventDescription.getText().length());

                mAcceptModifyEvent.setVisibility(View.VISIBLE);

            }
        });

        mAcceptModifyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modifyAcceptButton) {

                if (NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())) {
                    String newDescription = mEventDescription.getText().toString();
                    if (newDescription.length() != 0) {
                        // update the description
                        mRetrievedEvent.setDescription(mEventDescription.getText().toString());

                        // disable modification
                        mEventDescription.setEnabled(false);

                        // update the event
                        mEventViewModel.updateEvent(mRetrievedEvent);

                        mModifyEvent.setVisibility(View.VISIBLE);
                        modifyAcceptButton.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_popup_label, Toast.LENGTH_LONG).show();
                }


            }
        });

        mAcceptModifyEvent.setVisibility(View.GONE);

        // if the user is the event owner
        if (checkIsCurrentUserAdmin()) {

            // user cannot join or abandon his own event
            mAbandonEvent.setVisibility(View.GONE);
            mJoinEvent.setVisibility(View.GONE);

        } else {

            // attendees cannot modify an event
            mModifyEvent.setVisibility(View.GONE);

            // check if the user has already joined the event
            if (isLoggedUserJoint()) {
                mJoinEvent.setVisibility(View.GONE);
                mAbandonEvent.setVisibility(View.VISIBLE);
            } else {
                mJoinEvent.setVisibility(View.VISIBLE);
                mAbandonEvent.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Check if the user has already joint the event
     *
     * @return true if joint
     */
    private boolean isLoggedUserJoint() {

        String loggedUserID = mUserViewModel.retrieveCachedUser().getId();

        return mRetrievedEvent.getAttendingUsers().keySet().contains(loggedUserID);

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

    /**
     * Show the attendance status of the retrieved attendees
     * @param attendances Attendees info
     */
    private void showAttendaces(List<Pair<User, Boolean>> attendances) {

        final String ADMIN_LABEL = getString(R.string.admin_label);
        final String CONFIRMED_LABEL = getString(R.string.confirmed_label);
        final String NOT_CONFIRMED_LABEL = getString(R.string.not_confirmed_label);

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

            // go to the user description if click on user pic or on the name
            View.OnClickListener showUserClicked = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent showUserDetails = new Intent(v.getContext(), UserDetailsActivity.class);
                    showUserDetails.putExtra(ExtrasLabels.USER_ID, attendance.first.getId());

                    v.getContext().startActivity(showUserDetails);
                }
            };

            userPic.setOnClickListener(showUserClicked);
            userName.setOnClickListener(showUserClicked);


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
                                showAttendeeConfirmationDialog(view, attendance.first.getId());
                            }
                        });

                    }
                }

            }

            // add to the list
            mAttendees.addView(view);

        }

    }

    /**
     * Check if the logged user is the admin
     * @return true if admin
     */
    private boolean checkIsCurrentUserAdmin() {

        boolean isAdmin = false;

        // admin if the event is private or if the user has the same ID of the adminID
        if (mRetrievedEvent.isPrivateEvent() || mRetrievedEvent.getAdminID().equals(this.mUserViewModel.retrieveCachedUser().getId())) {
            isAdmin = true;
        }

        return isAdmin;
    }

    /**
     * Show a popup dialog. The admin can confirm or delete an attendance application
     * @param attendeeRow Row in the layout attendee list
     * @param attendeeID Id of the attendee
     */
    public void showAttendeeConfirmationDialog(final View attendeeRow, final String attendeeID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String title = getString(R.string.action_attendee_popup);
        String message = getString(R.string.admin_attendee_action_request);
        String positive = getString(R.string.confirm);
        String negative = getString(R.string.reject);


        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.i("confermato", userName);
                mEventViewModel.confirmAttendee(mRetrievedEvent.getId(), attendeeID);

                ((TextView) attendeeRow.findViewById(R.id.attendee_status_txt)).setText(R.string.confirmed_label);
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

    /**
     * Popup used by the user to abandon an event
     */
    public void showEventAbandonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO take the texts from the resources
        String title = getString(R.string.abandon_action_popup_title);
        String message = getString(R.string.abandon_popup_text);
        String positive = getString(R.string.confirm);
        String negative = getString(R.string.reject);


        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEventViewModel.deleteAttendee(mRetrievedEvent.getId(), mUserViewModel.retrieveCachedUser().getId());
                refresh();

            }
        });

        // negative button does nothing
        builder.setNegativeButton(negative, null);

        builder.show();
    }

    /**
     * Refresh the activity reopening it
     */
    private void refresh() {

        Intent refresh = new Intent(this, EventDescriptionActivity.class);
        Bundle oldExtras = getIntent().getExtras();
        if (oldExtras != null)
            refresh.putExtras(getIntent().getExtras());

        startActivity(refresh);

    }

    /**
     * If the user presses back, it opens the main activity
     */
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Avoid memory leaks (remove all the observers)
        if (eventRetrieve != null) {
            eventRetrieve.cancel();
        }
        if (attendeesInfosRetrieve != null) {
            attendeesInfosRetrieve.cancel();

        }
        if (weatherForecastRetrieve != null) {
            weatherForecastRetrieve.cancel();

        }

    }

    /**
     * Show a popup message if it is impossible to retrieve the requested event
     */
    private void showRetrieveErrorPopupDialog() {

        if (this.popupDialogProgress != null) {
            this.popupDialogProgress.dismiss();
        }

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // , go to the main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show a popup message if it is impossible to connect to the internet
     */
    private void showNoInternetConnectionPopup() {

        if (this.popupDialogProgress != null) {
            this.popupDialogProgress.dismiss();
        }

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go to the main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show a popup message if the user applies for an event, but still requires a confirmation
     */
    private void showConfirmationNeedPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.need_for_confirmation_popup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refresh();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
