package wefit.com.wefit.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.mainscreen.fragments.EventWallFragment;
import wefit.com.wefit.mainscreen.fragments.ScheduledEventsFragment;
import wefit.com.wefit.mainscreen.fragments.UserProfileFragment;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

import static wefit.com.wefit.mainscreen.fragments.EventWallFragment.REQUEST_CHECK_SETTINGS;

public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {
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

    // cannot put it in the fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    mainFragment.enableGoogleApiClient();
                else
                    mainFragment.fetchEvents();
                break;
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
