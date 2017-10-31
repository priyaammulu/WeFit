package wefit.com.wefit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import wefit.com.wefit.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity{

    private static final int GOOGLE_REQ_LOGIN_CODE = 1;

    /**
     * Facebook login button
     */
    LoginButton mFacebookLogin;

    /**
     * G login button
     */
    Button mGoogleLogin;

    /**
     * Handle login callback functions
     */
    CallbackManager fbCallbackManager;

    /**
     * Google API manager client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Handle to the login VM
     */
    LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.loginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        // google sign in configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //.requestServerAuthCode(getString(R.string.server_client_id))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // tODO non so cosa fare
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FacebookSDK (it's deprecated, but we're using an old version of the SDK)
        // it has to be done before setContentView by specification
        FacebookSdk.sdkInitialize(getApplicationContext());
        // create a callback manager
        fbCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        this.setupFacebookButton();

        this.setupGoogleButton();


    }

    private void setupGoogleButton() {

        mGoogleLogin = (Button) findViewById(R.id.google_login_btn);

        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });


    }

    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_REQ_LOGIN_CODE);


    }

    private void setupFacebookButton() {

        // retrieve fb login button
        mFacebookLogin = (LoginButton) this.findViewById(R.id.facebook_login_btn);

        // bind callback
        mFacebookLogin.registerCallback(fbCallbackManager, new FacebookLoginRequestCallback());
    }


    /**
     * Callback for the login button
     */
    private class FacebookLoginRequestCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(final LoginResult loginResult) {
            //Log.i("LOGIN FB OK", loginResult.getAccessToken().getToken());


            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            JSONObject jsonObject = response.getJSONObject();
                            if (jsonObject != null) {

                                String name, gender, email;

                                try {
                                    name = jsonObject.getString("name");
                                } catch (JSONException e) {
                                    name = "Noname";
                                }

                                try {
                                    email = jsonObject.getString("email");
                                } catch (JSONException e) {
                                    email = "Nomail";
                                }

                                try {
                                    gender = jsonObject.getString("gender");
                                } catch (JSONException e) {
                                    gender = "Nogender";
                                }

                                loginViewModel.associateUser(
                                        loginResult.getAccessToken().getToken(),
                                        loginResult.getAccessToken().getUserId(),
                                        gender,
                                        name,
                                        email);

                                Log.i("INFO", "Now you can go chap!");

                                // go to main act
                                startMainActivity();

                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email, gender, name");
            request.setParameters(parameters);
            request.executeAsync();


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

        if (requestCode == GOOGLE_REQ_LOGIN_CODE) {
            // google handling
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GOOGLE succes", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //String authcode = acct != null ? acct.getServerAuthCode() : null;
            Log.d("userinfo", acct.getId());

            // TODO signout
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d("loggedout", "success");
                        }
                    });

        } else {
            Log.d("error", "cannot retrieveerror");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginViewModel = null;
    }

    private void startMainActivity() {
        Intent activityChange = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(activityChange);
    }


}
