package wefit.com.wefit.newevent;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import wefit.com.wefit.R;

public class NewEventActivity extends AppCompatActivity implements NewFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        bind();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.new_event_fragment, new NewEventFragmentFirst())
                .commit();
    }

    private void bind() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
