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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import wefit.com.wefit.pojo.User;
import wefit.com.wefit.viewmodels.UserViewModel;

public class GioTestActivity extends AppCompatActivity {

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

        /*
        User creator = new User();
        creator.setId("zbLOEjOmbjWMJCNETOhXkvyTwhi2");

        final Event event = new Event();
        //event.setDisplayName("-Kz47m2Qmn623ifrqLJO");
        //event.setId("hello"); // tODO remove
        event.setAdmin(creator);
        event.setAdminID("fake_admin_id");
        event.setName("evento 0");
        event.setDescription("locamente innamorado");
        event.setImage("245236tddwhtsr");
        event.setPublicationDate(652432);
        event.setEventDate(1511870400);
        event.setMaxAttendee(20);

        Location location = new Location();
        location.setLatitude(14);
        location.setLongitude(13);
        location.setName("guantanamera city");
        event.setEventLocation(location);
        event.setCategoryID("volleyball");

        Map<String, Boolean> attendances = new HashMap<>();
        attendances.put("oMHgmaouzSPyxOVK0gcW3mPp7d42", true);
        attendances.put("IeCvyPwpL6aXbHMAQUdD4BFhcB43", false);

        Log.i("base", event.toString());

        event.setAttendingUsers(attendances);

        User newuser = new User();
        newuser.setId("dsfasf");
        newuser.setFullName("Mattia Bianchi");
        newuser.setEmail("test@gmail.com");
        newuser.setGender("M");
        newuser.setBiography("lorem ipsum");


        final
        //remoteUserDao.save(newuser);

        //RemoteEventDao remoteDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store", remoteUserDao);
        //remoteDao.save(event);

        RemoteEventDao remoteEventDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store");


        WeatherForecast forecaster = new OpenWeatherMapForecastImpl("3f305e12883b15929de1b1b4a5c0c61d");

        forecaster.getForecast(event.getEventLocation(), event.getEventDate()).subscribe(new Consumer<Weather>() {
            @Override
            public void accept(Weather weather) throws Exception {
                // TODO fai qualcosa


                Log.i("weather", weather.toString());
            }
        });
        */

        /*
        remoteEventDao.loadEventsByAdmin("dsagsfdgghg").subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                Log.i("ALMOND", events.toString());
            }
        });
        */

        /*
        remoteEventDao.loadEventsByIDs(Arrays.asList("-KzYaxwmKpyJsNsajIke", "-KzYbCoF3yjm1CHYm_hE")).subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                Log.i("ALMOND", String.valueOf(events.size()));
            }
        });
        */
        /*
        remoteEventDao.loadNewEvents(2, null).subscribe(new Consumer<List<Event>>() {
            @Override
            public void accept(List<Event> events) throws Exception {
                Log.i("retrieved_ev", String.valueOf(events.size()));

                List<String> adminIDs = new ArrayList<>();
                for (Event event : events) {
                    adminIDs.add(event.getAdminID());
                    Log.i("adminIDs", adminIDs.toString());
                }

                remoteUserDao.loadByIDs(adminIDs).subscribe(new Consumer<Map<String, User>>() {
                    @Override
                    public void accept(Map<String, User> users) throws Exception {
                        Log.i("retrUse", users.toString());
                    }
                });

            }
        });
        */




        /*
        List<String> requestUserIDs = new ArrayList<>();
        requestUserIDs.add("dsagsfdgghg");
        requestUserIDs.add("dsfasf");
        remoteUserDao.loadByIDs(requestUserIDs).subscribe(new Consumer<Set<User>>() {
            @Override
            public void accept(Set<User> users) throws Exception {
                Log.i("retrUse", String.valueOf(users.size()));
            }
        });
        */



        /*
        LocalEventDao localEventDao = new LocalSQLiteEventDao(getApplicationContext());
        //localEventDao.wipe();
        //localEventDao.save(event);


        Event ev = localEventDao.loadEventByID("1");
        ev.setDescription("banzai");
        localEventDao.save(ev);
        ev = localEventDao.loadEventByID("1");
        Log.i("locev", ev.toString());
        */

        //LocalEventNotifier notifier = new LocalEventNotifierImpl(this);
        //notifier.notifyEvent(event);


        //RemoteEventDao remoteDao = new RestructuredFirebaseEventDao(FirebaseDatabase.getInstance(), "event_store");
        //final RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), "users");

        //RemoteEventDao remoteDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store", remoteUserDao);
        //remoteDao.save(event);


        /*
        Location center = new Location();
        location.setLatitude(0);
        location.setLongitude(0);

        Location event1Loc = new Location(300, 0);

        Location event2Loc = new Location(200, 0);

        Event event1 = new Event();
        event1.setName("evento 1");
        event1.setEventLocation(event1Loc);

        Log.i("base1", event1.toString());

        Event event2 = new Event();
        event2.setName("evento 2");
        event2.setEventLocation(event2Loc);

        Log.i("base2", event2.toString());


        List<Event> ev = new ArrayList<>();
        ev.add(event1);
        ev.add(event2);

        Log.i("not_sort", ev.toString());

        DistanceSorter sort = new DistanceSorterImpl();
        List<Event> sorted = sort.sortByDistanceFromLocation(center, ev);
        Log.i("sort", sorted.toString());
        */


        //Log.i("gen_cat", CategoryFactory.getInstance().getCategoryByID(event.getCategoryID()).toString());



        /*
        remoteDao.loadEventByID(event.getDisplayName()).subscribe(new Consumer<Event>() {
            @Override
            public void accept(final Event event) throws Exception {
                //Log.i("Evento", event.toString());

                // TODO fill del creatore
                remoteUserDao.loadByID(event.getAdmin().getDisplayName()).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        //Log.i("Evento", user.toString());
                        event.setAdmin(user);
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
        List<Event> eventos = eventDao.loadNewEvents(20, 0);

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
        mNameTextbox.setText(logged.getFullName());
        mBiography.setText(logged.getBiography());

        // lista eventi
        List<String> events = logged.getAttendances();
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

                logged.setFullName(mNameTextbox.getText().toString());
                logged.setBiography(mBiography.getText().toString());
                //logged.setGender(mGenderPick.getText().toString());

                // add events in the store
                logged.setAttendances(Arrays.asList(mEventsPartecipation.getText().toString().split(" ")));

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

            // show image content
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0 /* Ignored for PNGs */, blob);
            byte[] bitmapdata = blob.toByteArray();

            Log.i("image", new String(bitmapdata));


        }
    }
}
