package wefit.com.wefit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.TreeMap;

import wefit.com.wefit.viewmodels.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    LoginButton mFbLoginButton;
    //TextView mLoginFacebookOutputText;
    CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // access to the login viewmodel
        LoginViewModel loginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();

        // login sdk configurations parameters
        Map<LoginViewModel.Configuration, Object> loginConfig = new TreeMap<>();
        loginConfig.put(LoginViewModel.Configuration.FB_CONFIG, getApplicationContext());

        // request configuration of login managers
        loginViewModel.configure(loginConfig);

        // buil the activity content
        setContentView(R.layout.activity_login);

        // facebook handling graphics
        mFbLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
        //mLoginFacebookOutputText = (TextView) findViewById(R.id.facebook_status_textview);

        // callback manager
        //callbackManager = CallbackManager.Factory.create();



        /*
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mLoginFacebookOutputText.setText("login success\n " +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        loginResult.getAccessToken().getToken()
                );

                handleFacebookToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {

                mLoginFacebookOutputText.setText("Login cancelled");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        */




    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookToken(AccessToken token) {

        // Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }
}


