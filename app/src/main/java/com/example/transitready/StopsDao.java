package com.example.transitready;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StopsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStops(List<Stops> ListStops);
    @Update
    void updateStops(Stops stops);
    @Delete
    void deleteStops(Stops stops);
    @Query("Select * from Stops where stop_id = :stop_id")
    Stops getStopsById(String stop_id);
    @Query("Select * from Stops where stop_code = :stop_code")
    Stops getStopByCode(int stop_code);
    @Query("Select count(*) from Stops")
    Integer getNumOfStops();
    @Query("Select * from Stops")
    List<Stops> getAllStops();
}
