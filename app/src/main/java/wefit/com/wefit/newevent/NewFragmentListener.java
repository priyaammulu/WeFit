package wefit.com.wefit.newevent;


import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface NewFragmentListener {
    void secondFragment(Event event);

    Event getEvent();

    void finish(Event newEvent);

    User getEventCreator();

    EventLocation getUserLocation();

    void thirdFragment(Event event);
}
