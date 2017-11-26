package wefit.com.wefit.newevent;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Calendar;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

import static android.app.Activity.RESULT_OK;


public class NewEventFragmentFirstPage extends Fragment {
    private static final int PLACE_PICKER_REQUEST = 1;
    private NewFragmentListener mListener;
    private EditText mEventName;
    private Button mButtonAhead;
    private LinearLayout mEventDatePicker;
    private LinearLayout mMap;
    private EventLocation mUserLocation;
    private EventLocation mRetrievedLocation;
    private Calendar calSelected = Calendar.getInstance();
    private SwitchCompat mSwitch;
    private LinearLayout mAttendeesLayout;
    private TextView mPublicPrivate;

    public NewEventFragmentFirstPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLocation = mListener.getUserLocation();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void bind(View view) {
        mAttendeesLayout = (LinearLayout) view.findViewById(R.id.new_event_one_attendees);
        mAttendeesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mEventName = (EditText) view.findViewById(R.id.new_event_name);
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calSelected.set(Calendar.YEAR, year);
                calSelected.set(Calendar.MONTH, monthOfYear);
                calSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }

        };
        mEventDatePicker = (LinearLayout) view.findViewById(R.id.new_event_datepicker);
        mEventDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getActivity(), dateListener, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mMap = (LinearLayout) view.findViewById(R.id.new_event_map);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO rivedere, comportamento strano qui
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                LatLng southest = new LatLng(mUserLocation.getLatitude() - 0.00000001, mUserLocation.getLongitude() - 0.00000001);
                LatLng northest = new LatLng(mUserLocation.getLatitude() + 0.00000001, mUserLocation.getLongitude() + 0.00000001);
                LatLngBounds bounds = new LatLngBounds(southest, northest);
                builder.setLatLngBounds(bounds);
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        mPublicPrivate = (TextView) view.findViewById(R.id.new_event_one_private_public);
        mSwitch = (SwitchCompat) view.findViewById(R.id.new_event_one_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPublicPrivate.setText("Private event");
                    mAttendeesLayout.setVisibility(View.GONE);
                }
                else {
                    mAttendeesLayout.setVisibility(View.VISIBLE);
                    mPublicPrivate.setText("Public event");
                }
            }
        });
        mButtonAhead = (Button) view.findViewById(R.id.new_event_button_ahead);
        mButtonAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Event event = new Event();
                event.setName(mEventName.getText().toString());

                //TODO rivedere il numberpicker !!!
                event.setMaxAttendees(3);

                event.setEventDate(calSelected.getTime().getTime());
                event.setEventLocation(mRetrievedLocation);

                mListener.secondFragment(event);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                // TODO rivedere perché deprecato
                Place place = PlacePicker.getPlace(data, getActivity());

                mRetrievedLocation = new EventLocation(place.getLatLng().latitude, place.getLatLng().longitude);
                mRetrievedLocation.setName(place.getAddress().toString());

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event_fragment_page_one, container, false);
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
