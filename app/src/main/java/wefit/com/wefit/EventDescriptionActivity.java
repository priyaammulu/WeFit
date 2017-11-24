package wefit.com.wefit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

import static wefit.com.wefit.mainscreen.fragments.MainFragment.EVENT;

public class EventDescriptionActivity extends AppCompatActivity {

    ActionBar mActionBar;
    Event mEvent;

    // view components
    private ImageView mEventImage;
    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mEventPublishDate;

    // viewmodels
    UserViewModel mUserViewModel;
    EventViewModel mEventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // retrieve the view-models
        mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();
        mEventViewModel = ((WefitApplication) getApplication()).getEventViewModel();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        // bind the views to the variables
        this.bindViews();

        // retrieve the event from the intent
        String eventID = this.getIntent().getStringExtra(EVENT);

        Log.i("evento", eventID);

        // download the update event from the server
        mEventViewModel.getEvent(eventID).subscribe(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {
                //Log.i("evento", event.toString());
                fillActivity(event);
            }
        });

    }

    private void bindViews() {

        this.mEventImage = (ImageView) findViewById(R.id.event_main_img);
        this.mEventName = (TextView) findViewById(R.id.event_title_txt);
        this.mEventDescription = (TextView) findViewById(R.id.event_description_txt);
        this.mEventPublishDate = (TextView) findViewById(R.id.event_date_posted);

    }

    private void fillActivity(Event retrievedEvent) {

        this.mEventName.setText(retrievedEvent.getName());
        this.mEventImage.setImageBitmap(this.decodeBase64BitmapString(retrievedEvent.getImage()));
        this.mEventDescription.setText(retrievedEvent.getDescription());
        this.mEventPublishDate.setText(getDate(new Date(retrievedEvent.getPublicationDate())));

        // check if the user is the admin of the event
        if (retrievedEvent.getAdminID().equals(this.mUserViewModel.retrieveCachedUser().getId())) {
            Log.i("Admin", "si");
        }


    }

    private Bitmap decodeBase64BitmapString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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

    private String getMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }

    private String getTime(Date date) {
        Locale locale = Locale.ITALIAN;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }


}
