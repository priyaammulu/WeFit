package wefit.com.wefit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.RemoteUserDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseEventDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseUserDao;
import wefit.com.wefit.viewmodels.UserViewModel;

public class UserParameterModification extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView mEmailShower;
    private EditText mEventsPartecipation;
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




        // TODO creazione evento fittizio



        User creator = new User();
        creator.setUserId("zbLOEjOmbjWMJCNETOhXkvyTwhi2");

        final Event event = new Event();
        //event.setId("-Kz47m2Qmn623ifrqLJO");
        event.setCreator(creator);
        event.setTitle("Bellolevento");
        event.setDescription("locamente innamorado");
        event.setImage("245236tddwhtsr");
        event.setPublished(new Date(652432));
        event.setExpire(new Date(124534765));

        Location location = new Location();
        location.setLatitude(34565.4);
        location.setLongitude(324535.6);
        location.setName("guantanamera city");
        event.setLocation(location);
        event.setCategoryName("category1");

        User part1 = new User();
        part1.setUserId("oMHgmaouzSPyxOVK0gcW3mPp7d42");
        User part2 = new User();
        part2.setUserId("IeCvyPwpL6aXbHMAQUdD4BFhcB43");

        List<User> parts = new ArrayList<>();
        parts.add(part1);
        parts.add(part2);

        event.setParticipants(parts);


        //RemoteEventDao remoteDao = new RestructuredFirebaseEventDao(FirebaseDatabase.getInstance(), "event_store");
        final RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), "users");

        RemoteEventDao remoteDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "event_store", remoteUserDao);
        remoteDao.save(event);


        remoteDao.getEvents(6, 0, null).subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                Log.i("Eventos", String.valueOf(events.size()));
            }
        });

        /*
        remoteDao.loadEventByID(event.getId()).subscribe(new Consumer<Event>() {
            @Override
            public void accept(final Event event) throws Exception {
                //Log.i("Evento", event.toString());

                // TODO fill del creatore
                remoteUserDao.loadByID(event.getCreator().getUserId()).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        //Log.i("Evento", user.toString());
                        event.setCreator(user);
                        Log.i("Evento", event.toString());
                    }
                });





            }
        });
        */



        /*
        LocalEventDao eventDao = new LocalSQLiteEventDao(this);
        //eventDao.wipe();
        event = eventDao.save(event);
        List<Event> eventos = eventDao.getEvents(20, 0);

        Log.i("Evento", String.valueOf(eventos.size()));
        */
        // TODO creazione evento fittizio

        final UserViewModel userViewModel = ((WefitApplication) getApplication()).getUserViewModel();

        mEmailShower = (TextView) findViewById(R.id.email_text);
        mNameTextbox = (EditText) findViewById(R.id.txt_username);
        mBiography = (EditText) findViewById(R.id.txt_bio);
        mTakePicture = (Button) findViewById(R.id.btn_change_image);
        mUserPic = (ImageView) findViewById(R.id.pic_user_image);
        mDateModification = (EditText) findViewById(R.id.txt_date);
        mGenderPick = (EditText) findViewById(R.id.txt_gender);
        mSubmit = (Button) findViewById(R.id.btn_data_modify_submit);

        mEventsPartecipation = (EditText) findViewById(R.id.txt_lst_events);

        final User logged = userViewModel.retrieveCachedUser();

        // show old data
        mEmailShower.setText(logged.getEmail());
        mNameTextbox.setText(logged.getName());
        mBiography.setText(logged.getBiography());

        // lista eventi
        List<String> events = logged.getEventPartecipations();
        StringBuilder strEvents = new StringBuilder();
        for (String s : events) {
            strEvents.append(s).append(" ");
        }
        mEventsPartecipation.setText(strEvents);


        // set image
        try {
            byte[] encodeByte = Base64.decode(logged.getPhoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            mUserPic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.getMessage();
        }


        // take picture
        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        // submit modifications
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logged.setName(mNameTextbox.getText().toString());
                logged.setBiography(mBiography.getText().toString());
                //logged.setGender(mGenderPick.getText().toString());

                // add events in the store
                logged.setEventPartecipations(Arrays.asList(mEventsPartecipation.getText().toString().split(" ")));

                // take image
                Bitmap pic = ((BitmapDrawable) mUserPic.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pic.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                logged.setPhoto(temp);
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                userViewModel.updateUser(logged);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mUserPic.setImageBitmap(imageBitmap);
        }
    }
}
