package wefit.com.wefit.utils.auth;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.RemoteUserDao;

/**
 * Created by gioacchino on 14/11/2017.
 */

public class Auth20FirebaseHandlerImpl implements Auth20Handler {

    private FirebaseAuth mFirebaseAuth;
    private RemoteUserDao mUserDao;

    @Override
    public void signIn() {
        // nothing, managed by the view
        // TODO maybe we should modify this
    }

    @Override
    public boolean isAuth() {

        return mFirebaseAuth.getCurrentUser() != null;// && mFirebaseAuth.getCurrentUser().isEmailVerified();

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

                Log.i("PROVA", "sono qui");
                // retrieve the user id
                final String userId = firebaseUser.getUid();

                Log.i("PROVA", "user id e: " + userId);

                // todo check if user exists in the system, if not create it

                mUserDao.loadByID(userId).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User loggedUser) throws Exception {

                        if (loggedUser.getId() == null) {


                            firebaseUser.sendEmailVerification();

                            loggedUser.setId(firebaseUser.getUid());
                            loggedUser.setFullName(firebaseUser.getDisplayName());
                            loggedUser.setEmail(getContact());

                            mUserDao.save(loggedUser);

                        }
                        Log.i("PROVA2", "sono qui");

                        flowableEmitter.onNext(loggedUser);

                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Auth20FirebaseHandlerImpl(FirebaseAuth mFirebaseAuth, RemoteUserDao mUserDao) {
        this.mFirebaseAuth = mFirebaseAuth;
        this.mUserDao = mUserDao;
    }

    private String getContact() {

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        // the user is logged with an email or a phone number
        String contact = firebaseUser.getEmail();
        if (contact == null) {
            contact = firebaseUser.getPhoneNumber();
        }

        return contact;


    }
}
