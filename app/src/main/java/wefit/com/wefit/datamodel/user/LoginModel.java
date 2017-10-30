package wefit.com.wefit.datamodel.user;

import android.content.Intent;
import android.view.View;

import java.util.Map;

import wefit.com.wefit.viewmodels.LoginViewModel;

/**
 * Created by lorenzo on 10/28/17.
 */

public interface LoginModel {
    boolean isAuth();

    void configure(Map<LoginViewModel.Configuration, Object> configuration);

    void setHandlers(Map<LoginViewModel.Handlers, View> handlers);

    void passActivityResults(int requestCode, int resultCode, Intent data);



}
