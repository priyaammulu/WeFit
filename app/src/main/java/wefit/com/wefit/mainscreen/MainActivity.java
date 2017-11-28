package wefit.com.wefit.mainscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {

    private static final int LOCATION_PERMISSION = 1;
    public static final String MAIN_FRAGMENT = "main";
    public static final String MY_ATTENDANCES_FRAGMENT = "attendances";
    public static final String PROFILE_FRAGMENT = "profile";
    private UserViewModel mUserViewModel;
    private EventViewModel mEventViewModel;
    private EventWallFragment mainFragment = new EventWallFragment();
    private ScheduledEventsFragment myAttendancesFragment = new ScheduledEventsFragment();
    private UserProfileFragment profileFragment = new UserProfileFragment();
    private LinkedList<Fragment> stack = new LinkedList<>();
    private Map<String, Fragment> fragmentMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // startActivity(new Intent(this, GioTestActivity.class));

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

    }


    private void setFragments() {

        this.fragmentMap = new HashMap<>();
        fragmentMap.put(MAIN_FRAGMENT, mainFragment);
        fragmentMap.put(MY_ATTENDANCES_FRAGMENT, myAttendancesFragment);
        fragmentMap.put(PROFILE_FRAGMENT, profileFragment);


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, mainFragment)
                .add(R.id.main_fragment, myAttendancesFragment)
                .add(R.id.main_fragment, profileFragment)
                .hide(myAttendancesFragment)
                .hide(profileFragment)
                .commit();

        stack.push(mainFragment);
    }

    // ref: https://stackoverflow.com/questions/16461483/preserving-fragment-state
    public void fragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myAttendancesFragment)
                .hide(profileFragment)
                .show(fragment)
                .commit();
        stack.push(fragment);


    }

    public void fragmentTransaction(String fragmentID) {
        fragmentTransaction(fragmentMap.get(fragmentID));
    }

    @Override
    public void onBackPressed() {
        if (stack.isEmpty() || stack.pop() instanceof EventWallFragment)
            super.onBackPressed();
        else
            fragmentTransaction(stack.pop());
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
        FusedLocationProviderClient bn = LocationServices.getFusedLocationProviderClient(this);
        bn.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            EventLocation loc = new EventLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mEventViewModel.setLocation(loc);
                        }
                    }
                });
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

                    // TODO vedere cosa fare
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;
            }
        }
    }


}
