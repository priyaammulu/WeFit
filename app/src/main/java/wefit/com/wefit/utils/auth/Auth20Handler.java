package wefit.com.wefit.utils.auth;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 14/11/2017.
 */

public interface Auth20Handler {

    void signIn();
    boolean isAuth();
    void signOut();
    Flowable<User> retrieveUser();



}
