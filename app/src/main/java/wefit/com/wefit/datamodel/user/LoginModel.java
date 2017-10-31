package wefit.com.wefit.datamodel.user;


/**
 * Created by lorenzo on 10/28/17.
 */

public interface LoginModel {

    void associateUser(UserModel loggedUser);

    boolean isAuth();


}
