package wefit.com.wefit.newevent;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;


public class NewEventFragmentFirst extends Fragment {
    private NewFragmentListener mListener;
    private EditText mName;
    private NumberPicker mParticipants;
    private Button mButtonAhead;
    private DatePicker mDatePicker;

    public NewEventFragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void bind(View view) {
        mName = (EditText) view.findViewById(R.id.new_event_name);
        mParticipants = (NumberPicker) view.findViewById(R.id.new_event_participants);
        mParticipants.setMinValue(1);
        mParticipants.setMaxValue(15);
        mDatePicker = (DatePicker) view.findViewById(R.id.new_event_datepicker);
        mButtonAhead = (Button) view.findViewById(R.id.new_event_button_ahead);
        mButtonAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = new Event();
                event.setTitle(mName.getText().toString());
                event.setNumberParticipants(mParticipants.getValue());
                event.setExpire(getDateFromDatePicker(mDatePicker));
                mListener.secondFragment(event);
            }
        });
    }

    private Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event_fragment_first, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewFragmentListener) {
            mListener = (NewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
