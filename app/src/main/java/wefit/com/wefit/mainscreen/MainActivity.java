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

import java.util.LinkedList;

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
    private UserViewModel mUserViewModel;
    private EventViewModel mEventViewModel;
    private EventWallFragment mainFragment = new EventWallFragment();
    private ScheduledEventsFragment myAttendancesFragment = new ScheduledEventsFragment();
    private UserProfileFragment profileFragment = new UserProfileFragment();
    private LinkedList<Fragment> stack = new LinkedList<>();
    //views
    private ImageView leftTopBottom;
    private TextView middleTopBottom;
    private ImageView topBarlogo;
    private ImageView rightTopButtom;
    private ImageView home_icon;
    private TextView home_text;
    private ImageView myEvents_icon;
    private TextView myEvents_text;
    private ImageView settings_icon;
    private TextView settings_text;
    private LinearLayout mBottomBar;
    private LinearLayout myEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // startActivity(new Intent(this, GioTestActivity.class));
        mUserViewModel = getUserViewModel();
        mEventViewModel = getEventViewModel();
        if (!mUserViewModel.isAuth())
            signOut();
        setContentView(R.layout.activity_main);
        bindLayoutComponents();
        setFragments();
    }

    private void bindLayoutComponents() {
        Button mSignOut = (Button) findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        myEvents = (LinearLayout) findViewById(R.id.myEvents);
        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_caledar_pressed);

                myEvents_text = (TextView) findViewById(R.id.myEvents_text);
                myEvents_text.setTextColor(getResources().getColor(R.color.logoBlue));

                myEvents_text.setTypeface(null, Typeface.BOLD);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                settings_text = (TextView) findViewById(R.id.settings_text);
                settings_text.setTextColor(getResources().getColor(R.color.black));
                settings_text.setTypeface(null, Typeface.NORMAL);

                home_text = (TextView) findViewById(R.id.home_text);
                home_text.setTextColor(getResources().getColor(R.color.black));
                home_text.setTypeface(null, Typeface.NORMAL);


                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search);

                fragmentTransaction(myAttendancesFragment);

            }
        });
        final LinearLayout settings = (LinearLayout) findViewById(R.id.button_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(profileFragment);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user_pressed);

                settings_text = (TextView) findViewById(R.id.settings_text);
                settings_text.setTextColor(getResources().getColor(R.color.logoBlue));
                settings_text.setTypeface(null, Typeface.BOLD);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                home_text = (TextView) findViewById(R.id.home_text);
                home_text.setTextColor(getResources().getColor(R.color.black));
                home_text.setTypeface(null, Typeface.NORMAL);

                myEvents_text = (TextView) findViewById(R.id.myEvents_text);
                myEvents_text.setTextColor(getResources().getColor(R.color.black));
                myEvents_text.setTypeface(null, Typeface.NORMAL);


                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search);
            }
        });

        LinearLayout main = (LinearLayout) findViewById(R.id.button_main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(mainFragment);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home_pressed);

                home_text = (TextView) findViewById(R.id.home_text);
                home_text.setTextColor(getResources().getColor(R.color.logoBlue));
                home_text.setTypeface(null, Typeface.BOLD);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                settings_text = (TextView) findViewById(R.id.settings_text);
                settings_text.setTextColor(getResources().getColor(R.color.black));
                settings_text.setTypeface(null, Typeface.NORMAL);

                myEvents_text = (TextView) findViewById(R.id.myEvents_text);
                myEvents_text.setTextColor(getResources().getColor(R.color.black));
                myEvents_text.setTypeface(null, Typeface.NORMAL);


                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);

                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search);

            }
        });


    }


    private void setFragments() {
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
    private void fragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myAttendancesFragment)
                .hide(profileFragment)
                .show(fragment)
                .commit();
        stack.push(fragment);
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

    @Override
    public void fillInIcons(int IconLeft, String iconMiddle, int iconRight) {
        //Icons for main Activity
        topBarlogo = (ImageView) findViewById(R.id.topBarLogo);
        topBarlogo.setVisibility(GONE);

        leftTopBottom = (ImageView) findViewById(R.id.leftTopButton);
        leftTopBottom.setImageResource(IconLeft);

        middleTopBottom = (TextView) findViewById(R.id.middleTopButton);
        middleTopBottom.setText(iconMiddle);
        middleTopBottom.setVisibility(View.VISIBLE);

        rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
        rightTopButtom.setImageResource(iconRight);

        LinearLayout search = (LinearLayout) findViewById(R.id.search_layout);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search_pressed);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);
            }
        });

    }

    @Override
    public void changeStatusPressedInMain() {
        LinearLayout search = (LinearLayout) findViewById(R.id.search_layout);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search_pressed);

                leftTopBottom = (ImageView) findViewById(R.id.leftTopButton);
                leftTopBottom.setImageResource(R.drawable.ic_edit);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);
            }
        });

        final LinearLayout leftTopButtons = (LinearLayout) findViewById(R.id.firstTopIcon_layout);
        leftTopButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftTopBottom = (ImageView) findViewById(R.id.leftTopButton);
                leftTopBottom.setImageResource(R.drawable.ic_edit_pressed);

                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_search);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);
            }
        });
    }

    @Override
    public void toggleBottomBar() {
        if (mBottomBar.getVisibility() == View.VISIBLE)
            mBottomBar.setVisibility(View.INVISIBLE);
        else
            mBottomBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void changeStatusPressedInProfile() {
        LinearLayout warning = (LinearLayout) findViewById(R.id.search_layout);
        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_warning_pressed);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);
            }
        });


        LinearLayout arrow = (LinearLayout) findViewById(R.id.firstTopIcon_layout);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftTopBottom = (ImageView) findViewById(R.id.leftTopButton);
                leftTopBottom.setImageResource(R.drawable.ic_arrow_pressed);

                rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
                rightTopButtom.setImageResource(R.drawable.ic_warning);

                home_icon = (ImageView) findViewById(R.id.home_icon);
                home_icon.setImageResource(R.drawable.ic_home);

                settings_icon = (ImageView) findViewById(R.id.settings_icon);
                settings_icon.setImageResource(R.drawable.ic_user);

                myEvents_icon = (ImageView) findViewById(R.id.myEvents_icon);
                myEvents_icon.setImageResource(R.drawable.ic_calendar);
            }
        });
    }

    @Override
    public void fillInIconsWithLogo(int iconLeft, int logo, int iconRight) {
        middleTopBottom = (TextView) findViewById(R.id.middleTopButton);
        middleTopBottom.setVisibility(GONE);

        leftTopBottom = (ImageView) findViewById(R.id.leftTopButton);
        leftTopBottom.setImageResource(iconLeft);

        topBarlogo = (ImageView) findViewById(R.id.topBarLogo);
        topBarlogo.setImageResource(logo);
        topBarlogo.setVisibility(View.VISIBLE);

        rightTopButtom = (ImageView) findViewById(R.id.rightTopButton);
        rightTopButtom.setImageResource(iconRight);
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
