package wefit.com.wefit.datamodel;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.auth.Auth20Handler;
import wefit.com.wefit.utils.persistence.LocalUserDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;

/**
 * Created by gioacchino on 14/11/2017.
 */

public class UserModelAsyncImpl implements UserModel {

    private final LocalUserDao localUserDao;
    private final Auth20Handler loginHandler;
    private final RemoteUserDao remoteUserStore;

    @Override
    public Flowable<User> retrieveLoggedUser() {

        Flowable<User> flow =  loginHandler.retrieveUser();

        // memorize locally the user to serve efficiently the future requests
        flow.subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {

                localUserDao.save(user);

            }
        });

        return flow;
    }

    @Override
    public boolean isAuth() {
        return loginHandler.isAuth();
    }

    @Override
    public void signOut() {
        loginHandler.signOut();
    }

    @Override
    public User getLocalUser() {
        return localUserDao.load();
    }

    @Override
    public void updateUser(User userToStore) {

        // update local cache
        localUserDao.save(userToStore);

        // update remotely
        remoteUserStore.save(userToStore);

    }

    public UserModelAsyncImpl(Auth20Handler loginHandler, LocalUserDao localUserDao, RemoteUserDao remoteUserDao) {
        this.loginHandler = loginHandler;
        this.localUserDao = localUserDao;
        this.remoteUserStore = remoteUserDao;
    }
}
