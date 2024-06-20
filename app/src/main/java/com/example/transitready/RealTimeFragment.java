package com.example.transitready;

import static com.example.transitready.BusTimeAdapter.addDelay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class RealTimeFragment extends Fragment {
    private AppDatabase db;
    RecyclerView busTimes;
    TextView stopCode, stopName;
    private int stop_code;
    private String stop_name;
    private BusTimeAdapter busTimeAdapter;
    private RequestQueue mQueue;
    List<Trips> tripsList;
    List<Routes> routesList;
    List<String> realTimes;
    private Handler handler = new Handler();
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            jsonParse();
            busTimeAdapter = new BusTimeAdapter(getActivity(), tripsList, routesList, realTimes, db);
            busTimes.setAdapter(busTimeAdapter);
            handler.postDelayed(this, 30000); // Re-post runnable every 30 seconds
        }
    };

    public RealTimeFragment() {
    }

    public RealTimeFragment(int stop_code, String stop_name) {
        this.stop_code = stop_code;
        this.stop_name = stop_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "StopInfo")
                .allowMainThreadQueries()
                .build();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_real_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        busTimes = view.findViewById(R.id.busTimes);
        busTimes.setLayoutManager(new LinearLayoutManager(getActivity()));

        stopCode = view.findViewById(R.id.stopCode);
        stopName = view.findViewById(R.id.stopName);

        stopCode.setText(String.valueOf(stop_code));
        stopName.setText(stop_name);

        tripsList = new ArrayList<>();
        routesList = new ArrayList<>();
        realTimes = new ArrayList<>();

        // Get request for gtfsr real-time info api
        mQueue = Volley.newRequestQueue(getActivity());

        jsonParse();
        stopSchedule();

        busTimeAdapter = new BusTimeAdapter(getActivity(), tripsList, routesList, realTimes, db);
        busTimes.setAdapter(busTimeAdapter);
    }

    /*
    * Function parses json response and Assign tripsList, routesList, and realTimes the necessary data to be used
    * in the BusTimeAdapter.
    * RealTime is calculated here using the calender library and delay which is in seconds retrieved from the api.
    * The startDate retrieved from the api is combined with the scheduled time retrieved from StopTimes table.
    * And then compared with current time to filter out old data.
    *
    * */
    public void jsonParse() {
        String url = "https://api.nationaltransport.ie/gtfsr/v2/TripUpdates?format=json";

        Stops stop = db.stopsDao().getStopByCode(stop_code);
        String stop_id = stop.getStop_id();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("API Response", response.toString());
                        try {
                            JSONArray entity = response.getJSONArray("entity");
                            List<Trips> filteredTripsList = new ArrayList<>();
                            List<Routes> filteredRoutesList = new ArrayList<>();
                            List<String> filteredRealTime = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                            Date currentDate = new Date();

                            for(int i = 0; i < entity.length(); i++) {
                                JSONObject tripUpdate = entity.getJSONObject(i).getJSONObject("trip_update");

                                if (tripUpdate.has("stop_time_update")) {
                                    JSONArray stopTimeUpdate = tripUpdate.getJSONArray("stop_time_update");

                                    for (int j = 0; j < stopTimeUpdate.length(); j++) {
                                        JSONObject stop = stopTimeUpdate.getJSONObject(j);
                                        String stopId = stop.getString("stop_id");

                                        if (stopId.equals(stop_id)) {
                                            String tripId = tripUpdate.getJSONObject("trip").getString("trip_id");
                                            String startDate = tripUpdate.getJSONObject("trip").getString("start_date");
                                            int delayInSeconds = 0;

                                            if (stop.has("departure") && stop.getJSONObject("departure").has("delay")) {
                                                delayInSeconds = stop.getJSONObject("departure").getInt("delay");
                                            }

                                            // Retrieving Scheduled time to add delay and make the real-time
                                            StopTimes stopTime = db.stopTimesDao().getStopTimesById(tripId, stop_id);
                                            if (stopTime == null || stopTime.getDeparture_time() == null) {
                                                Log.e("RealTimeFragment", "StopTime not found or departure time is null for tripId: " + tripId + ", stopId: " + stop_id);
                                                continue; // Skips this iteration if stopTime or departure_time is null
                                            }
                                            String scheduledTime = stopTime.getDeparture_time().substring(0, 5);

                                            String realTime = BusTimeAdapter.addDelay(delayInSeconds, scheduledTime);

                                            // Checks if time passes midnight and changes to next day
                                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                            Date originalTime = timeFormat.parse(scheduledTime);
                                            Date delayedDate = timeFormat.parse(realTime);

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                            calendar.setTime(originalTime);

                                            // Checks if delayed time is before the original time meaning a day change
                                            if (delayedDate.before(originalTime)) {
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                                Date startDateParsed = dateFormat.parse(startDate);
                                                calendar.setTime(startDateParsed);
                                                calendar.add(Calendar.DATE, 1); // Increments the date
                                                startDate = dateFormat.format(calendar.getTime());
                                            }

                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
                                            dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                            Date realTimeDate = dateTimeFormat.parse(startDate + " " + realTime);

                                            Log.d("Debug", "Current Date: " + currentDate);
                                            Log.d("Debug", "Real Time: " + realTimeDate);


                                            if (realTime != null && realTimeDate.after(currentDate)) {
                                                Trips trip = db.tripsDao().getTripsById(tripId);
                                                String route_id = trip.getRoute_id();

                                                filteredRealTime.add(sdf.format(realTimeDate));
                                                filteredTripsList.add(trip);
                                                filteredRoutesList.add(db.routesDao().getRoutesById(route_id));
                                            }


                                        }
                                    }
                                }
                            }
                            // Updates the adapter with filtered lists
                            tripsList.clear();
                            tripsList.addAll(filteredTripsList);
                            routesList.clear();
                            routesList.addAll(filteredRoutesList);
                            realTimes.clear();
                            realTimes.addAll(filteredRealTime);


                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.e("JSON Parsing Error", "Error parsing JSON", e);
                        } finally {
                            // Updates adapter after lists are updated
                            if (busTimeAdapter != null) {
                                busTimeAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("API Error", "Error in API response", error);
            }
        }) {
            // https://stackoverflow.com/questions/17049473/how-to-set-custom-header-in-volley-request
            @Override // Api key for accessing the api
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-key", "f4976edf43af4d2cba615e63a28833a8");
                return headers;
            }
        };
        mQueue.add(request);
    }

    public void stopSchedule() {
        Stops stop = db.stopsDao().getStopByCode(stop_code);
        String stop_id = stop.getStop_id();

        //StopTimes stopTimes = db.stopTimesDao().
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String currentTime = sdf.format(now.getTime());

        // 4 hours ahead time
        now.add(Calendar.HOUR, 4);
        String fourHoursAheadTime = sdf.format(now.getTime());

        // Fetching stop times from db
        List<StopTimes> stopTimesList = db.stopTimesDao().getStopTimesForStopId(stop_id, currentTime, fourHoursAheadTime);

        // Filtering out duplicates and adding to realTimes
        for (StopTimes stopTime : stopTimesList) {
            String departureTime = stopTime.getDeparture_time().substring(0, 5);

            if (!tripsList.contains(stopTime.getTrip_id()) && !realTimes.contains(departureTime)) {
                realTimes.add(departureTime);
            }
        }

        // Notifies the adapter of update
        if (busTimeAdapter != null) {
            busTimeAdapter.notifyDataSetChanged();
        }
    }

}