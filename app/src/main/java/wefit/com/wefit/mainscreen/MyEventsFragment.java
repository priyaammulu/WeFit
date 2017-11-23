package wefit.com.wefit.mainscreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import wefit.com.wefit.MyEventsAdapter;
import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.MainViewModel;


public class MyEventsFragment extends Fragment {
    private FragmentsInteractionListener mActivity;
    private MainViewModel mMainViewModel;
    private Subscription mSubscription;
    private ListView mListView;
    private MyEventsAdapter myEventsAdapter;


    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);

        // download the user events and show them
        Flowable<List<Event>> stream = mMainViewModel.getUserEvents();
        stream.subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                initilizeListView(events);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = mActivity.getMainViewModel();

    }

    private void initilizeListView(List<Event> events) {
        myEventsAdapter = new MyEventsAdapter(events, getActivity());
        mListView.setAdapter(myEventsAdapter);
    }

    private void bind(View view) {
        mListView = (ListView) view.findViewById(R.id.myevents_listview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
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
        if (mSubscription != null)
            mSubscription.cancel();
    }
}
