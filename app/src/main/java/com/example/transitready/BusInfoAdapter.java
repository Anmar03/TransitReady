package com.example.transitready;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class BusInfoAdapter extends RecyclerView.Adapter<BusInfoAdapter.BusInfoViewHolder> {

    private ArrayList<String> busNames;
    private Context context;

    public BusInfoAdapter(Context context, ArrayList<String> names) {
        this.context = context;
        this.busNames = names;
    }

    @NonNull
    @Override
    public BusInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_list_item, parent, false);
        return new BusInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusInfoViewHolder holder, int position) {
        String busName = busNames.get(position);

        // Set the text of the TextView in each item
        holder.busNameTextView.setText(busName);
    }

    @Override
    public int getItemCount() {
        return busNames.size();
    }

    static class BusInfoViewHolder extends RecyclerView.ViewHolder {
        TextView busNameTextView;
        BusInfoViewHolder(View itemView) {
            super(itemView);
            busNameTextView = itemView.findViewById(R.id.bus_name);
        }
    }

}