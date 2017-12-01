package wefit.com.wefit.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.mainscreen.fragments.EventWallFragment;
import wefit.com.wefit.mainscreen.fragments.FragmentsInteractionListener;
import wefit.com.wefit.mainscreen.fragments.ScheduledEventsFragment;
import wefit.com.wefit.mainscreen.fragments.UserProfileFragment;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

import static wefit.com.wefit.mainscreen.fragments.EventWallFragment.REQUEST_CHECK_SETTINGS;

/**
 * Created by lorenzo on 10/28/17.
 * This is the first Activity showed in the authenticated area of the app
 */
public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {
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
                    mainFragment.enableGoogleApiClient();
                else {
                    mainFragment.fetchEvents();
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


}
