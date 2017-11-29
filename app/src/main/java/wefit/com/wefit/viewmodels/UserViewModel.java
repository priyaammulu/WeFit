package wefit.com.wefit.viewmodels;


import io.reactivex.Flowable;
import wefit.com.wefit.datamodel.UserModel;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 10/28/17.
 */

public class UserViewModel {

    private UserModel mUserModel;

    public UserViewModel(UserModel LoginModel) {
        this.mUserModel = LoginModel;
    }

    public Flowable<User> retrieveUserFromRemoteStore() {
        return mUserModel.retrieveLoggedUser();
    }

    public User retrieveCachedUser() {
        return mUserModel.getLocalUser();
    }

    public boolean isAuth() {
        return mUserModel.isAuth();
    }

    public void signOut() {
        mUserModel.signOut();

    }

    public void updateUser(User userToSave) {
        mUserModel.updateUser(userToSave);
    }

    public Flowable<User> retrieveUserByID(String userID) {
        return mUserModel.retrieveUserByID(userID);
    }

}
