package wefit.com.wefit;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import wefit.com.wefit.datamodel.user.UserModel;
import wefit.com.wefit.utils.LocalKeyObjectStoreDAO;
import wefit.com.wefit.viewmodels.LoginViewModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        LoginViewModel mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        if (mLoginViewModel.isAuth())
            intent = new Intent(this, MainActivity.class);
        else
            intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
