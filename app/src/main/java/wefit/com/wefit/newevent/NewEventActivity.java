package wefit.com.wefit.newevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

public class NewEventActivity extends AppCompatActivity implements NewFragmentListener {
    private NewEventFragmentFirstPage fragmentFirst = new NewEventFragmentFirstPage();
    private NewEventFragmentSecondPage fragmentSecond = new NewEventFragmentSecondPage();
    private NewEventFragmentThirdPage fragmentThird = new NewEventFragmentThirdPage();
    private Event newEvent;
    private EventViewModel mainViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        mainViewModel = ((WefitApplication) getApplication()).getEventViewModel();
        userViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        bind();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.new_event_fragment, fragmentFirst)
                .commit();
    }

    private void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.new_event_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void bind() {

    }

    @Override
    public void secondFragment(Event event) {
        newEvent = event;
        attachFragment(fragmentSecond);
    }

    @Override
    public Event getEvent() {
        return newEvent;
    }

    @Override
    public void finish(Event newEvent) {
        this.newEvent = newEvent;

        mainViewModel.createNewEvent(newEvent);

        finish();
    }

    @Override
    public User getEventCreator() {
        return userViewModel.retrieveCachedUser();
    }

    @Override
    public EventLocation getUserLocation() {
        return mainViewModel.getUserLocation();
    }

    @Override
    public void thirdFragment(Event event) {
        this.newEvent = event;
        attachFragment(fragmentThird);
    }
}
