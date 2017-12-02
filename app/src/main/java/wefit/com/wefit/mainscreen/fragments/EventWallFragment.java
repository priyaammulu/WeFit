package wefit.com.wefit.mainscreen.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.mainscreen.adapters.EventWallAdapter;
import wefit.com.wefit.newevent.NewEventActivity;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.viewmodels.EventViewModel;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentsInteractionListener} interface
 * to handle interaction events.
 */
public class EventWallFragment extends Fragment {

    /**
     * RxJava Subscription
     */
    private Subscription mEventRetrieveSubscription;
    /**
     * RxJava Subscription
     */
    private Subscription mOtherEventsSubscription;
    /**
     * Location permission constant
     */
    private static final int LOCATION_PERMISSION = 1;
    /**
     * Request check settings
     */
    public static final int REQUEST_CHECK_SETTINGS = 2;
    /**
     * Event Wall Adapter
     */
    private EventWallAdapter mAdapter;
    /**
     * Listview with events
     */
    private ListView mEventList;
    /**
     * Main View Model
     */
    private EventViewModel mMainViewModel;
    /**
     * Activity listener
     */
    private FragmentsInteractionListener mListener;
    /**
     * Popup to show progresses
     */
    private ProgressDialog popupDialogProgress;
    /**
     * LinearLayout for no events
     */
    private LinearLayout mNoEventShow;
    /**
     * retrieved events
     */
    private List<Event> retrievedEvents;


    public EventWallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = mListener.getEventViewModel();
        provideLocation();
    }
    /**
     * Checks for location permissions and retrieves it
     */
    public void provideLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        } else {
            enableGoogleApiClient();
        }
    }

    /**
     * Asks the user to enable the GPS
     */
    @SuppressLint("MissingPermission")
    public void enableGoogleApiClient() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            EventLocation loc = new EventLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mMainViewModel.setLocation(loc);
                            fetchEvents();
                        }
                    }


                };
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null /* Looper */);
            }
        });
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableGoogleApiClient();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.location_permission_notallowed_toast, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
    /**
     * Setups the toolbar
     */
    private void setupTopbar(View layout) {
        layout.findViewById(R.id.new_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewEventActivity.class));
            }
        });
    }

    /**
     * It fetches events from the store
     */
    public void fetchEvents() {
        if (NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
            showWaitSpinner();
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

    /**
     * Binds UI views to fields
     */
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

    /**
     * Manages the events adapter to show the events retrieved
     */
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
     * It shows a Dialog containing an error to the user
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
     * It shows no internet connection popup
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
