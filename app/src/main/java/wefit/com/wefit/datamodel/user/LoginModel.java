package wefit.com.wefit.datamodel.user;

import java.util.Observable;


/**
 * Created by lorenzo on 10/28/17.
 */

public abstract class LoginModel extends Observable {

    public abstract void associateUser(String authKey, String userID);

    public abstract boolean isAuth();

    // void configure(Map<LoginViewModel.Configuration, Object> configuration);

    // void setHandlers(Map<LoginViewModel.Handlers, View> handlers);

    // void passActivityResults(int requestCode, int resultCode, Intent data);



}
