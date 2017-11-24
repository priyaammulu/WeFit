package wefit.com.wefit;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

import static wefit.com.wefit.mainscreen.fragments.MainFragment.EVENT;

public class EventDescriptionActivity extends AppCompatActivity {

    ActionBar mActionBar;
    Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // retrieve the view-models
        UserViewModel userViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        EventViewModel eventViewModel = ((WefitApplication) getApplication()).getEventViewModel();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        // retrieve the event from the intent
        String eventID = this.getIntent().getStringExtra(EVENT);

        Log.i("evento", eventID);

        eventViewModel.getEvent(eventID).subscribe(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {
                Log.i("evento", event.toString());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
