package com.example.transitready;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;



@Entity(
        tableName = "Trips",
        foreignKeys = @ForeignKey(
            entity = Routes.class,
            parentColumns = "route_id",
            childColumns = "route_id",
            onDelete = ForeignKey.CASCADE // Defined the onDelete behavior if a referenced row is deleted
        ),
        indices = {@Index("route_id")}
)
public class Trips {
    @ColumnInfo(name = "route_id")
    String route_id;
    @ColumnInfo(name = "trip_id")
    @PrimaryKey()
    @NonNull
    String trip_id;

    String trip_headsign;

    @Ignore
    public Trips() {
    }

    public Trips(String route_id, String trip_id, String trip_headsign) {
        this.route_id = route_id;
        this.trip_id = trip_id;
        this.trip_headsign = trip_headsign;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public void setTrip_headsign(String trip_headsign) {
        this.trip_headsign = trip_headsign;
    }
}
