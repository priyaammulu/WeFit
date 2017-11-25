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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.persistence.LocalEventDao;
import wefit.com.wefit.utils.persistence.RemoteEventDao;
import wefit.com.wefit.utils.persistence.firebasepersistence.FirebaseEventDao;
import wefit.com.wefit.utils.persistence.sqlitelocalpersistence.LocalSQLiteEventDao;
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
        setContentView(R.layout.activity_gio_test);


        // TODO creazione evento fittizio


        User creator = new User();
        //creator.setId("zbLOEjOmbjWMJCNETOhXkvyTwhi2");

        final Event mEvent = new Event();
        //mEvent.setDisplayName("-Kz47m2Qmn623ifrqLJO");
        //mEvent.setId("hello"); // tODO remove
        mEvent.setAdmin(creator);
        mEvent.setAdminID("IeCvyPwpL6aXbHMAQUdD4BFhcB43");
        mEvent.setName("beatles are here");
        mEvent.setDescription("I come to say hello!");
        mEvent.setImage("iVBORw0KGgoAAAANSUhEUgAAAKAAAAB4CAIAAAD6wG44AAAAA3NCSVQICAjb4U/gAAAD5ElEQVR4\nnO2dzU4TURiG328oiJg0caFgu5UoS4WEsKHxDgjsuAfkWhDdaOI1iF6EIbEY124Bf7aEBdDO5wIw\nEXuaOWX++uZ9dmTOvGd6nk5Pydc5x6anpxFga2srdGggu7u7Ue0H5pvDLQF6AIAk9/ww6avXb8xh\nnrilBeQXff2D8xtRESXglgKJo2HAtea/ZBr30do7ppIUaQLz2F5qTcPdQ8fMbMjR2zQecoqj8XXu\nc/fBtzSB+T938J1396Ly3+J9zNWcYW0CH3opgGyvo5LxiW1sm5uboXP29/ezX427r6ysRL2Ggfkp\n/OzF6Wnn/OqvsnDDk+/z89+fAr0bU0OISsYnNr/RbrdD5xwdHWXvAECr1YpqPzDf3adOJhNMuMHi\n3vG3whzNZrPdfgQkGd9YlYxPbH6mt6oYXySYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJJkeCyZFg\ncoKCY+uX7m5m2duPUB+tFXUbn1C+ra6uDukjeweXfUS1H5jv7r+Xfv149rPkciGAuYPZ2e5c9nJh\nJeMTm291+00WgIO57peH3fIF9z/1eh/72QWPxW+yNAeTI8HkSDA5EkyOBJMjweRIMDkSTI4EkyPB\n5EgwORJMjgSTExQcW7+MZUj+WNSJKxyfKIY9Pnp4eBiVNSQqKv9k5gSzUUn50Gw277ZnspcLqxqf\nqPzG+vp66IRK6pfuPrU2mTyeiIrKhYWFhef3F7MLrmR8YvM1B5MjweRIMDm1E+zX3xxvLLFTAing\nSP5bu2m8qZ1geAL0DGnG1chyw/oAYGmB//pUQe0WQkvML/aS872+4eLGoWK/hXpj6eWiOdwaZS7f\nVDS1u4Pt6g5G2aNs5wDc2Fa6q53gy6UMgbT0a0uuL6DcbgumdoJFvkgwORJMjgSTI8HkSDA5EkxO\nHZ8PVn6O+bqDyZFgciSYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJJkeCyZFgciSYnNw2iAawvLwc\n1V75JeTntkG0u29sbEStvqD8EvJz+4gueg8G5Y+WrzmYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJ\nJkeCyZFgciSYHOt0OqFjlWxorPx884MPgLv79vZ29j7MbGdnJ/trUH45+cGP6HHZo175w/M1B5Mj\nweRIMDkSTI4EkyPB5EgwORJMjgSTI8HkSDA5EkyOBJMTfHzU3Y+Pj6PKVa1WK6ocpvwS8oMbRF/W\nFzOmXzJC/VL5RecH9y4cYQ/5Quujyh8tX3MwORJMjgSTI8HkSDA5EkyOBJMjweRIMDkSTI4EkyPB\n5EgwOXq6kDxfzweT5+sOJs/XHEzOH/3BqRFWGcG3AAAAAElFTkSuQmCC\n");
        mEvent.setPublicationDate(1511527659215L);
        mEvent.setEventDate(151156353458005000L);
        mEvent.setMaxAttendee(20);

        Location location = new Location();
        location.setLatitude(53.35014);
        location.setLongitude(6.266155);
        location.setName("Stillogarn Road 15, Dublin");
        mEvent.setEventLocation(location);
        mEvent.setCategoryID("volleyball");

        Map<String, Boolean> attendances = new HashMap<>();
        attendances.put("1M5hEdlM4iQvMWXhA3eNTZ0Tjfg1", false);

        Log.i("base", mEvent.toString());

        mEvent.setAttendingUsers(attendances);

        /*
        User newuser = new User();
        newuser.setId("dsfasf");
        newuser.setFullName("Mattia Bianchi");
        newuser.setEmail("test@gmail.com");
        newuser.setGender("M");
        newuser.setBiography("lorem ipsum");
        */


        RemoteEventDao remoteEventDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store");
        remoteEventDao.save(mEvent);

        //LocalEventDao localEventDao = new LocalSQLiteEventDao(this);
        //localEventDao.wipe();
        //localEventDao.save(mEvent);

        /*
        WeatherForecast forecaster = new OpenWeatherMapForecastImpl("3f305e12883b15929de1b1b4a5c0c61d");

        forecaster.getForecast(mEvent.getEventLocation(), mEvent.getEventDate()).subscribe(new Consumer<Weather>() {
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
                for (Event mEvent : events) {
                    adminIDs.add(mEvent.getAdminID());
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
        //localEventDao.save(mEvent);


        Event ev = localEventDao.loadEventByID("1");
        ev.setDescription("banzai");
        localEventDao.save(ev);
        ev = localEventDao.loadEventByID("1");
        Log.i("locev", ev.toString());
        */

        //LocalEventNotifier notifier = new LocalEventNotifierImpl(this);
        //notifier.notifyEvent(mEvent);


        //RemoteEventDao remoteDao = new RestructuredFirebaseEventDao(FirebaseDatabase.getInstance(), "event_store");
        //final RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), "users");

        //RemoteEventDao remoteDao = new FirebaseEventDao(FirebaseDatabase.getInstance(), "test_event_store", remoteUserDao);
        //remoteDao.save(mEvent);


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


        //Log.i("gen_cat", CategoryIconFactory.getInstance().getCategoryByID(mEvent.getCategoryID()).toString());



        /*
        remoteDao.loadEventByID(mEvent.getDisplayName()).subscribe(new Consumer<Event>() {
            @Override
            public void accept(final Event mEvent) throws Exception {
                //Log.i("Evento", mEvent.toString());

                // TODO fill del creatore
                remoteUserDao.loadByID(mEvent.getAdmin().getDisplayName()).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        //Log.i("Evento", user.toString());
                        mEvent.setAdmin(user);
                        Log.i("Evento", mEvent.toString());
                    }
                });





            }
        });
        */



        /*
        LocalEventDao eventDao = new LocalSQLiteEventDao(this);
        //eventDao.wipe();
        mEvent = eventDao.save(mEvent);
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
