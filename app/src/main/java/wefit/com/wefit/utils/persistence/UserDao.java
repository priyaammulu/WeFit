package wefit.com.wefit.utils.persistence;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface UserDao {

    /**
     * Save a user in the store. This operation is async.
     * @param userID Id of the user in the store
     * @return This will throw an event when the user is loaded
     */
    Flowable<User> loadByID(String userID);

    /**
     * Store a user in the persistent store
     * @param userToStore Save this user in the persistent storage
     */
    void save(User userToStore);




}
