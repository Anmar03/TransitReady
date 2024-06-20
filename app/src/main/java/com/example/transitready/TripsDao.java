package com.example.transitready;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrips(List<Trips> tripsList);
    @Update
    void updateTrips(Trips trips);
    @Delete
    void deleteTrips(Trips trips);

    @Query("Select * from Trips where trip_id = :trip_id")
    Trips getTripsById(String trip_id);
}
