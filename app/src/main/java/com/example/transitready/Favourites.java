package com.example.transitready;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favourites")
public class Favourites {
    @PrimaryKey()
    private int fav_stop; // stores the stop_code of the favourite stop

    @Ignore
    public Favourites() {
    }

    public Favourites(int fav_stop) {
        this.fav_stop = fav_stop;
    }

    public int getFav_stop() {
        return fav_stop;
    }

    public void setFav_stop(int fav_stop) {
        this.fav_stop = fav_stop;
    }
}
