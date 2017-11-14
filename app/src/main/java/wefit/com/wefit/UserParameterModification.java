package wefit.com.wefit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import wefit.com.wefit.pojo.User;
import wefit.com.wefit.viewmodels.UserViewModel;

public class UserParameterModification extends AppCompatActivity {

    private TextView mEmailShower;
    private EditText mNameTextbox;
    private EditText mBiography;
    private Button mTakePicture;
    private ImageView mUserPic;
    private EditText mDateModification;
    private EditText mGenderPick;
    private Button mSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_parameter_modification);

        UserViewModel userViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        mEmailShower = (TextView) findViewById(R.id.email_text);
        mNameTextbox = (EditText) findViewById(R.id.txt_username);

        User logged =  userViewModel.retrieveCachedUser();

        mEmailShower.setText(logged.getContact());
        mNameTextbox.setText(logged.getName());



    }
}
