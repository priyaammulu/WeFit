package wefit.com.wefit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends AppCompatActivity {

    LoginButton mFbLoginButton;
    TextView mLoginFacebookOutputText;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // facebook sdk initialization
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        // facebook handling graphics
        mFbLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
        mLoginFacebookOutputText = (TextView) findViewById(R.id.facebook_status_textview);

        // callback manager
        callbackManager = CallbackManager.Factory.create();

        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mLoginFacebookOutputText.setText("login success\n " +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        loginResult.getAccessToken().getToken()
                );

            }

            @Override
            public void onCancel() {

                mLoginFacebookOutputText.setText("Login cancelled");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
