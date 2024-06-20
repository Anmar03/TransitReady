package com.example.transitready;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusInfoAdapter extends RecyclerView.Adapter<BusInfoAdapter.BusInfoViewHolder> {
    private AppDatabase db;
    private List<Stops> stopNames;
    private Context context;
    private AdapterCallback callback;

    public interface AdapterCallback {
        void onFragmentChange(Fragment fragment);
    }

    public void setCallback(AdapterCallback callback) {
        this.callback = callback;
    }


    public BusInfoAdapter(Context context, List<Stops> stopsList, AppDatabase db) {
        this.context = context;
        this.stopNames = stopsList;
        this.db = db;
    }

    public void setFilteredList(List<Stops> filteredList) {
        this.stopNames = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BusInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_list_item, parent, false);
        return new BusInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusInfoViewHolder holder, int position) {
        Stops stop = stopNames.get(position);
        String busInfo = "Stop " + stop.getStop_code() + ", " + stop.getStop_name();

        // Check if the stop is in favorites
        boolean isFavorite = checkIfFavorite(stop.getStop_code());
        holder.favourite.setBackgroundResource(isFavorite ?
                R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);

        // Set the text of the TextView in each item
        holder.busNameTextView.setText(busInfo);

        // Reset background color to default
        holder.itemHolder.setBackgroundColor(Color.WHITE);

        // Set specific background color when an item is clicked
        holder.itemHolder.setOnClickListener(v -> {
            if(callback != null) {
                int stopCode = stop.getStop_code();
                String stopName = stop.getStop_name();

                Fragment newFragment = new RealTimeFragment(stopCode, stopName);
                callback.onFragmentChange(newFragment);
            }
            holder.itemHolder.setBackgroundColor(Color.parseColor("#BABABA"));
        });
    }

    private boolean checkIfFavorite(int stopCode) {
        // Check database if stopCode exists in the favorites table
        return db.favouritesDao().checkIfFavourite(stopCode);
    }

    @Override
    public int getItemCount() {
        return stopNames.size();
    }

    class BusInfoViewHolder extends RecyclerView.ViewHolder {
        TextView busNameTextView;
        ConstraintLayout itemHolder;
        Button favourite;
        BusInfoViewHolder(View itemView) {
            super(itemView);
            busNameTextView = itemView.findViewById(R.id.bus_name);
            favourite = itemView.findViewById(R.id.favourite);
            itemHolder = itemView.findViewById(R.id.itemHolder);
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Toggle favorite status and update UI
                        Stops stop = stopNames.get(position);
                        int stopCode = stop.getStop_code();

                        toggleFavorite(stopCode);
                    }
                }
            });
        }

        private void toggleFavorite(int stopCode) {
            // Check if it's already a favorite
            boolean isFavorite = checkIfFavorite(stopCode);
            Favourites favourite = new Favourites(stopCode);
            if (isFavorite) {
                // Remove from favorites
                db.favouritesDao().deleteFavourites(favourite);
            } else {
                // Add to favorites
                db.favouritesDao().insertFavourites(favourite);
            }
            notifyItemChanged(getAdapterPosition());
        }


    }

}