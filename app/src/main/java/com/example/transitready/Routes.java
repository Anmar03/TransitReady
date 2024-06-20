package com.example.transitready;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Routes")
public class Routes {
    @ColumnInfo(name = "route_id")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    String route_id;
    @ColumnInfo(name = "route_no")
    String route_no;
    @ColumnInfo(name = "route_name")
    String route_name;

    @Ignore
    public Routes() {
    }

    public Routes(String route_id, String route_no, String route_name) {
        this.route_id = route_id;
        this.route_no = route_no;
        this.route_name = route_name;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getRoute_no() {
        return route_no;
    }

    public void setRoute_no(String route_no) {
        this.route_no = route_no;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }
}
