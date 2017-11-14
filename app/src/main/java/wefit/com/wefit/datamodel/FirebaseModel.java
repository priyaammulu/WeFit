package wefit.com.wefit.datamodel;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
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

    public FirebaseModel(Context currentContext) {

        // firebase initialization
        FirebaseApp.initializeApp(currentContext);
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
        return getUser() != null;
    }

    @Override
    public void signOut() {
        mAuth.signOut();
    }

    @Override
    public FirebaseUser getUser() {
        if (mUser == null)
            mUser = mAuth.getCurrentUser();
        return mUser;
    }
}
