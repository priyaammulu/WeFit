package wefit.com.wefit.mainscreen.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.utils.calendar.CalendarFormatter;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.EventViewModel;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentsInteractionListener} interface
 * to handle interaction events.
 */
public class UserProfileFragment extends Fragment {
    /**
     * Constants for #onActivityResult
     */
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int RESULT_OK = -1;
    /**
     * Reference to the activity
     */
    private FragmentsInteractionListener mListener;
    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * User showed in this layout
     */
    private User mShowedUser;

    /**
     * Layout components
     */
    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mBirthDate;
    private EditText mUserBio;
    private ImageView mEditButton;
    private ImageView mEditDate;
    private LinearLayout mActionModifyButton;
    private ImageView mEditPhotoIndicator;
    private EventViewModel mEventViewModel;

    /**
     * Backup old data
     */
    private String tmpOldPicture;
    private long tmpOldBirthDate;
    private String tmpOldBiography;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        fillFragment();
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * It binds UI layout to properties
     */
    private void bind(View view) {

        setupTopbar(view);

        mUserPic = (ImageView) view.findViewById(R.id.profile_user_pic);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mBirthDate = (TextView) view.findViewById(R.id.birth_date);
        mUserBio = (EditText) view.findViewById(R.id.user_bio);
        mEditButton = (ImageView) view.findViewById(R.id.user_profile_editbutton);
        mEditDate = (ImageView) view.findViewById(R.id.modify_birthdate_btn);
        mEditPhotoIndicator = (ImageView) view.findViewById(R.id.image_modify_indicator);

        mActionModifyButton = (LinearLayout) view.findViewById(R.id.profile_modify_actions);
        Button mAcceptModify = (Button) view.findViewById(R.id.profile_accept_modification_btn);
        Button mDeclineModify = (Button) view.findViewById(R.id.profile_discard_modification_btn);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserBio.setEnabled(true);

                mEditDate.setVisibility(View.VISIBLE);
                mActionModifyButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.INVISIBLE);
                mEditPhotoIndicator.setVisibility(View.VISIBLE);


                // copy the old infos (to perform rollback)
                tmpOldBiography = mShowedUser.getBiography();
                tmpOldBirthDate = mShowedUser.getBirthDate();
                tmpOldPicture = mShowedUser.getPhoto();

                mUserPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                });


            }
        });


        mDeclineModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetInvisibleModificationComponents();

                mShowedUser.setBiography(tmpOldBiography);
                mShowedUser.setBirthDate(tmpOldBirthDate);
                mShowedUser.setPhoto(tmpOldPicture);

                fillFragment();
            }
        });

        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // underaged users are not allowed
                final long HEIGHTEEN_YEARS_AGO = 568025136000L;

                Calendar cal = Calendar.getInstance();
                DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar cal = Calendar.getInstance();

                        // set day
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mShowedUser.setBirthDate(cal.getTime().getTime());

                        fillFragment();

                    }
                }, cal
                        .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));

                pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - HEIGHTEEN_YEARS_AGO);
                pickerDialog.show();


            }
        });

        mAcceptModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
                    resetInvisibleModificationComponents();

                    if (mUserBio.getText().toString().length() > 0) {

                        mShowedUser.setBiography(mUserBio.getText().toString());
                        mUserViewModel.updateUser(mShowedUser);

                    } else {
                        Toast.makeText(getContext(), getString(R.string.bio_forgot_toast), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();

                }

            }
        });
    }
    /**
     * It modifies the layout
     */
    private void resetInvisibleModificationComponents() {
        mEditDate.setVisibility(View.GONE);
        mActionModifyButton.setVisibility(View.GONE);
        mEditButton.setVisibility(View.VISIBLE);
        mEditPhotoIndicator.setVisibility(View.GONE);
        mUserBio.setEnabled(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = mListener.getUserViewModel();
        // retrieve the user from the local store
        mShowedUser = mUserViewModel.retrieveCachedUser();

        this.mEventViewModel = mListener.getEventViewModel();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * Loads the UI with the selected user
     */
    private void fillFragment() {
        if (mShowedUser != null) {
            mUserPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(mShowedUser.getPhoto()));
            mUserName.setText(mShowedUser.getFullName());

            // if the user has not specified his birth date
            if (mShowedUser.getBirthDate() == 0) {
                mBirthDate.setText(R.string.alert_no_age);
            } else {
                mBirthDate.setText(CalendarFormatter.getDate(mShowedUser.getBirthDate()));
            }

            mUserBio.setText(mShowedUser.getBiography());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
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
    }

    /**
     * It setups the toolbar
     */
    private void setupTopbar(View layout) {
        layout.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserViewModel.signOut();
                mEventViewModel.wipeLocalEvents();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // result for the image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mUserPic.setImageBitmap(imageBitmap);

            mShowedUser.setPhoto(ImageBase64Marshaller.encodeBase64BitmapString(imageBitmap));
        }
    }

}
