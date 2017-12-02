package wefit.com.wefit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * Created by lorenzo on 10/28/17.
 * This activity is just the splash-screen of the application.
 * It redirects the user to the login activity if he/she isn't logged yet
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        UserViewModel mLoginViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        // check if the user is already logged
        if (mLoginViewModel.isAuth()) {

            // refresh user data
            mLoginViewModel.retrieveUserFromRemoteStore();

            // go to the main activity
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else {
            // if the user is not logged, go to the login
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);

        // security reasons
        finish();
    }

}
