package wefit.com.wefit.newevent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;
import java.util.Date;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;
import wefit.com.wefit.utils.calendar.CalendarFormatter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lorenzo on 10/28/17.
 * This Fragment is the first step in the creation of an Event flow
 */
public class NewEventFragmentFirstPage extends Fragment {
    /**
     * Place picker constant
     */
    private static final int PLACE_PICKER_REQUEST = 1;
    /**
     * Activity reference
     */
    private NewFragmentListener mListener;
    /**
     * Layout
     */
    private EditText mEventName;
    private TextView mRetrievedPositionLabel;
    private TextView mAttendeesNumberLabel;
    private TextView mEventDateLabel;
    private LinearLayout mAttendeesLayout;
    private TextView mPublicPrivate;

    /**
     * Location retrieved
     */
    private EventLocation mRetrievedLocation;
    /**
     * Date selected
     */
    private Calendar calSelected = Calendar.getInstance();
    /**
     * Date in millis
     */
    private long dateMillis;
    /**
     * the number of attendees
     */
    private int numberAttendees = 2; // the minimum is always 2

    /**
     * True if the event is private
     */
    private boolean isEventPrivate = false;

    /**
     * To check if all the fields are filled
     */
    private boolean nameSelected = false;
    private boolean dateSelected = false;
    private boolean positionSelected = false;

    public NewEventFragmentFirstPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventLocation mUserLocation = mListener.getUserLocation();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Binds UI views to fields
     */
    private void bind(final View view) {
        ImageView mBackButton = (ImageView) view.findViewById(R.id.new_event_backbutton);
        mAttendeesLayout = (LinearLayout) view.findViewById(R.id.new_event_one_attendees);
        mEventName = (EditText) view.findViewById(R.id.new_event_name);
        LinearLayout mEventDatePicker = (LinearLayout) view.findViewById(R.id.new_event_datepicker);
        LinearLayout mMap = (LinearLayout) view.findViewById(R.id.new_event_map);
        mPublicPrivate = (TextView) view.findViewById(R.id.new_event_one_private_public);
        SwitchCompat mSwitch = (SwitchCompat) view.findViewById(R.id.new_event_one_switch);
        Button mButtonAhead = (Button) view.findViewById(R.id.new_event_button_ahead);
        SeekBar mAttendeesSeekbar = (SeekBar) view.findViewById(R.id.number_of_attendees_picker);
        mAttendeesNumberLabel = (TextView) view.findViewById(R.id.number_of_attendees_show);
        mEventDateLabel = (TextView) view.findViewById(R.id.new_event_date_label);
        mRetrievedPositionLabel = (TextView) view.findViewById(R.id.position_label);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameSelected = mEventName.getText().length() > 0;
            }
        });


        mEventDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // you can create a event starting from tomorrow
                final long DAY_MILLIS = 86400000;

                Calendar cal = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          final int dayOfMonth) {

                        // set day
                        calSelected.set(Calendar.YEAR, year);
                        calSelected.set(Calendar.MONTH, monthOfYear);
                        calSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                calSelected.set(Calendar.HOUR, hourOfDay);
                                calSelected.set(Calendar.MINUTE, minute);
                                dateMillis = calSelected.getTimeInMillis();

                                String dateFormatted = CalendarFormatter.getDate(dateMillis) + " " + CalendarFormatter.getTime(dateMillis);
                                mEventDateLabel.setText(dateFormatted);

                                dateSelected = true;

                            }
                        }, 0, 0, false).show();

                    }

                };


                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), dateListener, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                pickerDialog.getDatePicker().setMinDate(new Date().getTime() + DAY_MILLIS);
                pickerDialog.show();
            }
        });

        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // it will be set on the user current position
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                // Gioacchino's note for the group: this is legacy code
                //LatLng southest = new LatLng(mUserLocation.getLatitude() - 0.00000001, mUserLocation.getLongitude() - 0.00000001);
                //LatLng northest = new LatLng(mUserLocation.getLatitude() + 0.00000001, mUserLocation.getLongitude() + 0.00000001);
                //LatLngBounds bounds = new LatLngBounds(southest, northest);
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

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEventPrivate = true;
                    mPublicPrivate.setText(R.string.private_event_label);
                    mAttendeesLayout.setVisibility(View.GONE);
                } else {
                    isEventPrivate = false;
                    mAttendeesLayout.setVisibility(View.VISIBLE);
                    mPublicPrivate.setText(R.string.public_event_label);
                }
            }
        });


        mAttendeesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int minValue = numberAttendees;
            private int foundProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                foundProgress = progress + minValue;
                if (foundProgress < minValue) {
                    foundProgress = minValue;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String label = String.valueOf(foundProgress);
                mAttendeesNumberLabel.setText(label);
                numberAttendees = foundProgress;
            }
        });

        mButtonAhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFormFilled()) {
                    Event event = new Event();
                    event.setPublicationDate(new Date().getTime());
                    event.setName(mEventName.getText().toString());

                    // if the event IS NOT private, then set the number of maximum attendees
                    if (isEventPrivate) {
                        event.setPrivateEvent(true);
                    } else {
                        event.setMaxAttendees(numberAttendees);
                    }
                    event.setEventDate(calSelected.getTime().getTime());
                    event.setEventLocation(mRetrievedLocation);

                    mListener.secondFragment(event);

                } else {

                    showRetrieveErrorPopupDialog();

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(getContext(), data);

                mRetrievedLocation = new EventLocation(place.getLatLng().latitude, place.getLatLng().longitude);
                mRetrievedLocation.setName(place.getAddress().toString());
                mRetrievedPositionLabel.setText(place.getAddress().toString());

                positionSelected = true;

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

    /**
     * Returns true if the form is filled, false otherwise
     */
    private boolean isFormFilled() {
        boolean isFilled = false;
        if (nameSelected && dateSelected && positionSelected) {
            isFilled = true;
        }
        return isFilled;
    }

    /**
     * It shows an error popup
     */
    private void showRetrieveErrorPopupDialog() {

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.form_not_completely_filled_popup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, null);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
