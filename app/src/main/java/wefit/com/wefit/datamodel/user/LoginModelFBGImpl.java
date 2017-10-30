package wefit.com.wefit.datamodel.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Map;

import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by gioacchino on 30/10/2017.
 */

public class LoginModelFBGImpl implements LoginModel {

    /**
     * Facebook request handling
     */
    CallbackManager fbCallbackManager;

    /**
     * Current logged user
     */
    FBUserImpl loggedUser;


    @Override
    public boolean isAuth() {
        return false;
    }

    @Override
    public void configure(Map<LoginViewModel.Configuration, Object> configuration) {
        
        configureFacebookSDK((Context) configuration.get(LoginViewModel.Configuration.FB_CONFIG));

    }

    @Override
    public void setHandlers(Map<LoginViewModel.Handlers, View> handlers) {
        
        configureFacebookLoginButtonAction((LoginButton) handlers.get(LoginViewModel.Handlers.FACEBOOK_HANDLER));

    }

    @Override
    public void passActivityResults(int requestCode, int resultCode, Intent data) {

        //Log.i("DATA USEFUL", data.);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void configureFacebookSDK(Context context) {

        // facebook sdk initialization
        // TODO try do to better
        FacebookSdk.sdkInitialize(context);

        // create a callback manager
        fbCallbackManager = CallbackManager.Factory.create();


    }

    private void configureFacebookLoginButtonAction(LoginButton loginButton) {

        loginButton.registerCallback(fbCallbackManager, new FacebookLoginRequestCallback());

    }

    private class FacebookLoginRequestCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {

            loggedUser = new FBUserImpl(
                    loginResult.getAccessToken().getUserId(),
                    loginResult.getAccessToken().getToken()
            );

            Log.i("LOGIN FB SUCCESS", loggedUser.toString());

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


}
