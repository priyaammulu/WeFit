package wefit.com.wefit.datamodel;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.auth.Auth20Handler;

/**
 * Created by gioacchino on 14/11/2017.
 */

public class UserModelAsyncImpl implements UserModel {

    private Auth20Handler loginHandler;


    @Override
    public Flowable<User> retrieveLoggedUser() {
        return loginHandler.retrieveUser();
    }

    @Override
    public boolean isAuth() {
        return loginHandler.isAuth();
    }

    @Override
    public void signOut() {
        loginHandler.signOut();
    }

    public UserModelAsyncImpl(Auth20Handler loginHandler) {
        this.loginHandler = loginHandler;
    }
}
