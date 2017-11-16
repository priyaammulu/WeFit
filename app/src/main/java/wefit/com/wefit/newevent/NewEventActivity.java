package wefit.com.wefit.newevent;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;

public class NewEventActivity extends AppCompatActivity implements NewFragmentListener {
    private NewEventFragmentFirst fragmentFirst = new NewEventFragmentFirst();
    private NewEventFragmentSecond fragmentSecond = new NewEventFragmentSecond();
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        bind();
        attachFragment(fragmentFirst);
    }

    private void attachFragment(Fragment fragment) {
        fragmentFirst.setCategory(category);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.new_event_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void bind() {

    }

    @Override
    public void secondFragment(Category category) {
        attachFragment(fragmentSecond);
    }
}
