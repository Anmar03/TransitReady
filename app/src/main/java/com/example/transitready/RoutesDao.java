package com.example.transitready;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoutesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoutes(List<Routes> routesList);
    @Update
    void updateRoutes(Routes routes);
    @Delete
    void deleteRoutes(Routes routes);
    @Query("Select * from Routes where route_id = :route_id")
    Routes getRoutesById(String route_id);
}
