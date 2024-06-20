package com.example.transitready;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Stops.class, Routes.class, Trips.class, StopTimes.class, Favourites.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StopsDao stopsDao();
    public abstract RoutesDao routesDao();
    public abstract TripsDao tripsDao();
    public abstract StopTimesDao stopTimesDao();
    public abstract FavouritesDao favouritesDao();

}

