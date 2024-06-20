package com.example.transitready;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BusTimeAdapter extends RecyclerView.Adapter<BusTimeAdapter.BusTimeViewHolder> {
    private Context context;
    private List<Trips> tripsList;
    private List<Routes> routesList;
    private String stop_id;
    private List<String> realTimes;
    private AppDatabase db;

    public BusTimeAdapter(Context context, List<Trips> tripsList, List<Routes> routesList, List<String> realTime, AppDatabase db) {
        this.context = context;
        this.tripsList = tripsList;
        this.routesList = routesList;
        this.realTimes = realTime;
        this.db = db;
    }

    @NonNull
    @Override
    public BusTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_list_item, parent, false);
        return new BusTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusTimeAdapter.BusTimeViewHolder holder, int position) {
        Trips trip = tripsList.get(position);
        Routes route = routesList.get(position);
        String realTime = realTimes.get(position);

        holder.busNum.setText(route.getRoute_no());
        holder.busDirection.setText(trip.trip_headsign);
        holder.busDeparture.setText(realTime);

    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset
        return tripsList.size();
    }

    class BusTimeViewHolder extends RecyclerView.ViewHolder {
        TextView busNum;
        TextView busDirection;
        TextView busDeparture;
        BusTimeViewHolder(View itemView) {
            super(itemView);

            busNum = itemView.findViewById(R.id.busNum);
            busDirection = itemView.findViewById(R.id.busDirection);
            busDeparture = itemView.findViewById(R.id.busDeparture);

        }
    }

    public static String addDelay(int delay, String scheduledTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = sdf.parse(scheduledTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Adding / subtracting the delay in seconds
            calendar.add(Calendar.SECOND, delay);

            // Formatting the result back to HH:mm
            return sdf.format(calendar.getTime());
        } catch (Exception e) {

            e.printStackTrace();
            return null; // Return null as an error indicator
        }
    }


}
