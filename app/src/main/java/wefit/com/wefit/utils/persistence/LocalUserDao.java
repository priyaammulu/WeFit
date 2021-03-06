package wefit.com.wefit.utils.persistence;

import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 14/11/2017.
 * Ths class follows the DAO (Data Access Object) pattern.
 * It allows his client to perform operations on Users stored locally
 */

public interface LocalUserDao {

    /**
     * Save a user in the local data store
     * @param userToSave user to be saved
     */
    void save(User userToSave);

    /**
     * Load the currently saved user
     * @return local stored user
     */
    User load();

    /**
     * Delete all local user infos
     */
    void wipe();

}
