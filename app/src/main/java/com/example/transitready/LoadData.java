package com.example.transitready;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class LoadData {
    private final Context context;
    private final AppDatabase db;

    public LoadData(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;

    }

    public Single<List<Routes>> loadRoutes() {
        return Single.fromCallable(() -> {
            InputStream is = context.getAssets().open("routes.csv");
            InputStreamReader isr = new InputStreamReader(is);

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();
            CSVParser parser = new CSVParser(isr, format);
            List<Routes> routesList = new ArrayList<>();
            for (CSVRecord record : parser) {
                String routeId = record.get("route_id");
                String routeNo = record.get("route_short_name");
                String routeName = record.get("route_long_name");

                routesList.add(new Routes(routeId, routeNo, routeName));
            }
            parser.close();
            return routesList;
        }).subscribeOn(Schedulers.io());
    }

    public Single<List<Stops>> loadStops() {
        return Single.fromCallable(() -> {
            InputStream is = context.getAssets().open("stops.csv");
            InputStreamReader isr = new InputStreamReader(is);

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(isr, format);
            List<Stops> buffer = new ArrayList<>();
            final int batchSize = 20000;

            for (CSVRecord record : parser) {
                String stop_id = record.get("stop_id");
                int stop_code = record.get("stop_code").isEmpty() ? 0 : Integer.parseInt(record.get("stop_code"));
                String stop_name = record.get("stop_name");
                double stop_lat = Double.parseDouble(record.get("stop_lat"));
                double stop_lon = Double.parseDouble(record.get("stop_lon"));

                buffer.add(new Stops(stop_id, stop_code, stop_name, stop_lat, stop_lon));

                if (buffer.size() >= batchSize) {
                    db.stopsDao().insertStops(buffer);
                    buffer.clear();
                }
            }

            // Inserts the remaining records
            if (!buffer.isEmpty()) {
                db.stopsDao().insertStops(buffer);
            }

            parser.close();
            return buffer; // Returns the list of stops
        }).subscribeOn(Schedulers.io());
    }


    public Single<Boolean> loadStopTimes() {
        return Single.fromCallable(() -> {
            InputStream is = context.getAssets().open("stop_times_dublin.csv");
            InputStreamReader isr = new InputStreamReader(is);
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();
            CSVParser parser = new CSVParser(isr, format);

            List<StopTimes> buffer = new ArrayList<>();
            final int batchSize = 30000;

            for (CSVRecord record : parser) {
                String trip_id = record.get("trip_id");
                String stop_id = record.get("stop_id");
                String departure_time = record.get("departure_time");

                buffer.add(new StopTimes(trip_id, stop_id, departure_time));

                if (buffer.size() >= batchSize) {
                    db.stopTimesDao().insertStopTimes(buffer);
                    buffer.clear();
                }
            }

            // Inserts the remaining records
            if (!buffer.isEmpty()) {
                db.stopTimesDao().insertStopTimes(buffer);
            }

            parser.close();
            return true; // Indicates successful completion
        }).subscribeOn(Schedulers.io());
    }



    public Single<List<Trips>> loadTrips() {
        return Single.fromCallable(() -> {
            InputStream is = context.getAssets().open("trips.csv");
            InputStreamReader isr = new InputStreamReader(is);

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser parser = new CSVParser(isr, format);
            List<Trips> buffer = new ArrayList<>();
            final int batchSize = 20000; // can be adjusted based on memory

            for (CSVRecord record : parser) {
                String route_id = record.get("route_id");
                String trip_id = record.get("trip_id");
                String trip_headsign = record.get("trip_headsign");

                buffer.add(new Trips(route_id, trip_id, trip_headsign));

                if (buffer.size() >= batchSize) {
                    db.tripsDao().insertTrips(buffer);
                    buffer.clear();
                }
            }

            // Inserts the remaining records
            if (!buffer.isEmpty()) {
                db.tripsDao().insertTrips(buffer);
            }

            parser.close();
            return buffer; // Returns the list of trips
        }).subscribeOn(Schedulers.io());
    }


    public Single<Boolean> loadAllData() {
        return loadRoutes()
                .flatMap(routes -> {
                    db.routesDao().insertRoutes(routes);
                    return loadStops();
                })
                .flatMap(stops -> {
                    db.stopsDao().insertStops(stops);
                    return loadTrips();
                })
                .flatMap(trips -> {
                    db.tripsDao().insertTrips(trips);
                    return loadStopTimes();
                })
                .flatMap(result -> {
                    if (result) {
                        Log.d("DATA", "Data has loaded");
                        setDataLoaded();  // Set the flag indicating data is loaded
                        return Single.just(true); // successful completion
                    } else {
                        return Single.error(new Exception("Error loading StopTimes"));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private void setDataLoaded() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDataLoaded", true);
        editor.apply();
    }

}
