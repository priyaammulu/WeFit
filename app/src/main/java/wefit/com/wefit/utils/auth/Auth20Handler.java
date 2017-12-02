package wefit.com.wefit.utils.auth;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 14/11/2017.
 * Auth2.0 service handler interface
 */
public interface Auth20Handler {

    /**
     * Check if the user is authenticated
     * @return true if authenticated
     */
    boolean isAuth();

    /**
     * Signout from the service
     */
    void signOut();

    /**
     * Retrieve the signed user from the Auth2.0 Service
     * @return
     */
    Flowable<User> retrieveUser();



}
