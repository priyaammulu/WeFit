package wefit.com.wefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.main.MainActivity;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.NetworkCheker;
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

    /**
     * Firebase authentication manager
     */
    private FirebaseAuth mAuth;

    private ProgressDialog popupDialogProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // check if the device is online
        if (!NetworkCheker.getInstance().isNetworkAvailable(this)) {
            Toast.makeText(LoginActivity.this,
                    LoginActivity.this.getString(R.string.connection_failure_msg),
                    Toast.LENGTH_LONG)
                    .show();
        }

        // retrieve viewmodel to communicate with
        this.loginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        hideActionBar();

        // setup the services
        setupFirebaseAuth();
        setupGoogleLogin();
        setupFacebookLogin();


        setContentView(R.layout.activity_login);

        // bind the services to the buttons
        this.bindFacebookButton();
        this.bindGoogleButton();
    }

    private void hideActionBar() {
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    private void setupFirebaseAuth() {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
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
                showWaitSpinner();

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
                showWaitSpinner();
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

        @Override
        public void onSuccess(final LoginResult loginResult) {

            Log.i("LOGIN FB CANCELLED", "sorry the user is shy");

            handleFacebookAccessTokenForFirebase(loginResult.getAccessToken());

        }

        @Override
        public void onCancel() {
            // TODO mettere in inglese!
            stopSpinner();
            Toast.makeText(getApplicationContext(), "Hey, non ti vergognare", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            // TODO gestire meglio graficamente
            stopSpinner();
            Toast.makeText(getApplicationContext(), "Ops, facebook è impazzito", Toast.LENGTH_LONG).show();
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

            handleGoogleAccessTokenForFirebase(result.getSignInAccount());


        } else { // error handling
            stopSpinner();
            // TODO mettere in inglese!
            Toast.makeText(this, "Ops, qualcosa è andato storto", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Access firebase with FB credential
     *
     * @param token Facebook access token
     */
    private void handleFacebookAccessTokenForFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        buildFirebaseUser(credential);
    }

    /**
     * Access firebase with G credential
     *
     * @param account Google access token container
     */
    private void handleGoogleAccessTokenForFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        buildFirebaseUser(credential);
    }

    /**
     * Connect to the server
     * If necessary it creates the user
     *
     * @param credential Wrapped User credential (facebook or google)
     */
    private void buildFirebaseUser(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            // this is a round trip operation to guarantee that the user is in the remote DB
                            loginViewModel.retrieveUser().subscribe(new Consumer<User>() {
                                @Override
                                public void accept(User user) throws Exception {
                                    Log.i("USER", user.toString());

                                    // now the user is retrieved and you can go to the main activity
                                    startMainActivity();

                                }
                            });
                            // go to the main activity

                        } else {
                            stopSpinner();
                            // TODO gestire meglio graficamente
                            Toast.makeText(getApplicationContext(), "Ops, abbiamo problemi al server", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    /**
     * Launch the Main activity
     * Use after the login
     */
    private void startMainActivity() {

        stopSpinner();

        // check if the auth has been successful
        if (this.loginViewModel.isAuth()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // TODO mettere in inglese!
            Toast.makeText(this, "Hey, non hai ancora confermato la tua mail!", Toast.LENGTH_LONG).show();
        }

    }

    private void showWaitSpinner() {
        // tODO english
        popupDialogProgress = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
    }

    private void stopSpinner() {
        popupDialogProgress.dismiss();
    }


}
