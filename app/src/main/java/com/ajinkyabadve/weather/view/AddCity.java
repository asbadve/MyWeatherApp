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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ActivityAddCityBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.util.Util;
import com.ajinkyabadve.weather.view.adapter.CitiesAdapter;
import com.ajinkyabadve.weather.viewmodel.AddCityActivityViewModel;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/****
 * this activity is for adding multiple city and current location
 */
public class AddCity extends AppCompatActivity implements AddCityActivityViewModel.ActivityModelCommunicationListener, CitiesAdapter.OnCityOperation {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2;
    AddCityActivityViewModel addCityActivityViewModel;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = AddCity.class.getSimpleName();
    ActivityAddCityBinding activityAddCityBinding;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddCityBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_city);
        addCityActivityViewModel = new AddCityActivityViewModel(AddCity.this, this);
        activityAddCityBinding.setAddcityciewcodel(addCityActivityViewModel);
        setSupportActionBar(activityAddCityBinding.toolbar);
        setUpRecyclerView(activityAddCityBinding.cities);
        client = new GoogleApiClient.Builder(AddCity.this)
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    /***
     * set up recycler adapterView
     *
     * @param recyclerView
     */
    private void setUpRecyclerView(RecyclerView recyclerView) {
        CitiesAdapter citiesAdapter = new CitiesAdapter(this);
        recyclerView.setAdapter(citiesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        addCityActivityViewModel.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        addCityActivityViewModel.Stop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_city) {
            try {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                        .build();
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .setFilter(typeFilter)
                                .build(AddCity.this);


                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, "onOptionsItemSelected:GooglePlayServicesRepairableException ", e);
            }


            return true;
        } else if (id == R.id.action_add_current_city) {
            if (ActivityCompat.checkSelfPermission(AddCity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddCity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(AddCity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);


                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(AddCity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);


                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                chekErrorGetCurrentLatLong();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * get current lat long and its weather
     */
    @SuppressWarnings({"MissingPermission"})
    private void getCurrentLatLongAddCity() {
        addCityActivityViewModel.showProgressBar();
        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResolvingResultCallbacks<LocationResult>(AddCity.this, 5) {
                    @Override
                    public void onSuccess(@NonNull LocationResult locationResult) {
                        Log.d(TAG, "onSuccess() called with: " + "locationResult = [" + locationResult + "]");

                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get location.");
                            if (!Util.isGpsEnable(AddCity.this)) {
                                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Please turn on the gps", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Could not get location", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                            return;
                        }
                        Location location = locationResult.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(AddCity.this, Locale.getDefault());
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
                            addCityActivityViewModel.checkIfPlaceIsValid(locality);

                        } else {
                            addCityActivityViewModel.addCityByLatLong(latitude, longitude);
                        }


                    }

                    @Override
                    public void onUnresolvableFailure(@NonNull Status status) {
                        Log.d(TAG, "onUnresolvableFailure() called with: " + "status = [" + status + "]");
                        addCityActivityViewModel.hideProgressBar();
                        if (status != null) {
                            //the documentation is unavailable for status code.
                            int statusCode = status.getStatusCode();
                        }

                        if (!Util.isGpsEnable(AddCity.this)) {
                            Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Please turn on the gps", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Something went wrong", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                addCityActivityViewModel.checkIfPlaceIsValid(place.getName().toString());


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    chekErrorGetCurrentLatLong();
                } else {

                    Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Please turn on location permission from setting", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    if (sharedPreferenceDataManager != null) {// TODO: 02/07/2016
//                        sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_PERMISSION_FINE_LOCATION, false);
//                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * check the GPS error and other error if any present
     * and then get the current location and its weather
     */
    private void chekErrorGetCurrentLatLong() {
        if (Util.isNetworkAvailable(AddCity.this) && Util.isGpsEnable(AddCity.this)) {
            getCurrentLatLongAddCity();
        } else {
            if (!Util.isGpsEnable(AddCity.this) && !Util.isNetworkAvailable(AddCity.this)) {
                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Please turn on Gps as well as Internet connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (!Util.isGpsEnable(AddCity.this)) {
                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "Please turn on Gps", Snackbar.LENGTH_LONG);
                snackbar.show();

            } else if (!Util.isNetworkAvailable(AddCity.this)) {
                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "No connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    public void onCityAddedError(int errorFlag, String message) {
        String errorMesg = "";
        errorMesg = getErrorString(errorFlag, message, errorMesg);
        Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, errorMesg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /***
     * get the error string corresponding to the error code
     *
     * @param errorFlag
     * @param message
     * @param errorMesg
     * @return
     */
    private String getErrorString(int errorFlag, String message, String errorMesg) {
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

    @Override
    public void onCityAdded(RealmResults<CityRealm> cityRealms, SharedPreferenceDataManager sharedPreferenceDataManager) {
        CitiesAdapter adapter = (CitiesAdapter) activityAddCityBinding.cities.getAdapter();
        if (adapter != null) {
            adapter.setCityRealms(cityRealms);
        }
    }

    @Override
    public void OnCitySelectedFromAdapter(CityRealm cityRealm) {
        finish();
    }

    @Override
    public void OnCityDeleted(final CityRealm cityRealm) {
        AlertDialog alertDialog = new AlertDialog.Builder(AddCity.this)
                .setMessage(AddCity.this.getResources().getString(R.string.cofirmation_city_delete_messege) + " " + cityRealm.getName())
                .setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.where(CityRealm.class).equalTo("id", cityRealm.getId()).findFirst().deleteFromRealm();
                        realm.commitTransaction();

                    }
                })
                .setNegativeButton(R.string.no_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        alertDialog.show();

    }

}
