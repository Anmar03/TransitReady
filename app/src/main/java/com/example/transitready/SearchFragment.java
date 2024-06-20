package com.example.transitready;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements BusInfoAdapter.AdapterCallback {
    private AppDatabase db;
    private SearchView searchView;
    private Button hideKeyboard;
    private RecyclerView StopsRecyclerView;
    private BusInfoAdapter busInfoAdapter;
    private List<Stops> busInfoArray = new ArrayList<>();
    Stops stop, stop1, stop2, stop3, stop4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "StopInfo")
                .allowMainThreadQueries()
                .build();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        hideKeyboard = view.findViewById(R.id.hideKeyboard);
        StopsRecyclerView = view.findViewById(R.id.searchedStops);
        StopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Access the query hint TextView
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        // Setting the text size for the query hint
        int textSizeInSp = 12; 
        searchAutoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);

        // Set the input type to numbers
        searchView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        // OnClickListener for hideKeyboard button
        hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide keyboard when button is clicked
                hideKeyboard();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });


        // displays some stops to fill out the search fragment at start
        stop = db.stopsDao().getStopsById("8230DB001140");
        stop1 = db.stopsDao().getStopsById("8220DB000301");
        stop2 = db.stopsDao().getStopsById("8230DB004340");
        stop3 = db.stopsDao().getStopsById("8230DB008091");
        stop4 = db.stopsDao().getStopsById("8240DB002960");

        busInfoArray.add(stop);
        busInfoArray.add(stop1);
        busInfoArray.add(stop2);
        busInfoArray.add(stop3);
        busInfoArray.add(stop4);


        busInfoAdapter = new BusInfoAdapter(getActivity(), busInfoArray, db);
        busInfoAdapter.setCallback(this);
        StopsRecyclerView.setAdapter(busInfoAdapter);
    }

    ////// START OF REFERENCE
    // https://youtu.be/tQ7V7iBg5zE?si=IGEnbTbfR_aN-ioV **Was adjusted to suit the app's requirements**
    private void filterList(String input) {
        List<Stops> filteredList = new ArrayList<>();
        int code;


        try {
            code = Integer.parseInt(input);
            Stops stop = db.stopsDao().getStopByCode(code);

            if (stop != null) {
                filteredList.add(stop);
            }

            if (filteredList.isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "No Stops found", Toast.LENGTH_SHORT);
                // Setting the position of the Toast
                toast.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 20);

                toast.show();

            } else {
                busInfoAdapter.setFilteredList(filteredList);
            }
        } catch (NumberFormatException e) {
            // exception if input is not a valid integer
        }
    }
    ////// END OF REFERENCE


    // Function to hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }
    @Override
    public void onFragmentChange(Fragment fragment) {
        // Perform the fragment transaction or communicate with the parent Activity
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}