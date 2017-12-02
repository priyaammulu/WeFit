package wefit.com.wefit.newevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;

import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * Created by lorenzo on 10/28/17.
 * This activity allows the user to create a new event
 */
public class NewEventActivity extends AppCompatActivity implements NewFragmentListener {
    /**
     * Fragments
     */
    private NewEventFragmentFirstPage fragmentFirst = new NewEventFragmentFirstPage();
    private NewEventFragmentSecondPage fragmentSecond = new NewEventFragmentSecondPage();
    private NewEventFragmentThirdPage fragmentThird = new NewEventFragmentThirdPage();
    /**
     * Event being created
     */
    private Event newEvent;
    /**
     * Event View Model
     */
    private EventViewModel mainViewModel;
    /**
     * User View Model
     */
    private UserViewModel userViewModel;
    /**
     * Stack that manages the back button
     */
    private LinkedList<Fragment> stack = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        mainViewModel = ((WefitApplication) getApplication()).getEventViewModel();
        userViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.new_event_fragment, fragmentFirst)
                .add(R.id.new_event_fragment, fragmentSecond)
                .add(R.id.new_event_fragment, fragmentThird)
                .hide(fragmentSecond)
                .hide(fragmentThird)
                .commit();
        stack.push(fragmentFirst);
    }
    /**
     * It shows a new Fragment
     */
    private void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentFirst)
                .hide(fragmentSecond)
                .hide(fragmentThird)
                .show(fragment)
                .commit();
        stack.push(fragment);
    }

    @Override
    public void onBackPressed() {
        if (stack.isEmpty() || stack.pop() instanceof NewEventFragmentFirstPage)
            super.onBackPressed();
        else {
            attachFragment(stack.pop());
        }
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

        // go to the main wall
        startActivity(new Intent(this, MainActivity.class));
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
