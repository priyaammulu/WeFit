package wefit.com.wefit.datamodel;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 */

public interface UserModel {

    Flowable<User> retrieveLoggedUser();

    boolean isAuth();

    void signOut();
}
