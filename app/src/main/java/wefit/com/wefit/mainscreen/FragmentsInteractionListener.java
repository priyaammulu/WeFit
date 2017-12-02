package wefit.com.wefit.mainscreen;

import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * ref: http://developer.android.com/training/basics/fragments/communicating.html"
 */
public interface FragmentsInteractionListener {
    /**
     * Simple Event ViewModel getter
     * @return Event ViewModel
     */
    EventViewModel getEventViewModel();

    /**
     * Simple User ViewModel getter
     * @return User ViewModel
     */
    UserViewModel getUserViewModel();
}
