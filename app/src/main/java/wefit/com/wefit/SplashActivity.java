package wefit.com.wefit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import wefit.com.wefit.viewmodels.LoginViewModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        LoginViewModel mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        if (mLoginViewModel.isAuth()) {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else
            intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
