package wefit.com.wefit.newevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Calendar;
import java.util.Date;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.viewmodels.MainViewModel;

import static android.app.Activity.RESULT_OK;


public class NewEventFragmentFirst extends Fragment {
    private static final int PLACE_PICKER_REQUEST = 1;
    private NewFragmentListener mListener;
    private EditText mName;
    private NumberPicker mParticipants;
    private Button mButtonAhead;
    private DatePicker mDatePicker;
    private ImageButton mImageButton;
    private Location location;

    public NewEventFragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = mListener.getUserLocation();
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
        mImageButton = (ImageButton) view.findViewById(R.id.new_event_map);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                LatLng southest = new LatLng(location.getLatitude() - 0.000001, location.getLongitude() - 0.000001);
                LatLng northest = new LatLng(location.getLatitude() + 0.000001, location.getLongitude() + 0.000001);
                LatLngBounds bounds = new LatLngBounds(southest, northest);
                //builder.setLatLngBounds(bounds);
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        mButtonAhead = (Button) view.findViewById(R.id.new_event_button_ahead);
        mButtonAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = new Event();
                event.setName(mName.getText().toString());
                //event.setMaxAttendee(mParticipants.getValue());
                //event.setEventDate(getDateFromDatePicker(mDatePicker));
                mListener.secondFragment(event);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
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
