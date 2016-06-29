package com.ajinkyabadve.weather.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ListItemBinding;
import com.ajinkyabadve.weather.model.realm.ListRealm;
import com.ajinkyabadve.weather.viewmodel.ItemListViewModel;

import io.realm.RealmList;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemListViewHolder> {
    private RealmList<ListRealm> list;

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemBinding listItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item,
                parent,
                false);
        return new ItemListViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int position) {
        holder.bindList(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void setList(RealmList<ListRealm> list) {
        this.list = list;
    }

    public static class ItemListViewHolder extends RecyclerView.ViewHolder {


        private final ListItemBinding listItemBinding;

        public ItemListViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.rootView);
            this.listItemBinding = listItemBinding;
        }


        void bindList(ListRealm listRealm) {
            if (listItemBinding.getViewModel() == null) {
                listItemBinding.setViewModel(new ItemListViewModel(itemView.getContext(), listRealm));
            } else {
                listItemBinding.getViewModel().setListRealm(listRealm);
            }
        }
    }
}
