package wefit.com.wefit.datamodel;

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



}
