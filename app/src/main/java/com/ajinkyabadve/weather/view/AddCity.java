package com.ajinkyabadve.weather.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ajinkyabadve.weather.view.adapter.CitiesAdapter;
import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ActivityAddCityBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.viewmodel.AddCityActivityViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import io.realm.RealmResults;

public class AddCity extends AppCompatActivity implements AddCityActivityViewModel.ActivityModelCommunicationListener {
    AddCityActivityViewModel addCityActivityViewModel;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = AddCity.class.getSimpleName();
    ActivityAddCityBinding activityAddCityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddCityBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_city);
        addCityActivityViewModel = new AddCityActivityViewModel(AddCity.this, this);
        activityAddCityBinding.setAddcityciewcodel(addCityActivityViewModel);
        setSupportActionBar(activityAddCityBinding.toolbar);
        setUpRecyclerView(activityAddCityBinding.cities);
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        CitiesAdapter citiesAdapter = new CitiesAdapter();
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
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(typeFilter)
                                .build(AddCity.this);


                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, "onOptionsItemSelected:GooglePlayServicesRepairableException ", e);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                // TODO: 29/06/2016 check if this place is in openweather api or not
                addCityActivityViewModel.checkIfPlaceIsValid(place);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    @Override
    public void onCityAddedError(int errorFlag) {
        switch (errorFlag) {
            // TODO: 29/06/2016  show accurate error flags
            case AddCityActivityViewModel.FLAG_CITY_ALREADY_PRESENT:
                Snackbar snackbar = Snackbar.make(activityAddCityBinding.coordinateLayout, "This city is already added", Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
            case AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG:
                break;
            case AddCityActivityViewModel.FLAG_CITY_WEATHER_NOT_AVAILABLE:
                break;

        }
    }

    @Override
    public void onCityAdded(RealmResults<CityRealm> cityRealms) {
        CitiesAdapter adapter =
                (CitiesAdapter) activityAddCityBinding.cities.getAdapter();
        if (adapter != null) {
            adapter.setCityRealms(cityRealms);
            adapter.notifyDataSetChanged();
        }
    }
}