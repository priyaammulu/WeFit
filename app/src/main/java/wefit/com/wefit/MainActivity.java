package wefit.com.wefit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.LoginViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private Button mSignOut;
    private LoginViewModel mLoginViewModel;
    private MainViewModel mMainViewModel;
    private EventAdapter mAdapter;
    private ListView mEventList;

    private Button tmpEventDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();
        mMainViewModel = ((WefitApplication) getApplication()).getMainViewModel();
        setContentView(R.layout.activity_main);
        mEventList = (ListView) findViewById(R.id.event_list);
        mSignOut = (Button) findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginViewModel.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        Flowable<List<Event>> stream = mMainViewModel.getEvents();
        stream.subscribe(
                this::handleAdapter,
                this::handleError);


        // todo rimuovere
        tmpEventDescription = (Button) findViewById(R.id.tmp_btn);
        tmpEventDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EventDescriptionActivity.class));
            }
        });
    }

    private void handleError(Throwable error) {

    }

    private void handleAdapter(List<Event> events) {
        if (mAdapter == null) {
            mAdapter = new EventAdapter(events, this);
            mEventList.setAdapter(mAdapter);
        }
        else
            mAdapter.setEvents(events);
        mAdapter.notifyDataSetChanged();
    }
}
