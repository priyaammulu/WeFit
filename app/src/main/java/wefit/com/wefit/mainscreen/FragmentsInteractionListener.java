package wefit.com.wefit.mainscreen;

import wefit.com.wefit.viewmodels.MainViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface FragmentsInteractionListener {
    MainViewModel getMainViewModel();
    void provideLocation();
    void fillInIcons(int IconLeft, String iconMiddle, int iconRight);
    void fillInIconWithLogo(int IconLeft, int logo, int iconRight);

    UserViewModel getLoginViewModel();

}
