package com.example.transitready;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "StopTimes", primaryKeys = {"trip_id", "stop_id"},
        foreignKeys = {
                @ForeignKey(
                entity = Trips.class,
                parentColumns = "trip_id",
                childColumns = "trip_id",
                onDelete = ForeignKey.CASCADE // Defined the onDelete behavior if referenced row is deleted
                ),
                @ForeignKey(
                entity = Stops.class,
                parentColumns = "stop_id",
                childColumns = "stop_id",
                onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("trip_id"), @Index("stop_id")}

)
public class StopTimes {
    @ColumnInfo(name = "trip_id")
    @NonNull
    String trip_id;
    @ColumnInfo(name = "stop_id")
    @NonNull
    String stop_id;
    @ColumnInfo(name = "departure_time")
    String departure_time;

    @Ignore
    public StopTimes() {
    }

    public StopTimes(String trip_id, String stop_id, String departure_time) {
        this.trip_id = trip_id;
        this.stop_id = stop_id;
        this.departure_time = departure_time;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }
}
