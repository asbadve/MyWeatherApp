package com.ajinkyabadve.weather;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ajinkyabadve.weather.databinding.ItemCityBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.viewmodel.ItemCityViewModel;

import io.realm.RealmResults;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder> {
    RealmResults<CityRealm> cityRealms;

    public CitiesAdapter() {
//        this.cityRealms; = Collections.emptyList();
    }

    public void setCityRealms(RealmResults<CityRealm> cityRealms) {
        this.cityRealms = cityRealms;
    }

    @Override
    public CitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCityBinding itemCityBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_city,
                parent,
                false);
        return new CitiesViewHolder(itemCityBinding);
    }

    @Override
    public void onBindViewHolder(CitiesViewHolder holder, int position) {
        holder.bindCity(cityRealms.get(position));
    }

    @Override
    public int getItemCount() {
        if (cityRealms == null)
            return 0;
        return cityRealms.size();
    }

    public static class CitiesViewHolder extends RecyclerView.ViewHolder {
        final ItemCityBinding itemCityBinding;

        public CitiesViewHolder(ItemCityBinding itemCityBinding) {
            super(itemCityBinding.rootView);
            this.itemCityBinding = itemCityBinding;
        }

        void bindCity(CityRealm cityRealm) {
            if (itemCityBinding.getViewModel() == null) {
                itemCityBinding.setViewModel(new ItemCityViewModel(itemView.getContext(), cityRealm));
            } else {
                itemCityBinding.getViewModel().setCityRealm(cityRealm);
            }
        }
    }
}
