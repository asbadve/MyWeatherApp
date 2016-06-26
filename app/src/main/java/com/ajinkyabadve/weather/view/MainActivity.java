package com.ajinkyabadve.weather.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ajinkyabadve.Realm.R;
import com.ajinkyabadve.Realm.databinding.ActivityMainBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.viewmodel.MainViewModel;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * test
 */
public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<CityRealm>> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MainViewModel mainViewModel;
    private ActivityMainBinding activityMainBinding;

    Realm realm;
    private RealmResults<CityRealm> resultRealmResults;

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        String[] columnToBeSort = {"releaseDate", "voteAverage"};
        Sort[] sortOrders = {Sort.ASCENDING, Sort.DESCENDING};
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        resultRealmResults = realm.where(CityRealm.class).findAllAsync();
        resultRealmResults.addChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        resultRealmResults.removeChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new MainViewModel(MainActivity.this);
        activityMainBinding.setViewModel(mainViewModel);
        setSupportActionBar(activityMainBinding.toolbar);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChange(RealmResults<CityRealm> element) {
        Log.d(TAG, "onChange() called with: " + "element = [" + element + "]");
        CityRealm df = element.get(0);
    }
}
