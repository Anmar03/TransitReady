package com.example.transitready;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class HomeFragment extends Fragment implements BusInfoAdapter.AdapterCallback{
    private AppDatabase db;
    private RecyclerView favStopsRecyclerView;
    private BusInfoAdapter busInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "StopInfo")
                .allowMainThreadQueries()
                .build();

        // Inflates the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favStopsRecyclerView = view.findViewById(R.id.busTimes);
        favStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Stops> busInfoArray = new ArrayList<>();

        // Fetches favorite stop codes
        List<Integer> favoriteStopCodes = db.favouritesDao().getFavourites();

        // Fetches Stops for each fav stop code
        for (int stopCode : favoriteStopCodes) {
            Stops stop = db.stopsDao().getStopByCode(stopCode);
            if (stop != null) {
                busInfoArray.add(stop);
            }
        }

        if(busInfoArray.isEmpty()) {
            Toast.makeText(getActivity(), "Add Stops by searching then", LENGTH_SHORT).show();
        }

        busInfoAdapter = new BusInfoAdapter(getActivity(), busInfoArray, db);
        busInfoAdapter.setCallback(this);
        favStopsRecyclerView.setAdapter(busInfoAdapter);
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        // Performs the fragment transaction or communicates with the parent Activity
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
