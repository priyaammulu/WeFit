package wefit.com.wefit.utils.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface RemoteUserDao {

    /**
     * Load a user from the store. This operation may be async.
     * @param userID Id of the user in the store
     * @return This will throw an event when the user is loaded
     */
    Flowable<User> loadByID(String userID);

    /**
     * Load a set of users from the store. This operation may be async.
     * @param userIds IDs of the users
     * @return This will throw an event when the users are loaded
     */
    Flowable<Map<String, User>> loadByIDs(List<String> userIds);

    /**
     * Store a user in the persistent store
     * @param userToStore Save this user in the persistent storage
     */
    void save(User userToStore);




}
