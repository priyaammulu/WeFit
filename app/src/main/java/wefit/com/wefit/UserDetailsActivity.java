package wefit.com.wefit;

import android.annotation.SuppressLint;
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

/**
 * Created by Gioacchino Castorio.
 * This activity shows the PUBLIC information (detail) of a selected user.
 * It allows the user to contact him/her by Email
 * (it is not possible if the selected user is the logged one)
 */
public class UserDetailsActivity extends AppCompatActivity {

    /**
     * Reference to the underlying infrastructure
     */
    private UserViewModel mUserViewModel;

    /**
     * Reference to the retrieved user
     */
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

    /**
     * It simply is the ref to the popup spinner
     */
    private ProgressDialog popupDialogProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupDialogProgress = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true);

        // retrieve the user viewmodel
        this.mUserViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        // retrieve the user's ID from the passed info
        Intent receivedIntent = this.getIntent();
        String userID = receivedIntent.getStringExtra(ExtrasLabels.USER_ID);


        // always check internet connection
        if (NetworkCheker.getInstance().isNetworkAvailable(this)) {
            mUserViewModel.retrieveUserByID(userID).subscribe(new FlowableSubscriber<User>() {
                @Override
                public void onSubscribe(Subscription subscription) {

                    // save the observer registration, in order to dismiss it if there are problems
                    subscription.request(1L);
                    retrieveUserSubscription = subscription;
                }

                @Override
                public void onNext(User user) {

                    // memorize the retrieved user
                    mRetrievedUser = user;

                    // show the layout content
                    // this is a deferred operation in order to download the information first
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

    /**
     * Show the layout on the activity
     * This is a deferred operation in order to download the information first
     */
    private void setupLayout() {

        stopSpinner();
        setContentView(R.layout.activity_user_detail);
        bindActivityComponents();
        fillActivity(); // add user infos
    }

    /**
     * Bind each layout component to its own ref
     */
    private void bindActivityComponents() {

        mUserPic = (ImageView) findViewById(R.id.retrieved_profile_user_pic);
        mUserName = (TextView) findViewById(R.id.retrieved_user_name);
        mBirthDate = (TextView) findViewById(R.id.retrieved_birth_date);
        mUserBio = (TextView) findViewById(R.id.retrieved_user_bio);

        // back-button on topbar
        ImageView mBackbutton = (ImageView) findViewById(R.id.user_details_backbutton);
        mBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // contact button on top-bar
        ImageView mContactButton = (ImageView) findViewById(R.id.user_contact_button);
        if (mRetrievedUser.getId().equals(mUserViewModel.retrieveCachedUser().getId())) {
            // the user cannot contact himself
            mContactButton.setVisibility(View.GONE);
        }
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                // send an email to the user
                String[] TO = {mRetrievedUser.getEmail()};

                // create an email intent specifying the header
                // unfortunately it is not possible to specify the sender
                emailIntent.setData(Uri.parse(getString(R.string.mail_uri_protocol)));
                emailIntent.setType(getString(R.string.email_content_type));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_object) );


                @SuppressLint("StringFormatMatches")
                /* This is a false warning, because the string has been manually formatted
                * This was necessary because we have to move the definition of the message in the "strings" file
                */
                String filledMailBody = String.format(
                        Locale.ENGLISH,
                        getString(R.string.message_mail_user),
                        mRetrievedUser.getFullName(),
                        mUserViewModel.retrieveCachedUser().getFullName());

                emailIntent.putExtra(Intent.EXTRA_TEXT, filledMailBody);

                try {
                    // try to open an email client
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_picker_title)));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), R.string.toat_email_user_agent_not_installed, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Fill the activity with the user's retrieved info
     */
    private void fillActivity() {

        mUserPic.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(mRetrievedUser.getPhoto()));
        mUserName.setText(mRetrievedUser.getFullName());
        mBirthDate.setText(CalendarFormatter.getDate(mRetrievedUser.getBirthDate()));
        mUserBio.setText(mRetrievedUser.getBiography());

    }

    /**
     * Show it if it's impossible to retrieve the user
     */
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

    /**
     * Stop the loading spinner
     */
    private void stopSpinner() {
        if (popupDialogProgress != null)
            popupDialogProgress.dismiss();
        popupDialogProgress = null;
    }

    /**
     * Show it if there's no internet connection
     */
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
