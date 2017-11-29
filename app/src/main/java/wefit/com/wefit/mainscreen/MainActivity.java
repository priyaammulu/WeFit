package wefit.com.wefit.mainscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import wefit.com.wefit.viewmodels.UserViewModel;
import wefit.com.wefit.viewmodels.EventViewModel;

public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {

    private static final int LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    public static final String WALL_FRAGMENT = "main";
    public static final String MY_EVENTS_FRAGMENT = "attendances";
    public static final String PROFILE_FRAGMENT = "profile";


    private UserViewModel mUserViewModel;
    private EventViewModel mEventViewModel;
    private EventWallFragment mainFragment = new EventWallFragment();
    private ScheduledEventsFragment myEventsFragment = new ScheduledEventsFragment();
    private UserProfileFragment profileFragment = new UserProfileFragment();
    private LinkedList<Fragment> stack = new LinkedList<>();
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
    }

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

    // ref: https://stackoverflow.com/questions/16461483/preserving-fragment-state
    public void fragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myEventsFragment)
                .hide(profileFragment)
                .show(fragment)
                .commit();
        stack.push(fragment);
    }

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
    public void onBackPressed() {
        if (stack.isEmpty() || stack.pop() instanceof EventWallFragment)
            super.onBackPressed();
        else {

            Fragment oldfragment = stack.pop();

            String switchFragmentID = null;

            if (oldfragment == mainFragment) {
                switchFragmentID = WALL_FRAGMENT;
            }
            else if (oldfragment == myEventsFragment){
                switchFragmentID = MY_EVENTS_FRAGMENT;
            }
            else if (oldfragment == profileFragment){
                switchFragmentID = PROFILE_FRAGMENT;
            }

            fragmentTransaction(switchFragmentID);
        }

    }

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

    @Override
    public void provideLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        } else {
            enableGoogleApiClient();
        }
    }


    @SuppressLint("MissingPermission")
    private void enableGoogleApiClient() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationCallback mLocationCallback = new LocationCallback() {
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
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null /* Looper */);
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
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    enableGoogleApiClient();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
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

    private void openWall() {

        mWallButtonPressed.setVisibility(View.VISIBLE);
        mWallButton.setVisibility(View.GONE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyEventsButton.setVisibility(View.VISIBLE);
        mMyEventsButtonPressed.setVisibility(View.GONE);

    }

    private void openProfile() {

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.GONE);
        mProfileButtonPressed.setVisibility(View.VISIBLE);
        mMyEventsButton.setVisibility(View.VISIBLE);
        mMyEventsButtonPressed.setVisibility(View.GONE);

    }

    private void openMyEvents() {

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyEventsButton.setVisibility(View.GONE);
        mMyEventsButtonPressed.setVisibility(View.VISIBLE);

    }


}
