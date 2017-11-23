package wefit.com.wefit.mainscreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wefit.com.wefit.R;


public class ProfileFragment extends Fragment {
    private FragmentsInteractionListener mListener;

    TextView userName;
    TextView city;
    TextView birthDate;
    TextView description;

    ImageView leftTopButton;
    TextView middleTopButton;
    ImageView rightTopButton;

    MainActivity mainActivity;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mListener.fillInIcons(R.drawable.ic_arrow, "Profile", R.drawable.ic_warning);
        }
    }

    private void bind(View view) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //insert icons
//        mainActivity.fillInIcons(R.drawable.ic_arrow, "Profile", R.drawable.ic_warning);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

       /* User user = new User();
        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(user.getDisplayName());

        city = (TextView) findViewById(R.id.city);
        city.setText("Lives in "+ user.getCity());

        birthDate = (TextView) findViewById(R.id.birth_date);
        birthDate.setText("Birth date: " +user.getBirthDate());

        description = (TextView) findViewById(R.id.discription);
        description.setText(user.getDescription());*/
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
}
