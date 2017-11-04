package wefit.com.wefit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import wefit.com.wefit.viewmodels.LoginViewModel;
import wefit.com.wefit.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentInteractionListener {
    private Button mSignOut;
    private LoginViewModel mLoginViewModel;
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ((WefitApplication) getApplication()).getLoginViewModel();
        mMainViewModel = ((WefitApplication) getApplication()).getMainViewModel();
        setContentView(R.layout.activity_main);
        bind();
    }

    private void bind() {
        mSignOut = (Button) findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(view -> {
            mLoginViewModel.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragment fragment = MainFragment.newInstance();
        fragmentTransaction.add(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public MainViewModel getMainViewModel() {
        return mMainViewModel;
    }
}
