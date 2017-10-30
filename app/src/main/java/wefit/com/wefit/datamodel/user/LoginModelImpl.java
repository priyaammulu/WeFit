package wefit.com.wefit.datamodel.user;

import android.content.Intent;
import android.view.View;

import java.util.Map;

import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public class LoginModelImpl implements LoginModel {

    @Override
    public boolean isAuth() {
        return false;
    }

    @Override
    public void configure(Map<LoginViewModel.Configuration, Object> configuration) {

    }

    @Override
    public void setHandlers(Map<LoginViewModel.Handlers, View> handlers) {

    }

    @Override
    public void passActivityResults(int requestCode, int resultCode, Intent data) {

    }


}
