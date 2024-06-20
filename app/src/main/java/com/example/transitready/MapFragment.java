package com.example.transitready;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private AppDatabase db;
    private MarkerClickCallback markerClickCallback;
    private List<Marker> allMarkers = new ArrayList<>();


    public interface MarkerClickCallback {
        void onMarkerClick(Fragment fragment);
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng dublin = new LatLng(53.349805, -6.26031);

            List<Stops> stopsList = db.stopsDao().getAllStops();

            // Iterates through all stops and adds a marker for each one
            for (Stops stop : stopsList) {
                LatLng stopLocation = new LatLng(stop.getStop_lat(), stop.getStop_lon());
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(stopLocation)
                        .title(stop.getStop_name())
                        .snippet("Stop Code: " + stop.getStop_code())
                );
                marker.setTag(stop);
                allMarkers.add(marker);
            }

            // click listener for marker to change to the realTimeFragment
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Stops clickedStop = (Stops) marker.getTag();
                    if (clickedStop != null) {
                        Fragment newFragment = new RealTimeFragment(clickedStop.getStop_code(), clickedStop.getStop_name());
                        markerClickCallback.onMarkerClick(newFragment);
                    } else {
                        Toast.makeText(getActivity(), "Stop info Not Found", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            // Makes markers only visible when zoomed to 14
            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    float zoom = googleMap.getCameraPosition().zoom;
                    // Marker will only be visible if the camera is zoomed in more
                    boolean showMarkers = zoom > 14;

                    for (Marker marker : allMarkers) {
                        marker.setVisible(showMarkers);
                    }
                }
            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin, 12));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "StopInfo")
                .allowMainThreadQueries()
                .build();

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (getActivity() instanceof MarkerClickCallback) {
            markerClickCallback = (MarkerClickCallback) getActivity();
        } else {
            throw new RuntimeException("Hosting activity must implement MarkerClickCallback");
        }
    }
}