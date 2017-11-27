package wefit.com.wefit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.UserViewModel;

public class UserDetailsActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;

    private User mRetrievedUser;

    /**
     * Layout components
     */
    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mBirthDate;
    //private TextView mUserSex;
    private TextView mUserBio;
    //private Button mReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_detail);


        this.mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        Intent receivedIntent = this.getIntent();

        String userID = receivedIntent.getStringExtra(ExtrasLabels.USER_ID);




        this.mUserViewModel.retrieveUserByID(userID).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {

                mRetrievedUser = user;

                setupLayout();

            }
        });


    }

    private void setupLayout() {
        bindActivityComponents();
        fillActivity(); // add user infos
    }

    private void bindActivityComponents() {

        mUserPic = (ImageView) findViewById(R.id.retrieved_profile_user_pic);
        mUserName = (TextView) findViewById(R.id.retrieved_user_name);
        mBirthDate = (TextView) findViewById(R.id.retrieved_birth_date);
        mUserBio = (TextView) findViewById(R.id.retrieved_user_bio);
        //mReportButton = (Button) findViewById(R.id.user_report_btn);

    }

    private void fillActivity() {

        mUserPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(mRetrievedUser.getPhoto()));
        mUserName.setText(mRetrievedUser.getFullName());
        mBirthDate.setText(getDate(new Date(mRetrievedUser.getBirthDate())));
        mUserBio.setText(mRetrievedUser.getBiography());

    }


    // TODO spostare altrove, troppo codice ripetuto
    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }
}
