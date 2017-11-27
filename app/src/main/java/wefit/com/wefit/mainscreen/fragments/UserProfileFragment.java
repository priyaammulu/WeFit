package wefit.com.wefit.mainscreen.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wefit.com.wefit.R;
import wefit.com.wefit.mainscreen.FragmentsInteractionListener;
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
        mUserPic = (ImageView) view.findViewById(R.id.profile_user_pic);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mBirthDate = (TextView) view.findViewById(R.id.birth_date);
        mUserBio = (EditText) view.findViewById(R.id.user_bio);
        mButton = (Button) view.findViewById(R.id.profile_edit);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserBio.setEnabled(!mUserBio.isEnabled());
                mListener.toggleBottomBar();
                if (mUserBio.isEnabled()) {
                    mUserBio.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mUserBio, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
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
            mListener.fillInIcons(R.drawable.ic_arrow, "Profile", R.drawable.ic_warning);
            mListener.changeStatusPressedInProfile();

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
        return inflater.inflate(R.layout.profile_layout, container, false);
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

}
