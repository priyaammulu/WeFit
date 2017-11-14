package wefit.com.wefit.datamodel;

import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 */

public interface UserModel {

    void associateUser(User loggedUser);

    boolean isAuth();

    void signOut();
}
