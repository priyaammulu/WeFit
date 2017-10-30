package wefit.com.wefit.datamodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.List;
import java.util.Map;

import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by gioacchino on 30/10/2017.
 */

public class LoginModelFBGImpl implements LoginModel {

    // facebook handling
    CallbackManager fbCallbackManager;
    // FacebookCallback<LoginResult> loginResultFacebookCallback;

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

            Log.i("LOGIN FB SUCCESS", "login success\n " +
                    loginResult.getAccessToken().getUserId() + "\n" +
                    loginResult.getAccessToken().getToken()
            );

        }

        @Override
        public void onCancel() {

            Log.i("LOGIN FB CANCELLED", "sorry the user is shy");

        }

        @Override
        public void onError(FacebookException error) {
            // donno what to do
            //throw new
        }
    }


}
