package com.example.transitready;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouritesDao {
    @Insert
    void insertFavourites(Favourites favourites);

    @Delete
    void deleteFavourites(Favourites favourites);

    @Query("Select fav_stop from Favourites")
    List<Integer> getFavourites();

    @Query("Select Exists (select 1 from Favourites where fav_stop = :stop_code)")
    boolean checkIfFavourite(int stop_code);


}
