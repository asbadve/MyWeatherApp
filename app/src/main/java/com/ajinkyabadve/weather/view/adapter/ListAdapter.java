package com.ajinkyabadve.weather.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.databinding.ListItemBinding;
import com.ajinkyabadve.weather.databinding.ListItemTodayBinding;
import com.ajinkyabadve.weather.model.realm.ListRealm;
import com.ajinkyabadve.weather.viewmodel.ItemListViewModel;

import io.realm.RealmList;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmList<ListRealm> list;
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemBinding listItemBinding;
        ListItemTodayBinding listItemTodayBinding;
        if (viewType == VIEW_TYPE_TODAY) {
            listItemTodayBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.list_item_today,
                    parent,
                    false);
            return new TodayItemListViewHolder(listItemTodayBinding);
        } else {
            listItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.list_item,
                    parent,
                    false);
            return new ItemListViewHolder(listItemBinding);

        }


    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==VIEW_TYPE_TODAY) {
            TodayItemListViewHolder itemListViewHolder = (TodayItemListViewHolder) holder;
            itemListViewHolder.bindList(list.get(position));
        } else {
            ItemListViewHolder itemListViewHolder = (ItemListViewHolder) holder;
            itemListViewHolder.bindList(list.get(position));

        }
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


    public static class TodayItemListViewHolder extends RecyclerView.ViewHolder {
        private final ListItemTodayBinding listItemBinding;

        public TodayItemListViewHolder(ListItemTodayBinding listItemBinding) {
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
