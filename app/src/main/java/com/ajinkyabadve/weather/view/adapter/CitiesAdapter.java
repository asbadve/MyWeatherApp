package com.ajinkyabadve.weather.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ItemCityBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.viewmodel.ItemCityViewModel;

import io.realm.RealmResults;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder> {
    private int defaultId = 0;
    RealmResults<CityRealm> cityRealms;
    private OnCitySelected onCitySelected;

    public interface OnItemClick {
        void OnCitySelected(CityRealm cityRealm);
    }

    public interface OnCitySelected {
        void OnCitySelectedFromAdapter(CityRealm cityRealm);
    }

    public CitiesAdapter(OnCitySelected OnCitySelected, int defaultId) {
        this.defaultId = defaultId;
        this.onCitySelected = OnCitySelected;
    }

    public void setCityRealms(RealmResults<CityRealm> cityRealms, int defaultId) {
        this.defaultId = defaultId;
        this.cityRealms = cityRealms;
        notifyDataSetChanged();
    }

    @Override
    public CitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCityBinding itemCityBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_city,
                parent,
                false);
        return new CitiesViewHolder(itemCityBinding, onCitySelected);
    }

    @Override
    public void onBindViewHolder(CitiesViewHolder holder, int position) {
        if (defaultId == cityRealms.get(position).getId()) {
            holder.itemCityBinding.rootView.setBackgroundColor(ContextCompat.getColor(holder.itemCityBinding.rootView.getContext(), R.color.colorAccentFaint));
        } else {
            holder.itemCityBinding.rootView.setBackgroundColor(ContextCompat.getColor(holder.itemCityBinding.rootView.getContext(), android.R.color.white));
        }
        holder.bindCity(cityRealms.get(position));
    }

    @Override
    public int getItemCount() {
        if (cityRealms == null)
            return 0;
        return cityRealms.size();
    }

    public static class CitiesViewHolder extends RecyclerView.ViewHolder implements OnItemClick {
        final ItemCityBinding itemCityBinding;
        private OnCitySelected onCitySelected;

        public CitiesViewHolder(ItemCityBinding itemCityBinding, OnCitySelected onCitySelected) {
            super(itemCityBinding.rootView);
            this.itemCityBinding = itemCityBinding;
            this.onCitySelected = onCitySelected;
        }

        void bindCity(CityRealm cityRealm) {
            if (itemCityBinding.getViewModel() == null) {
                itemCityBinding.setViewModel(new ItemCityViewModel(itemView.getContext(), cityRealm, this));
            } else {
                itemCityBinding.getViewModel().setCityRealm(cityRealm);
            }
        }

        @Override
        public void OnCitySelected(CityRealm cityRealm) {
            onCitySelected.OnCitySelectedFromAdapter(cityRealm);
        }
    }

    private RealmResults<CityRealm> setDefaultCity(RealmResults<CityRealm> cityRealms, SharedPreferenceDataManager preferenceDataManager) {
        int cityIdPreference = preferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
        for (int i = 0; i < cityRealms.size(); i++) {
            if (cityRealms.get(i).getId() == cityIdPreference) {
                cityRealms.get(i).setDefault(true);
            } else {
                cityRealms.get(i).setDefault(false);
            }
        }
        return cityRealms;
    }
}
