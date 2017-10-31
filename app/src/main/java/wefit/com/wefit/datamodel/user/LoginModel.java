package wefit.com.wefit.datamodel.user;

import java.util.Observable;


/**
 * Created by lorenzo on 10/28/17.
 */

public interface LoginModel {

    void associateUser(String authKey,
                       String userID,
                       String gender,
                       String name,
                       String email);

    boolean isAuth();


}
