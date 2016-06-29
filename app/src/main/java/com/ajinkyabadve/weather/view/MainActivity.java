package com.ajinkyabadve.weather.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ActivityMainBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.ListRealm;
import com.ajinkyabadve.weather.ListAdapter;
import com.ajinkyabadve.weather.viewmodel.MainViewModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * test
 */
public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<CityRealm>>, MainViewModel.OnDialogShow {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private MainViewModel mainViewModel;
    ActivityMainBinding activityMainBinding;

    Realm realm;
    private RealmResults<CityRealm> resultRealmResults;

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
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
        mainViewModel = new MainViewModel(MainActivity.this, this);
        activityMainBinding.setViewModel(mainViewModel);
        setSupportActionBar(activityMainBinding.toolbar);
        setWeatherRecyclerView(activityMainBinding.weather);
    }

    private void setWeatherRecyclerView(RecyclerView recyclerView) {
        ListAdapter listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        if (element.size() > 0) {
            CityRealm cityRealm = element.get(0);//just for now get the first o.w get the default selected city
            RealmList<ListRealm> listRealm = cityRealm.getListRealm();
            ListAdapter listAdapter = (ListAdapter) activityMainBinding.weather.getAdapter();
            listAdapter.setList(listRealm);
        }
    }

    @Override
    public void onAddCityDialogShow() {

        startActivity(new Intent(MainActivity.this, AddCity.class));


//        FragmentManager fm = getSupportFragmentManager();
//        AddCityDialogFragment addCityDialogFragment = AddCityDialogFragment.newInstance("Some Title");
//        addCityDialogFragment.show(fm, "fragment_edit_name");
    }
}
