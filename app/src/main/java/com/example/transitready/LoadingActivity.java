package com.example.transitready;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.room.Room;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoadingActivity extends Activity {
    private ProgressBar progressBar;
    private AppDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading); // Set your loading screen layout
        progressBar = findViewById(R.id.loadingProgressBar);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        if (isDataLoaded()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the loading activity
        } else {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "StopInfo")
                    .allowMainThreadQueries()
                    .build();
            loadData();
        }
    }

    private boolean isDataLoaded() {
        return sharedPreferences.getBoolean("isDataLoaded", false);
    }


    private void loadData() {
        LoadData loadData = new LoadData(getApplicationContext(), db);
        loadData.loadAllData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDataLoaded, this::onDataLoadFailed);
    }

    private void onDataLoaded(Boolean success) {
        if (success) {
            // Data loaded successfully, start the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the loading activity
        }
    }

    private void onDataLoadFailed(Throwable throwable) {
        throwable.printStackTrace();
        // rip
    }
}
