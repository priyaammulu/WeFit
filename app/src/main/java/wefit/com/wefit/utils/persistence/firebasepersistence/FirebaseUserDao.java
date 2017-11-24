package wefit.com.wefit.utils.persistence.firebasepersistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.RemoteUserDao;

/**
 * Created by gioacchino on 13/11/2017.
 */

public class FirebaseUserDao implements RemoteUserDao {

    private DatabaseReference mUserStorage;

    @Override
    public Flowable<User> loadByID(String userID) {
        return Flowable.create(new UserLoaderProvider(userID), BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<Map<String, User>> loadByIDs(final List<String> userIds) {

        return Flowable.create(new FlowableOnSubscribe<Map<String, User>>() {
            @Override
            public void subscribe(final FlowableEmitter<Map<String, User>> flowableEmitter) throws Exception {

                final Map<String, User> retrievedUser = new HashMap<>();

                // make sure that the IDs are unique
                final Set<String> uniqueIDs = new HashSet<>(userIds);

                for (String userID : uniqueIDs) {

                    // retrieve the user from the system async
                    loadByID(userID).subscribe(new Consumer<User>() {
                        @Override
                        public void accept(User user) throws Exception {

                            retrievedUser.put(user.getId(), user);

                            // if the retrieved user number equals the size of the requested IDs
                            // then the load is complete
                            if (retrievedUser.size() == uniqueIDs.size()) {
                                flowableEmitter.onNext(retrievedUser);
                            }

                        }
                    });

                }

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void save(User userToStore) {

        // the user has always an ID
        String userID = userToStore.getId();

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
                    .orderByKey() // It's necessary to access the children
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
                            // required, but it cannot happen
                        }
                    });


        }

        public UserLoaderProvider(String userID) {
            this.userID = userID;
        }
    }
}
