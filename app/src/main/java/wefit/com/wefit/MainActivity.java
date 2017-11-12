package wefit.com.wefit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import wefit.com.wefit.viewmodels.LoginViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentInteractionListener {
    private static final int LOCATION_PERMISSION = 1;
    private LoginViewModel mLoginViewModel;
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();
        mMainViewModel = ((WefitApplication) getApplication()).getMainViewModel();


        if (!mLoginViewModel.isAuth())
            signOut();

        setContentView(R.layout.activity_main);
        bind();
    }

    private void bind() {
        Button mSignOut = (Button) findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut() {
        mLoginViewModel.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public MainViewModel getMainViewModel() {
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
