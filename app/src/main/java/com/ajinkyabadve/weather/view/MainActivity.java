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
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.view.adapter.ListAdapter;
import com.ajinkyabadve.weather.viewmodel.MainViewModel;

import java.util.Calendar;

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

            int cityIdPreference = SharedPreferenceDataManager.getInstance(MainActivity.this).getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
            CityRealm cityRealm = null;
            if (cityIdPreference != 0) {
                cityRealm = element.where().equalTo("id", cityIdPreference).findFirst();
            } else {
                cityRealm = element.where().findFirst();
            }

            //just for now get the first o.w get the default selected city from shared prefrence
            if (cityRealm != null) {
                activityMainBinding.toolbar.setTitle(cityRealm.getName());
                long fromUnix = System.currentTimeMillis() / 1000L;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(fromUnix);
                c.add(Calendar.DATE, 14);
                long toUnix = c.getTimeInMillis();
                RealmResults<ListRealm> listRealmRealmResults = cityRealm.getListRealm().where().between("dt", fromUnix, toUnix).findAll();
                RealmList<ListRealm> listRealm = new RealmList<ListRealm>();
                listRealm.addAll(listRealmRealmResults.subList(0, listRealmRealmResults.size()));
                ListAdapter listAdapter = (ListAdapter) activityMainBinding.weather.getAdapter();
                if (listAdapter != null) {
                    listAdapter.setList(listRealm);
                    listAdapter.notifyDataSetChanged();
                }
            }


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
