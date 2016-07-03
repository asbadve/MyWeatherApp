package com.ajinkyabadve.weather.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.ajinkyabadve.weather.util.Util;
import com.ajinkyabadve.weather.view.adapter.ListAdapter;
import com.ajinkyabadve.weather.view.adapter.SimpleDividerItemDecoration;
import com.ajinkyabadve.weather.viewmodel.AddCityActivityViewModel;
import com.ajinkyabadve.weather.viewmodel.MainViewModel;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<CityRealm>>, MainViewModel.OnDialogShow, MainViewModel.onCityAddeByLatLong {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2;
    private MainViewModel mainViewModel;
    ActivityMainBinding activityMainBinding;

    Realm realm;
    private RealmResults<CityRealm> resultRealmResults;
    private GoogleApiClient client;
    private SharedPreferenceDataManager sharedPreferenceDataManager;

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
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new MainViewModel(MainActivity.this, this, this);
        activityMainBinding.setViewModel(mainViewModel);
        setSupportActionBar(activityMainBinding.toolbar);
        setWeatherRecyclerView(activityMainBinding.weather);
        client = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        client.connect();
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(MainActivity.this);

    }

    private void setWeatherRecyclerView(RecyclerView recyclerView) {
        ListAdapter listAdapter = new ListAdapter();
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(MainActivity.this));
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
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
            int cityIdPreference = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
            CityRealm cityRealm = null;
            if (cityIdPreference != 0) {
                cityRealm = element.where().equalTo("id", cityIdPreference).findFirst();
                if (cityRealm == null) {
                    cityRealm = element.where().findFirst();
                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
                }
            } else {
                cityRealm = element.where().findFirst();
            }

            //just for now get the first o.w get the default selected city from shared prefrence
            if (cityRealm != null) {
                activityMainBinding.toolbar.setTitle(cityRealm.getName());

                String startDate = Util.getDbDateString(new Date());
                String endDate = Util.getDbDateStringAfter14Days(new Date());
//                long fromUnix = System.currentTimeMillis() / 1000L;
//                Calendar c = Calendar.getInstance();
//                c.setTimeInMillis(fromUnix);
//                c.add(Calendar.DATE, 14);
//                long toUnix = c.getTimeInMillis();
                RealmResults<ListRealm> listRealmRealmResults = cityRealm.getListRealm().where().between("dt", Integer.parseInt(startDate), Integer.parseInt(endDate)).findAll();
                RealmList<ListRealm> listRealm = new RealmList<ListRealm>();
                listRealm.addAll(listRealmRealmResults.subList(0, listRealmRealmResults.size()));
                ListAdapter listAdapter = (ListAdapter) activityMainBinding.weather.getAdapter();
                if (listAdapter != null) {
                    listAdapter.setList(listRealm);
                    listAdapter.notifyDataSetChanged();
                }
            }


        } else {
            ListAdapter listAdapter = (ListAdapter) activityMainBinding.weather.getAdapter();
            if (listAdapter != null) {
                listAdapter.setList(null);
                listAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onAddCityDialogShow(boolean showDialog) {
        if (showDialog) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(MainActivity.this.getResources().getString(R.string.add_city_manualy_current_location))
                    .setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                                    // Show an expanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.


                                } else {

                                    // No explanation needed, we can request the permission.

                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.
                                }
                                return;
                            } else {
                                checkErrorGetLatLong();
                            }


                        }
                    })
                    .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.FIREST_LAUCH, 2);
                            startActivity(new Intent(MainActivity.this, AddCity.class));
                        }
                    })
                    .create();
            alertDialog.show();
        } else {
            startActivity(new Intent(MainActivity.this, AddCity.class));

        }


//        FragmentManager fm = getSupportFragmentManager();
//        AddCityDialogFragment addCityDialogFragment = AddCityDialogFragment.newInstance("Some Title");
//        addCityDialogFragment.show(fm, "fragment_edit_name");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    checkErrorGetLatLong();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    if (sharedPreferenceDataManager != null) {
//                        sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_PERMISSION_FINE_LOCATION, false);
//                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkErrorGetLatLong() {
        if (Util.isNetworkAvailable(MainActivity.this) && Util.isGpsEnable(MainActivity.this)) {
            getCurrentLatLong();
        } else {
            if (!Util.isGpsEnable(MainActivity.this) && !Util.isNetworkAvailable(MainActivity.this)) {
                Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "Please turn on Gps as well as Internet connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (!Util.isGpsEnable(MainActivity.this)) {
                Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "Please turn on Gps", Snackbar.LENGTH_LONG);
                snackbar.show();

            } else if (!Util.isNetworkAvailable(MainActivity.this)) {
                Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "No connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void getCurrentLatLong() {
        mainViewModel.showProgressBar();
        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResolvingResultCallbacks<LocationResult>(MainActivity.this, 5) {
                    @Override
                    public void onSuccess(@NonNull LocationResult locationResult) {
                        Log.d(TAG, "onSuccess() called with: " + "locationResult = [" + locationResult + "]");
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get location.");
                            if (!Util.isGpsEnable(MainActivity.this)) {
                                Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "Please turn on the gps", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "Could not get location", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                            return;
                        }
                        Location location = locationResult.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses != null) {
                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);
                            String locality = addresses.get(0).getLocality();
                            mainViewModel.checkIfPlaceIsValid(locality);

                        } else {
                            mainViewModel.addCityByLatLong(latitude, longitude);

                        }


                    }

                    @Override
                    public void onUnresolvableFailure(@NonNull Status status) {
                        Log.d(TAG, "onUnresolvableFailure() called with: " + "status = [" + status + "]");
                        mainViewModel.hideProgressBar();

                        if (status != null) {
                            //the documentation is unavailable for status code.
                            int statusCode = status.getStatusCode();
                        }

                        if (!Util.isGpsEnable(MainActivity.this)) {
                            Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, "Please turn on the gps", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


                    }
                });
    }

    @Override
    public void onCityAddedByLatLong() {

    }

    @Override
    public void onCityAddedError(@AddCityActivityViewModel.AddCityErrorFlag int errorFlag, String message) {
        String errorMesg = "";
        errorMesg = getErrorString(errorFlag, message, errorMesg);
        Snackbar snackbar = Snackbar.make(activityMainBinding.coordinateLayout, errorMesg, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    private String getErrorString(@AddCityActivityViewModel.AddCityErrorFlag int errorFlag, String message, String errorMesg) {
        switch (errorFlag) {
            case AddCityActivityViewModel.FLAG_CITY_ALREADY_PRESENT:
                errorMesg = "This city is already added";

                break;
            case AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG:
                if (message != null) {
                    errorMesg = message;
                } else {
                    errorMesg = "Something went wrong please try again later";
                }

                break;
            case AddCityActivityViewModel.FLAG_CITY_WEATHER_NOT_AVAILABLE:
                errorMesg = "Weather for this city is not available";

                break;
            case AddCityActivityViewModel.FLAG_CITY_NOT_FOUND:
                errorMesg = "Weather for this city is not available";

                break;
            case AddCityActivityViewModel.FLAG_INTERNET_NOT_AVAILABLE:
                errorMesg = "No connection";

                break;
            case AddCityActivityViewModel.FLAG_CITY_NOT_MATCH:
                errorMesg = "City name not match";
                break;

        }
        return errorMesg;
    }
}
