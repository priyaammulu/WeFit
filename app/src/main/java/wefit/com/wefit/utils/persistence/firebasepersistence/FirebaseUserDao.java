package wefit.com.wefit.utils.persistence.firebasepersistence;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.NoSuchElementException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.UserDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class FirebaseUserDao implements UserDao {

    private DatabaseReference mUserStorage;

    @Override
    public Flowable<User> loadByID(String userID) {
        return Flowable.create(new UserLoaderProvider(userID), BackpressureStrategy.BUFFER);
    }

    @Override
    public void save(User userToStore) {

        // the user has always an ID
        String userID = userToStore.getUserId();

        // save the event in the store
        userID = this.mUserStorage.child(userID).getKey();

        // save in the db
        this.mUserStorage.child(userID).setValue(userToStore);

    }

    public FirebaseUserDao(FirebaseDatabase firebaseDatabase, String userStoreName) {

        // access to the remote user store
        this.mUserStorage = firebaseDatabase.getReference(userStoreName);

    }

    private class UserLoaderProvider implements FlowableOnSubscribe<User> {

        private String userID;

        @Override
        public void subscribe(final FlowableEmitter<User> flowableEmitter) throws Exception {

            // reitrieve the user by ID
            mUserStorage
//                    .orderByKey()
                    .equalTo(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User retrieved;
                            try {
                                // there is only one value
                                DataSnapshot userWrapper = dataSnapshot.getChildren().iterator().next();
                                retrieved = userWrapper.getValue(User.class);
                            } catch (NoSuchElementException e) {
                                retrieved = new User();
                            }

                            // send event user retrieved
                            flowableEmitter.onNext(retrieved);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // TODO what to do here?
                        }
                    });


        }

        public UserLoaderProvider(String userID) {
            this.userID = userID;
        }
    }
}
