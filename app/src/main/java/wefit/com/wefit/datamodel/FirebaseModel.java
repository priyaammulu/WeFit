package wefit.com.wefit.datamodel;

import android.util.Log;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import wefit.com.wefit.pojo.User;


/**
 * Created by lorenzo on 10/28/17.
 */

public class FirebaseModel implements LoginModel {

    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public FirebaseModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void associateUser(User loggedUser) {
        Log.i("INFO", "set associated");
        user = loggedUser;
        Log.i("INFO", user.getAuthKey() + "\n" + user.getUserId());
    }

    @Override
    public boolean isAuth() {
        if (mUser == null)
            mUser = mAuth.getCurrentUser();
        return mUser != null;
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }
}
