package wefit.com.wefit.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.EventAdapter;
import wefit.com.wefit.EventDescriptionActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.newevent.NewEventActivity;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.MainViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentsInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {
    public static final String EVENT = "selected";
    private EventAdapter mAdapter;
    private ListView mEventList;
    private MainViewModel mMainViewModel;
    private FragmentsInteractionListener mListener;
    // this should be handled by another class
    private Subscription mSubscription;

    private TextView middleTopBottom;



    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        fetchEvents();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = mListener.getMainViewModel();
        mListener.provideLocation();
        //mListener.fillInIconWithLogo(R.drawable.ic_edit, R.drawable.wefitlogo_extended, R.drawable.ic_search);

    }

    private void fetchEvents() {
        Log.i("PROMISE creation main", "creazione promessa");
        Flowable<List<Event>> stream = mMainViewModel.getEvents();
        stream.subscribe(new FlowableSubscriber<List<Event>>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
                mSubscription = subscription;
            }

            @Override
            public void onNext(List<Event> events) {
                //handleAdapter(events);
                Log.i("PROMISE GETEVENT main", events.toString());
                Log.i("PROMISE RESPEcet main", "respected");
                handleAdapter(events);
            }

            @Override
            public void onError(Throwable throwable) {
                handleError(throwable);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void bind(View view) {
        mEventList = (ListView) view.findViewById(R.id.event_list);
        mEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), EventDescriptionActivity.class);
                Event selected = mAdapter.getItem(i);
                intent.putExtra(EVENT, selected);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.new_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), NewEventActivity.class));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){

            //mListener.fillInIconWithLogo(R.drawable.ic_edit, R.drawable.wefitlogo_extended, R.drawable.ic_search);

        }
    }


    private void handleError(Throwable error) {
        //todo implement
    }

    private void handleAdapter(List<Event> events) {
        if (mAdapter == null) {
            mAdapter = new EventAdapter(events, getActivity());
            mEventList.setAdapter(mAdapter);
        } else
            mAdapter.setEvents(events);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
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
        if (mSubscription != null)
            mSubscription.cancel();
    }

}
