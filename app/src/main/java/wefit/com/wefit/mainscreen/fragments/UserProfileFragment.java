package wefit.com.wefit.mainscreen.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
    private Button mButton;

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
        /*mButton = (Button) view.findViewById(R.id.profile_edit);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserBio.setEnabled(!mUserBio.isEnabled());
                if (mUserBio.isEnabled()) {
                    mUserBio.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mUserBio, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        });
        */
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
            mBirthDate.setText(getDate(new Date(mShowedUser.getBirthDate())));
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
