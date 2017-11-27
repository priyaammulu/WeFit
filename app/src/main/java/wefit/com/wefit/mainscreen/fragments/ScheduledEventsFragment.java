package wefit.com.wefit.mainscreen.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.mainscreen.adapters.AttendancesEventAdapter;
import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;


public class ScheduledEventsFragment extends Fragment {

    /**
     * RxJava observer subscriptions
     */
    private Subscription eventRetrieveSubscription;

    private FragmentsInteractionListener mActivity;
    private EventViewModel mEventViewModel;

    private ListView mListView;
    private AttendancesEventAdapter attendancesEventAdapter;
    private UserViewModel mUserViewModel;


    public ScheduledEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        bind(view);

        // download the user events and show them
        mEventViewModel
                .getUserEvents()
                .subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(List<Event> events) throws Exception {


                        if (events.size() != 0) { // if the use has some joined events!!!
                            initializeListView(events);
                        }

                    }
                });
        //}

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventViewModel = mActivity.getEventViewModel();
        mUserViewModel = mActivity.getUserViewModel();

    }

    private void initializeListView(final List<Event> events) {
        attendancesEventAdapter = new AttendancesEventAdapter(events, getActivity(), mUserViewModel.retrieveCachedUser());
        mListView.setAdapter(attendancesEventAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // retrieve the event
                Event selected = events.get(position);

                // send the event ID
                Intent intent = new Intent(getActivity(), EventDescriptionActivity.class);
                intent.putExtra(ExtrasLabels.EVENT, selected.getId());

                if (selected.isPrivateEvent()) { // only private events do not need
                    intent.putExtra(ExtrasLabels.IS_PRIVATE, true);
                }

                startActivity(intent);

            }
        });
    }

    private void bind(View view) {
        mListView = (ListView) view.findViewById(R.id.myevents_listview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joined_events, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mActivity.fillInIcons(R.drawable.ic_edit, "MyEvents", R.drawable.ic_search);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentsInteractionListener) {
            mActivity = (FragmentsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventRetrieveSubscription != null)
            eventRetrieveSubscription.cancel();
    }
}
