package com.ajinkyabadve.weather.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ItemCityBinding;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.viewmodel.ItemCityViewModel;

import io.realm.RealmResults;

/**
 * Recycler adapter or cities
 * Created by Ajinkya on 29/06/2016.
 */
public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder> {
    RealmResults<CityRealm> cityRealms;
    private OnCityOperation onCityOperation;

    /***
     * onclick listener
     */
    public interface OnItemClick {
        /**
         * call when city get selected
         *
         * @param cityRealm selected city object
         */
        void OnCitySelected(CityRealm cityRealm);

        /**
         * call when city get deleted
         *
         * @param cityRealm deleted city object
         */
        void OnCityDeletedFromAdapter(CityRealm cityRealm);

    }

    /**
     * listener for the interaction of adapter and its view model
     */
    public interface OnCityOperation {
        /***
         * call when city get selected
         *
         * @param cityRealm selected city
         */
        void OnCitySelectedFromAdapter(CityRealm cityRealm);

        /**
         * call when city get deleted
         *
         * @param cityRealm
         */
        void OnCityDeleted(CityRealm cityRealm);
    }

    /**
     * @param OnCityOperation
     */
    public CitiesAdapter(OnCityOperation OnCityOperation) {
        this.onCityOperation = OnCityOperation;
    }

    /***
     * set realm result list to adapter
     *
     * @param cityRealms RealmResult objects of cities
     */
    public void setCityRealms(RealmResults<CityRealm> cityRealms) {
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
        return new CitiesViewHolder(itemCityBinding, onCityOperation);
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

    public static class CitiesViewHolder extends RecyclerView.ViewHolder implements OnItemClick {
        final ItemCityBinding itemCityBinding;
        private OnCityOperation onCityOperation;

        /**
         * @param itemCityBinding
         * @param onCityOperation
         */
        public CitiesViewHolder(ItemCityBinding itemCityBinding, OnCityOperation onCityOperation) {
            super(itemCityBinding.rootView);
            this.itemCityBinding = itemCityBinding;
            this.onCityOperation = onCityOperation;
        }

        /**
         * bind the view model and data
         *
         * @param cityRealm city object to be bind
         */

        void bindCity(CityRealm cityRealm) {
            if (itemCityBinding.getViewModel() == null) {
                itemCityBinding.setViewModel(new ItemCityViewModel(itemView.getContext(), cityRealm, this));
            } else {
                itemCityBinding.getViewModel().setCityRealm(cityRealm);
            }
        }

        @Override
        public void OnCitySelected(CityRealm cityRealm) {
            onCityOperation.OnCitySelectedFromAdapter(cityRealm);
        }

        @Override
        public void OnCityDeletedFromAdapter(CityRealm cityRealm) {
            onCityOperation.OnCityDeleted(cityRealm);
        }
    }

}
