package wefit.com.wefit.datamodel;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 * This interface provides operations that can be done on User
 */
public interface UserModel {
    /**
     * Retrived the user currently logged
     * @return User the user logged
     */
    Flowable<User> retrieveLoggedUser();

    /**
     * Returns true if user is currently logged, false otherwise
     */
    boolean isAuth();

    /**
     * It signs out
     */
    void signOut();

    /**
     * Returns the User stored locally
     * @return User the user selected
     */
    User getLocalUser();

    /**
     * Updates the current user
     */
    void updateUser(User userToStore);

    /**
     * Returns a User having the userID passed
     * @return User the user selected
     */
    Flowable<User> retrieveUserByID(String userID);
}
