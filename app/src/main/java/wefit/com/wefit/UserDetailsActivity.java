package wefit.com.wefit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import java.util.Locale;

import io.reactivex.FlowableSubscriber;
import wefit.com.wefit.mainscreen.MainActivity;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.utils.NetworkCheker;
import wefit.com.wefit.utils.calendar.CalendarFormatter;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.UserViewModel;

public class UserDetailsActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;

    private User mRetrievedUser;

    /**
     * RxJava observer substrictions
     */
    private Subscription retrieveUserSubscription;

    /**
     * Layout components
     */
    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mBirthDate;
    private TextView mUserBio;

    private ProgressDialog popupDialogProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupDialogProgress = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true);

        this.mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        Intent receivedIntent = this.getIntent();
        String userID = receivedIntent.getStringExtra(ExtrasLabels.USER_ID);


        // always check internet connection
        if (NetworkCheker.getInstance().isNetworkAvailable(this)) {
            mUserViewModel.retrieveUserByID(userID).subscribe(new FlowableSubscriber<User>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1L);
                    retrieveUserSubscription = subscription;
                }

                @Override
                public void onNext(User user) {

                    // memorize the retrieved user
                    mRetrievedUser = user;

                    setupLayout();
                }

                @Override
                public void onError(Throwable throwable) {

                    showRetrieveErrorPopupDialog();

                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            showNoInternetConnectionPopup();
        }


    }

    private void setupLayout() {

        stopSpinner();
        setContentView(R.layout.activity_user_detail);
        bindActivityComponents();
        fillActivity(); // add user infos
    }

    private void bindActivityComponents() {

        mUserPic = (ImageView) findViewById(R.id.retrieved_profile_user_pic);
        mUserName = (TextView) findViewById(R.id.retrieved_user_name);
        mBirthDate = (TextView) findViewById(R.id.retrieved_birth_date);
        mUserBio = (TextView) findViewById(R.id.retrieved_user_bio);

        ImageView mContactButton = (ImageView) findViewById(R.id.user_contact_button);
        ImageView mBackbutton = (ImageView) findViewById(R.id.user_details_backbutton);

        mBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send an email to the user
                String[] TO = {mRetrievedUser.getEmail()};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType(getString(R.string.email_content_type));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_object) );

                // TOO not possible to extract
                String mailBody = "Hi %s, I'm %s from WeFit";

                String filledMailBody = String.format(Locale.ENGLISH, mailBody,
                        mRetrievedUser.getFullName(),
                        mUserViewModel.retrieveCachedUser().getFullName());

                emailIntent.putExtra(Intent.EXTRA_TEXT, filledMailBody);

                try {
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_picker_title)));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), R.string.toat_email_user_agent_not_installed, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void fillActivity() {

        mUserPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(mRetrievedUser.getPhoto()));
        mUserName.setText(mRetrievedUser.getFullName());
        mBirthDate.setText(CalendarFormatter.getDate(mRetrievedUser.getBirthDate()));
        mUserBio.setText(mRetrievedUser.getBiography());

    }

    private void showRetrieveErrorPopupDialog() {

        this.popupDialogProgress.dismiss();

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // , go to the main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (retrieveUserSubscription != null) {
            retrieveUserSubscription.cancel();
        }
    }

    private void stopSpinner() {
        if (popupDialogProgress != null)
            popupDialogProgress.dismiss();
        popupDialogProgress = null;
    }

    private void showNoInternetConnectionPopup() {

        stopSpinner();

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go to the main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
