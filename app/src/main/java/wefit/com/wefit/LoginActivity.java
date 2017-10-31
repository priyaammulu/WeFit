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

import wefit.com.wefit.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

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

        Log.i("K", "sono qui");
        // bind callback
        mFacebookLogin.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("LOGIN FB OK", loginResult.getAccessToken().getToken());

            }

            @Override
            public void onCancel() {
                Log.i("LOGIN FB CANCELLED", "sorry the user is shy");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        Log.i("K", "assegnato");
    }


    /**
     * Callback for the login button
     */
    private class FacebookLoginRequestCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {

            Log.i("LOGIN FB OK", loginResult.getAccessToken().getToken());


        }

        @Override
        public void onCancel() {

            Log.i("LOGIN FB CANCELLED", "sorry the user is shy");

        }

        @Override
        public void onError(FacebookException error) {
            Log.i("ERROR", "i really donno");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
