package com.example.transitready;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private RecyclerView favStopsRecyclerView;
    private BusInfoAdapter busInfoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favStopsRecyclerView = view.findViewById(R.id.fav_stops);
        favStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<String> busInfoArray = new ArrayList<>();

        busInfoArray.add("Stop 1140, Woodbrook Park, Knocklyon");
        busInfoArray.add("Stop 946, Woodstown, Knocklyon");
        busInfoArray.add("Stop 301, Eden Quay, Dublin");
        busInfoArray.add("Stop 7143 Castegate Way, Adamstown");

        busInfoAdapter = new BusInfoAdapter(getActivity(), busInfoArray);
        favStopsRecyclerView.setAdapter(busInfoAdapter);
    }
}
