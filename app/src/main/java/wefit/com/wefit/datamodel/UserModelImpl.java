package wefit.com.wefit.datamodel;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.UserDao;


/**
 * Created by lorenzo on 10/28/17.
 */

public class UserModelImpl implements UserModel {

    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private UserDao userDao;

    public UserModelImpl(Context currentContext, UserDao userPersistence) {

        // firebase initialization
        FirebaseApp.initializeApp(currentContext);
        mAuth = FirebaseAuth.getInstance();

        this.userDao = userPersistence;

    }

    @Override
    public Flowable<User> retrieveLoggedUser() {
        return null;
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
