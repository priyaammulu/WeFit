package wefit.com.wefit.mainscreen.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wefit.com.wefit.LoginActivity;
import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.UserViewModel;


public class UserProfileFragment extends Fragment {

    private FragmentsInteractionListener mListener;
    private UserViewModel mUserViewModel;

    private User mShowedUser;

    /**
     * Layout components
     */
    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mBirthDate;
    private EditText mUserBio;

    /**
     * Backup old data
     */
    private String tmpOldPicture;
    private long tmpOldBirthDate;
    private String tmpOldBiography;

    private ImageView mEditButton;
    private ImageView mEditDate;
    private LinearLayout mActionModifyButton;
    private Button mDeclineModify;
    private Button mAcceptModify;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        fillFragment();
        super.onViewCreated(view, savedInstanceState);
    }

    private void bind(View view) {

        setupTopbar(view);
        setupNavbar(view);

        mUserPic = (ImageView) view.findViewById(R.id.profile_user_pic);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mBirthDate = (TextView) view.findViewById(R.id.birth_date);
        mUserBio = (EditText) view.findViewById(R.id.user_bio);
        mEditButton = (ImageView) view.findViewById(R.id.user_profile_editbutton);
        mEditDate = (ImageView) view.findViewById(R.id.modify_birthdate_btn);

        mActionModifyButton = (LinearLayout) view.findViewById(R.id.profile_modify_actions);
        mAcceptModify = (Button) view.findViewById(R.id.profile_accept_modification_btn);
        mDeclineModify = (Button) view.findViewById(R.id.profile_discard_modification_btn);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserBio.setEnabled(true);

                mEditDate.setVisibility(View.VISIBLE);
                mActionModifyButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.INVISIBLE);

                // copy the old infos (to perform rollback)
                tmpOldBiography = mShowedUser.getBiography();
                tmpOldBirthDate = mShowedUser.getBirthDate();
                tmpOldPicture = mShowedUser.getPhoto();

            }
        });



        mDeclineModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditDate.setVisibility(View.GONE);
                mActionModifyButton.setVisibility(View.GONE);
                mEditButton.setVisibility(View.VISIBLE);

                mShowedUser.setBiography(tmpOldBiography);
                mShowedUser.setBirthDate(tmpOldBirthDate);
                mShowedUser.setPhoto(tmpOldPicture);

                fillFragment();
            }
        });

        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        tmpOldBirthDate = mShowedUser.getBirthDate();

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
                        cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mUserBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                tmpOldBiography = mUserBio.getText().toString();


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = mListener.getUserViewModel();
        // retrieve the user from the local store
        mShowedUser = mUserViewModel.retrieveCachedUser();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

            // TODO here the operations

        }
    }


    private void fillFragment() {
        if (mShowedUser != null) {
            mUserPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(mShowedUser.getPhoto()));
            mUserName.setText(mShowedUser.getFullName());

            // if the user has not specified his birth date
            if (mShowedUser.getBirthDate() == 0) {
                mBirthDate.setText(R.string.alert_no_age);
            }
            else {
                mBirthDate.setText(getDate(new Date(mShowedUser.getBirthDate())));
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

    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }

    private void setupNavbar(View layout) {


        layout.findViewById(R.id.profile_myevents_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).fragmentTransaction(MainActivity.MY_ATTENDANCES_FRAGMENT);
            }
        });

        layout.findViewById(R.id.profile_button_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).fragmentTransaction(MainActivity.MAIN_FRAGMENT);
            }
        });

        layout.findViewById(R.id.profile_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).fragmentTransaction(MainActivity.PROFILE_FRAGMENT);
            }
        });


    }

    private void setupTopbar(View layout) {


        layout.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserViewModel.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();

            }
        });



    }

}
