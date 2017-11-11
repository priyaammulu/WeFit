package wefit.com.wefit.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.viewmodels.LoginViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements FragmentsInteractionListener {
    private static final int LOCATION_PERMISSION = 1;
    private LoginViewModel mLoginViewModel;
    private MainViewModel mMainViewModel;
    private MainFragment mainFragment = new MainFragment();
    private MyeventsFragment myeventsFragment = new MyeventsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private LinkedList<Fragment> stack = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();
        mMainViewModel = getMainViewModel();
        if (!mLoginViewModel.isAuth())
            signOut();
        setContentView(R.layout.activity_main);
        bind();
        setFragments();
    }

    private void bind() {
        Button mSignOut = (Button) findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        LinearLayout myEvents = (LinearLayout) findViewById(R.id.button_myevents);
        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(myeventsFragment);
            }
        });
        final LinearLayout settings = (LinearLayout) findViewById(R.id.button_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(settingsFragment);
            }
        });
        LinearLayout main = (LinearLayout) findViewById(R.id.button_main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(mainFragment);
            }
        });
    }

    private void setFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, mainFragment)
                .add(R.id.main_fragment, myeventsFragment)
                .add(R.id.main_fragment, settingsFragment)
                .hide(myeventsFragment)
                .hide(settingsFragment)
                .commit();
        stack.push(mainFragment);
    }

    // ref: https://stackoverflow.com/questions/16461483/preserving-fragment-state
    private void fragmentTransaction(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myeventsFragment)
                .hide(settingsFragment)
                .show(fragment)
               // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
            stack.push(fragment);
    }

    @Override
    public void onBackPressed() {
        if (stack.isEmpty() || stack.pop() instanceof MainFragment)
            super.onBackPressed();
        else
            fragmentTransaction(stack.pop());
    }

    private void signOut() {
        mLoginViewModel.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public MainViewModel getMainViewModel() {
        if (mMainViewModel == null)
            mMainViewModel = ((WefitApplication) getApplication()).getMainViewModel();
        return mMainViewModel;
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
                            wefit.com.wefit.pojo.Location loc = new wefit.com.wefit.pojo.Location();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mMainViewModel.setLocation(loc);
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

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
