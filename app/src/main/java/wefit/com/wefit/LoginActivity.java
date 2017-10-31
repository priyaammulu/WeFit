package wefit.com.wefit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Observable;
import java.util.Observer;

import wefit.com.wefit.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements Observer {

    /**
     * Facebook login button
     */
    LoginButton mFacebookLogin;

    /**
     * Handle login callback functions
     */
    CallbackManager fbCallbackManager;

    /**
     * Handle to the login VM
     */
    LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.loginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        // Initialize FacebookSDK (it's deprecated, but we're using an old version of the SDK)
        // it has to be done before setContentView by specification
        FacebookSdk.sdkInitialize(getApplicationContext());
        // create a callback manager
        fbCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        this.setupFacebookButton();


    }

    private void setupFacebookButton() {

        // retrieve fb login button
        mFacebookLogin = (LoginButton) this.findViewById(R.id.facebook_login_btn);

        loginViewModel.registerLoginWait(this);

        // bind callback
        mFacebookLogin.registerCallback(fbCallbackManager, new FacebookLoginRequestCallback());
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.i("Weee", "OSSERVATO");
    }


    /**
     * Callback for the login button
     */
    private class FacebookLoginRequestCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            //Log.i("LOGIN FB OK", loginResult.getAccessToken().getToken());

            loginViewModel.associateUser(
                    loginResult.getAccessToken().getToken(),
                    loginResult.getAccessToken().getUserId());

        }

        @Override
        public void onCancel() {
            Log.i("LOGIN FB CANCELLED", "sorry the user is shy");
        }

        @Override
        public void onError(FacebookException error) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginViewModel = null;
    }


}
