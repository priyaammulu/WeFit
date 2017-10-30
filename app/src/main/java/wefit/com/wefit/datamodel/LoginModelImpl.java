package wefit.com.wefit.datamodel;

import android.os.Bundle;
import android.view.View;

import java.util.List;
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


}
