package com.example.transitready;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StopTimesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStopTimes(List<StopTimes> stopTimesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStopTime(StopTimes stopTimesList);
    @Update
    void updateStopTimes(StopTimes stopTimes);
    @Delete
    void deleteStopTimes(StopTimes stopTimes);

    @Query("Select * from StopTimes where trip_id = :trip_id AND stop_id = :stop_id")
    StopTimes getStopTimesById(String trip_id, String stop_id);

    @Query("SELECT * FROM stopTimes WHERE stop_id = :stopId AND departure_time >= :currentTime AND departure_time <= :fourHoursAheadTime ORDER BY departure_time ASC")
    List<StopTimes> getStopTimesForStopId(String stopId, String currentTime, String fourHoursAheadTime);
}
