package wefit.com.wefit.mainscreen.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.newevent.NewEventActivity;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.mainscreen.adapters.AttendancesEventAdapter;
import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentsInteractionListener} interface
 * to handle interaction events.
 */
public class ScheduledEventsFragment extends Fragment {

    /**
     * RxJava subscription
     */
    private Subscription eventRetrieveSubscription;

    /**
     * Reference to the Activity
     */
    private FragmentsInteractionListener mActivity;
        /**
         * Event View Model
         */
    private EventViewModel mEventViewModel;
    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * List view
     */
    private ListView mListView;
    /**
     * LinearLayout
     */
    private LinearLayout mNoEventsLabel;
    /**
     * Dialog showing the activity progress
     */
    private ProgressDialog popupDialogProgress;


    public ScheduledEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindLayoutComponents(view);
        initializeListView(new ArrayList<Event>());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventViewModel = mActivity.getEventViewModel();
        mUserViewModel = mActivity.getUserViewModel();

    }
    /**
     * It initializes the List View with events
     */
    private void initializeListView(final List<Event> events) {
        AttendancesEventAdapter attendancesEventAdapter = new AttendancesEventAdapter(events, getActivity(), mUserViewModel.retrieveCachedUser());
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

    /**
     * It binds UI layout to properties
     */
    private void bindLayoutComponents(View view) {
        setupTopbar(view);
        this.mListView = (ListView) view.findViewById(R.id.myevents_listview);
        this.mNoEventsLabel = (LinearLayout) view.findViewById(R.id.baggar_all_events);
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
            // load refreshed data
            showWaitSpinner();

            if (NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
                // retrieve events from the server and from the local storage
                mEventViewModel.getUserEvents().subscribe(new FlowableSubscriber<List<Event>>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(1L);

                        // if there are some old listeners, remove them
                        if (eventRetrieveSubscription != null) {
                            eventRetrieveSubscription.cancel();
                        }

                        eventRetrieveSubscription = subscription;
                    }

                    @Override
                    public void onNext(List<Event> events) {

                        stopWaitSpinner();

                        if (events.size() != 0) {
                            initializeListView(events);
                        } else { // if the user has no events hide the list
                            mListView.setVisibility(View.GONE);
                            mNoEventsLabel.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onError(Throwable throwable) {

                        showRetrieveErrorPopupDialog();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                showNoInternetConnectionPopup();
            }

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

        if (eventRetrieveSubscription != null) {
            eventRetrieveSubscription.cancel();
        }

    }

    /**
     * It shows the wait spinner
     */
    private void showWaitSpinner() {
        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupDialogProgress = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }
    /**
     * It stops the wait spinner
     */
    private void stopWaitSpinner() {
        if (this.popupDialogProgress != null) {
            popupDialogProgress.dismiss();
        }
    }
    /**
     * It shows a retrieve error popup dialog
     */
    private void showRetrieveErrorPopupDialog() {

        stopWaitSpinner();

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // , go to the main activity
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * It setups the toolbar
     */
    private void setupTopbar(View layout) {
        layout.findViewById(R.id.myevent_new_event_attendances_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewEventActivity.class));
            }
        });
    }
    /**
     * It shows a no internet connection popup dialog
     */
    private void showNoInternetConnectionPopup() {
        stopWaitSpinner();
        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go to the main activity
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
