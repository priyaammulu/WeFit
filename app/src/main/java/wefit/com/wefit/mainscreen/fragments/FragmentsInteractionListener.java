package wefit.com.wefit.mainscreen.fragments;

import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface FragmentsInteractionListener {
    /**
     * It returns an Event view model
     */
    EventViewModel getEventViewModel();
    /**
     * It returns an User view model
     */
    UserViewModel getUserViewModel();
}
