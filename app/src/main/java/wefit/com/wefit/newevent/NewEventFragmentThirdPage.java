package wefit.com.wefit.newevent;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * Created by lorenzo on 10/28/17.
 * This Fragment is the third step in the creation of an Event flow
 */
public class NewEventFragmentThirdPage extends Fragment {
    /**
     * Reference to the activity
     */
    private NewFragmentListener mListener;

    /**
     * Layout
     */
    private EditText mEventDescription;

    public NewEventFragmentThirdPage() {
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

    /**
     * Binds UI views to fields
     */
    private void bind(View view) {

        ImageView mBackButton = (ImageView) view.findViewById(R.id.new_event_page3_backbutton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button finishButton = (Button) view.findViewById(R.id.new_event_button_finish);
        mEventDescription = (EditText) view.findViewById(R.id.new_event_third_description);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFormFilled()) {
                    Event event = mListener.getEvent();
                    event.setDescription(mEventDescription.getText().toString());
                    mListener.finish(event);
                } else {
                    showRetrieveErrorPopupDialog();
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event_fragment_page_third, container, false);
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
     * Return true if the form is filled
     */
    private boolean isFormFilled() {
        return mEventDescription.getText().length() > 0;
    }
    /**
     * It shows an error popup dialog
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
