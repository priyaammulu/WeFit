package wefit.com.wefit.utils.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.UserDao;

/**
 * Created by gioacchino on 14/11/2017.
 */

public class Auth20FirebaseHandlerImpl implements Auth20Handler {

    private FirebaseAuth mFirebaseAuth;
    private UserDao mUserDao;

    @Override
    public void signIn() {
        // nothing, managed by the view
        // TODO maybe we should modify this
    }

    @Override
    public boolean isAuth() {

        return mFirebaseAuth.getCurrentUser() != null;

    }

    @Override
    public void signOut() {
        mFirebaseAuth.signOut();
    }

    @Override
    public Flowable<User> retrieveUser() {
        return Flowable.create(new FlowableOnSubscribe<User>() {
            @Override
            public void subscribe(final FlowableEmitter<User> flowableEmitter) throws Exception {
                final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                // retrieve the user id
                final String userId = getUserID();

                // todo check if user exists in the system, if not create it

                mUserDao.loadByID(userId).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User loggedUser) throws Exception {

                        if (loggedUser.getUserId() == null) {
                            loggedUser.setUserId(userId);
                            loggedUser.setName(firebaseUser.getDisplayName());
                            loggedUser.setContact(userId);

                            mUserDao.save(loggedUser);

                        }

                        flowableEmitter.onNext(loggedUser);

                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Auth20FirebaseHandlerImpl(FirebaseAuth mFirebaseAuth, UserDao mUserDao) {
        this.mFirebaseAuth = mFirebaseAuth;
        this.mUserDao = mUserDao;
    }

    private String getUserID() {

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        // the user is logged with an email or a phone number
        String userID = firebaseUser.getEmail();
        if (userID == null) {
            userID = firebaseUser.getPhoneNumber();
        }

        return userID;



    }
}
