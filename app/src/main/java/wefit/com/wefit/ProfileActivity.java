package wefit.com.wefit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Alicia on 14/11/17.
 */

public class ProfileActivity extends AppCompatActivity {

   TextView userName;
   TextView city;
   TextView birthDate;
   TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);





      /*  User user = new User();
        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(user.getName());

        city = (TextView) findViewById(R.id.city);
        city.setText("Lives in "+ user.getCity());

        birthDate = (TextView) findViewById(R.id.birth_date);
        birthDate.setText("Birth date: " +user.getBirthDate());

        description = (TextView) findViewById(R.id.discription);
        description.setText(user.getDescription());*/


    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
            case android.R.id.button_myevents:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/



}
