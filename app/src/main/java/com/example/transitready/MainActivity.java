package com.example.transitready;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;


import com.example.transitready.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements BusInfoAdapter.AdapterCallback, MapFragment.MarkerClickCallback {
    /////// START OF REFERENCE
    // https://youtu.be/jOFLmKMOcK0?si=4sBXIpiYBgC2qzmP
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
            } else if (item.getItemId() == R.id.map) {
                replaceFragment(new MapFragment());
            }

            return true;
        });


    }
    @Override
    public void onFragmentChange(Fragment fragment) {
        replaceFragment(fragment);
    }

    @Override
    public void onMarkerClick(Fragment fragment) { replaceFragment(fragment); }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
/////// END OF REFERENCE