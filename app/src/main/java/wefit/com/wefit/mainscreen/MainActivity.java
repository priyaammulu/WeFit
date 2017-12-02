package wefit.com.wefit.mainscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.mainscreen.fragments.EventWallFragment;
import wefit.com.wefit.mainscreen.fragments.ScheduledEventsFragment;
import wefit.com.wefit.mainscreen.fragments.UserProfileFragment;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;


/**
 * Created by lorenzo on 10/28/17.
 * This is the first Activity showed in the authenticated area of the app
 */
public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {

    /**
     * Location permission constant
     */
    private static final int LOCATION_PERMISSION = 1;
    /**
     * Request check settings
     */
    public static final int REQUEST_CHECK_SETTINGS = 2;

    /**
     * Constants used to handle fragments
     */
    public static final String WALL_FRAGMENT = "main";
    public static final String MY_EVENTS_FRAGMENT = "attendances";
    public static final String PROFILE_FRAGMENT = "profile";

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * Event View Model
     */
    private EventViewModel mEventViewModel;
    /**
     * Event Wall Fragment
     */
    private EventWallFragment mainFragment = new EventWallFragment();
    /**
     * Scheduled Events Fragment
     */
    private ScheduledEventsFragment myEventsFragment = new ScheduledEventsFragment();

    /**
     * Profile Fragment
     */
    private UserProfileFragment profileFragment = new UserProfileFragment();
    /**
     * Stack used to manage Fragment states (in order to handle back button)
     */
    private LinkedList<Fragment> stack = new LinkedList<>();
    /**
     * Maps containing fragments
     */
    private Map<String, Fragment> fragmentMap;

    /**
     * Bottombar components
     */
    private LinearLayout mWallButtonPressed;
    private LinearLayout mWallButton;
    private LinearLayout mProfileButton;
    private LinearLayout mProfileButtonPressed;
    private LinearLayout mMyEventsButton;
    private LinearLayout mMyEventsButtonPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = getUserViewModel();
        mEventViewModel = getEventViewModel();

        // check if the user is logged (security)
        if (!mUserViewModel.isAuth()) {
            signOut();
        }

        setContentView(R.layout.activity_main);
        bindLayoutComponents();
        setFragments();

        // find user location
        provideLocation();
    }

    /**
     * It binds UI layout to properties
     */
    private void bindLayoutComponents() {
        mWallButtonPressed = (LinearLayout) findViewById(R.id.button_wall_pressed);
        mWallButton = (LinearLayout) findViewById(R.id.button_wall_not_pressed);
        mProfileButton = (LinearLayout) findViewById(R.id.profile_button_not_pressed);
        mProfileButtonPressed = (LinearLayout) findViewById(R.id.profile_button_pressed);
        mMyEventsButton = (LinearLayout) findViewById(R.id.myevents_button_not_pressed);
        mMyEventsButtonPressed = (LinearLayout) findViewById(R.id.myevents_button_pressed);

        mWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction(WALL_FRAGMENT);
            }
        });

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction(PROFILE_FRAGMENT);
            }
        });

        mMyEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction(MY_EVENTS_FRAGMENT);
            }
        });

    }

    /**
     * It initializes fragments
     */
    private void setFragments() {
        this.fragmentMap = new HashMap<>();
        fragmentMap.put(WALL_FRAGMENT, mainFragment);
        fragmentMap.put(MY_EVENTS_FRAGMENT, myEventsFragment);
        fragmentMap.put(PROFILE_FRAGMENT, profileFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, mainFragment)
                .add(R.id.main_fragment, myEventsFragment)
                .add(R.id.main_fragment, profileFragment)
                .hide(myEventsFragment)
                .hide(profileFragment)
                .commit();
        stack.push(mainFragment);
    }

    /**
     * Change fragment
     * ref: https://stackoverflow.com/questions/16461483/preserving-fragment-state
     *
     * @param fragment current fragment
     */
    public void fragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myEventsFragment)
                .hide(profileFragment)
                .show(fragment)
                .commitAllowingStateLoss();
        stack.push(fragment);
    }

    /**
     * It shows the right fragment
     */
    public void fragmentTransaction(String fragmentID) {

        if (fragmentID.equals(WALL_FRAGMENT)) {
            openWall();
        }
        if (fragmentID.equals(MY_EVENTS_FRAGMENT)) {
            openMyEvents();
        }
        if (fragmentID.equals(PROFILE_FRAGMENT)) {
            openProfile();

        }

        fragmentTransaction(fragmentMap.get(fragmentID));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (stack.isEmpty() || stack.pop() instanceof EventWallFragment)
            super.onBackPressed();
        else {

            Fragment oldfragment = stack.pop();

            String switchFragmentID = null;

            if (oldfragment == mainFragment) {
                switchFragmentID = WALL_FRAGMENT;
            } else if (oldfragment == myEventsFragment) {
                switchFragmentID = MY_EVENTS_FRAGMENT;
            } else if (oldfragment == profileFragment) {
                switchFragmentID = PROFILE_FRAGMENT;
            }

            fragmentTransaction(switchFragmentID);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // position retrieval
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    enableGoogleApiClient();
                else {
                    fragmentTransaction(WALL_FRAGMENT);
                    Toast.makeText(getApplicationContext(), R.string.location_permission_notallowed_toast, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * It signs out the user
     */
    private void signOut() {
        mUserViewModel.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public EventViewModel getEventViewModel() {
        if (mEventViewModel == null)
            mEventViewModel = ((WefitApplication) getApplication()).getEventViewModel();
        return mEventViewModel;
    }

    @Override
    public UserViewModel getUserViewModel() {
        if (this.mUserViewModel == null)
            mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        return mUserViewModel;
    }

    /**
     * Modify top and bottom bar according to the wall fragment
     */
    private void openWall() {
        mWallButtonPressed.setVisibility(View.VISIBLE);
        mWallButton.setVisibility(View.GONE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyEventsButton.setVisibility(View.VISIBLE);
        mMyEventsButtonPressed.setVisibility(View.GONE);

    }

    /**
     * Modify top and bottom bar according to the profile fragment
     */
    private void openProfile() {
        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.GONE);
        mProfileButtonPressed.setVisibility(View.VISIBLE);
        mMyEventsButton.setVisibility(View.VISIBLE);
        mMyEventsButtonPressed.setVisibility(View.GONE);
    }

    /**
     * Modify top and bottom bar according to the my events fragment
     */
    private void openMyEvents() {

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyEventsButton.setVisibility(View.GONE);
        mMyEventsButtonPressed.setVisibility(View.VISIBLE);

    }

    /**
     * Checks for location permissions and retrieves it
     */
    public void provideLocation() {


        // ref: https://stackoverflow.com/questions/10117587/gps-is-not-enabled-but-isproviderenabled-is-returning-true
        // check if the GPS is enabled
        String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.equals("")) {
            //GPS Enabled
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions(getTargetFragment(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            } else {
                enableGoogleApiClient();
            }
        } else {
            // if the GPS is not enabled, position in dublin
            Toast.makeText(getApplicationContext(), R.string.gps_not_available, Toast.LENGTH_LONG).show();

            fragmentTransaction(WALL_FRAGMENT);
        }

    }

    /**
     * Asks the user to enable the GPS
     */
    @SuppressLint("MissingPermission")
    public void enableGoogleApiClient() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                LocationCallback callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            EventLocation loc = new EventLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mEventViewModel.setLocation(loc);
                        }
                    }


                };


                FusedLocationProviderClient fuserLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                // retrieve new location
                fuserLocationClient.requestLocationUpdates(locationRequest,
                        callback,
                        null);

                // parallely get last known location, to have info as soon as possible
                fuserLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            EventLocation loc = new EventLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mEventViewModel.setLocation(loc);
                        }



                        fragmentTransaction(WALL_FRAGMENT);

                    }
                });


            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getParent(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            // permission for position
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableGoogleApiClient();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.location_permission_notallowed_toast, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }
}
