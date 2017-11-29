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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.mainscreen.adapters.EventWallAdapter;
import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.newevent.NewEventActivity;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.viewmodels.EventViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentsInteractionListener} interface
 * to handle interaction events.
 */
public class EventWallFragment extends Fragment {

    /**
     * RxJava Observer Listener
     */
    private Subscription mEventRetrieveSubscription;
    private Subscription mOtherEventsSubscription;


    private EventWallAdapter mAdapter;
    private ListView mEventList;
    private EventViewModel mMainViewModel;
    private FragmentsInteractionListener mListener;
    private ProgressDialog popupDialogProgress;
    private LinearLayout mNoEventShow;

    private List<Event> retrievedEvents;


    public EventWallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        showWaitSpinner();

        fetchEvents();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = mListener.getEventViewModel();
        mListener.provideLocation();


    }


    private void setupTopbar(View layout) {


        layout.findViewById(R.id.new_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewEventActivity.class));
            }
        });


    }


    private void fetchEvents() {
        if (NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
            mMainViewModel
                    .getEvents()
                    .subscribe(new FlowableSubscriber<List<Event>>() {
                        @Override
                        public void onSubscribe(Subscription subscription) {
                            subscription.request(Long.MAX_VALUE);
                            mEventRetrieveSubscription = subscription;
                        }

                        @Override
                        public void onNext(List<Event> events) {
                            stopWaitSpinner();
                            handleAdapter(events);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            stopWaitSpinner();
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

    private void bind(View view) {
        this.setupTopbar(view);

        mNoEventShow = (LinearLayout) view.findViewById(R.id.no_available_events_label);

        mEventList = (ListView) view.findViewById(R.id.event_list);
        mEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // retrieve the event
                Event selected = mAdapter.getItem(i);

                // send the event ID
                Intent intent = new Intent(getActivity(), EventDescriptionActivity.class);
                intent.putExtra(ExtrasLabels.EVENT, selected.getId());
                startActivity(intent);
            }
        });

        view.findViewById(R.id.new_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), NewEventActivity.class));
            }
        });


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

            fetchEvents();


        }
    }


    private void handleAdapter(List<Event> events) {

        if (events.size() != 0) {

            retrievedEvents = events;

            mEventList.setVisibility(View.VISIBLE);
            mNoEventShow.setVisibility(View.GONE);

            if (mAdapter == null) {
                mAdapter = new EventWallAdapter(retrievedEvents, getActivity());
                mEventList.setAdapter(mAdapter);
            } else
                mAdapter.setEvents(retrievedEvents);
            mAdapter.notifyDataSetChanged();

            mEventList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    int visibleElementCount = firstVisibleItem + visibleItemCount;

                    if (visibleElementCount == totalItemCount && totalItemCount != 0) {


                        mMainViewModel.getNewEvents().subscribe(new FlowableSubscriber<List<Event>>() {
                            @Override
                            public void onSubscribe(Subscription subscription) {
                                subscription.request(1L);
                                if (mOtherEventsSubscription != null) {
                                    mOtherEventsSubscription.cancel();
                                }
                                mOtherEventsSubscription = subscription;
                            }

                            @Override
                            public void onNext(List<Event> events) {
                                // TODO not tested, I need more events!!!
                                if (events.size() != 0) {

                                    retrievedEvents.addAll(events);
                                    mAdapter.notifyDataSetChanged();

                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });


                    }

                }
            });

        } else {
            mEventList.setVisibility(View.GONE);
            mNoEventShow.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_wall, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentsInteractionListener) {
            mListener = (FragmentsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventRetrieveSubscription != null)
            mEventRetrieveSubscription.cancel();

        if (mOtherEventsSubscription != null) {
            mOtherEventsSubscription.cancel();
        }
    }

    private void showWaitSpinner() {
        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupDialogProgress = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    private void stopWaitSpinner() {
        if (this.popupDialogProgress != null) {
            popupDialogProgress.dismiss();
        }
    }

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

    private void showNoInternetConnectionPopup() {

        stopWaitSpinner();

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.no_internet_popup_label)
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
