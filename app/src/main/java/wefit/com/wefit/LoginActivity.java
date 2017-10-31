package wefit.com.wefit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wefit.com.wefit.datamodel.user.UserModel;
import wefit.com.wefit.utils.LocalKeyObjectStoreDAO;
import wefit.com.wefit.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_REQ_LOGIN_CODE = 1;

    /**
     * Facebook login button
     */
    Button mFacebookLogin;

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

        // retrieve viewmodel to communicate with
        this.loginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        // setup the services
        setupGoogleLogin();
        setupFacebookLogin();


        setContentView(R.layout.activity_login);

        // bind the services to the buttons
        this.bindFacebookButton();
        this.bindGoogleButton();


    }

    /**
     * FB login service initialization
     */
    private void setupFacebookLogin() {
        // Initialize FacebookSDK (it's deprecated, but we're using an old version of the SDK)
        // it has to be done before setContentView by specification
        FacebookSdk.sdkInitialize(getApplicationContext());
        // create a FB callback manager
        fbCallbackManager = CallbackManager.Factory.create();
    }

    /**
     * Google login service initialization
     */
    private void setupGoogleLogin() {
        // Initialize GoogleLogin SDK
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                //.requestIdToken(getString(R.string.server_client_id))
                .requestProfile()
                .build();
        // API handler for Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // TODO handle graphically this retieve error
                        Toast.makeText(LoginActivity.this,
                                LoginActivity.this.getString(R.string.connection_failure_msg),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Connect the button on the layout to the Google login service
     */
    private void bindGoogleButton() {

        mGoogleLogin = (Button) findViewById(R.id.google_login_btn);

        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_REQ_LOGIN_CODE);
            }
        });


    }

    /**
     * Connect the button on the layout to the Facebook login service
     */
    private void bindFacebookButton() {

        // retrieve fb login button
        mFacebookLogin = (Button) this.findViewById(R.id.facebook_login_btn);

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookLoginRequestCallback());

        mFacebookLogin.setOnClickListener(new View.OnClickListener() {

            // list of required permission from facebook
            final List<String> requestedPermissions = Arrays.asList(
                    "public_profile",
                    "email");

            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, requestedPermissions);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the request has the specified code GOOGLE_REQ_LOGIN_CODE, it is related to google
        if (requestCode == GOOGLE_REQ_LOGIN_CODE) { // google handling
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleLoginResultHandling(result);
        } else { // facebook request has NO USER DEFINED CODEs
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * Handle the login result from Facebook API
     */
    private class FacebookLoginRequestCallback implements FacebookCallback<LoginResult> {

        private static final String REQUESTED_FIELDS_KEY = "fields";
        private static final String REQUESTED_FIELDS = "email, gender, name";

        @Override
        public void onSuccess(final LoginResult loginResult) {

            // request service callback handler
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            UserModel user = new UserModel();
                            JSONObject jsonObject = response.getJSONObject();

                            if (jsonObject != null) {

                                String name, gender, email;

                                // cannot assume that the user has all the specified fields

                                try {
                                    name = jsonObject.getString("name");
                                } catch (JSONException e) {
                                    name = null;
                                }

                                try {
                                    email = jsonObject.getString("email");
                                } catch (JSONException e) {
                                    email = null;
                                }

                                try {
                                    gender = jsonObject.getString("gender");
                                } catch (JSONException e) {
                                    gender = null;
                                }

                                // fill of the user pojo
                                user.setAuthKey(loginResult.getAccessToken().getToken());
                                user.setUserId(loginResult.getAccessToken().getUserId());
                                user.setName(name);
                                user.setGender(gender);
                                user.setEmail(email);
                                loginViewModel.associateUser(user);

                                Log.i("INFO", "Now you can go chap!");
                                Log.i("USER", user.toString());

                                // TODO deve essere migliorato
                                LocalKeyObjectStoreDAO.getInstance().save(LoginActivity.this, "user", user);

                                // TODO remove, this is for the authomatica logout
                                LoginManager.getInstance().logOut();

                                // go to main act
                                startMainActivity();

                            }
                        }
                    });

            // star the asyncronous request to the FB login service
            Bundle parameters = new Bundle();
            parameters.putString(REQUESTED_FIELDS_KEY, REQUESTED_FIELDS);
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

    /**
     * Handle the login result from Google API
     *
     * @param result Result of the call
     */
    private void googleLoginResultHandling(GoogleSignInResult result) {
        Log.d("GOOGLE succes", "googleLoginResultHandling:" + result.isSuccess());

        // if the request was successfull
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount retrievedAccount = result.getSignInAccount();


            // creation of the user pojo
            UserModel user = new UserModel();
            user.setAuthKey(retrievedAccount.getIdToken());
            user.setUserId(retrievedAccount.getId());
            user.setName(retrievedAccount.getDisplayName());
            user.setGender(null);
            user.setEmail(retrievedAccount.getEmail());

            loginViewModel.associateUser(user);

            Log.d("userinfo", "account retrieved from the server");
            Log.d("userinfo", user.toString());

            // TODO deve essere migliorato
            LocalKeyObjectStoreDAO.getInstance().save(LoginActivity.this, "user", user);


            // TODO signout, to remove
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d("loggedout", "success");

                        }
                    });

            startMainActivity();


        } else { // error handling
            // TODO handle graphically this retieve error
            Log.d("error", "cannot retrieveerror");
        }
    }

    /**
     * Launch the Main activity
     * Use after the login
     */
    private void startMainActivity() {
        Intent activityChange = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(activityChange);
    }


}
