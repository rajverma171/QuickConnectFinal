package com.prodev.quickconnect;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // loading default frag on frameLayout
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new RecentFragment()).commit();

        // xml into object
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if ( item.getItemId() == R.id.frag_contact) {
                    selectedFragment = new ContactsFragment();
                } else {
                    selectedFragment = new RecentFragment();
                }
//
//                // Switch case to handle different fragments
//                switch (item.getItemId()) {
//                    case R.id.frag_recent:
//                        selectedFragment = new RecentFragment();
//                        break;
//                    case R.id.frag_contact:
//                        selectedFragment = new ContactsFragment();
//                        break;
//                }

                // If a fragment is selected, replace the existing fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;  // Return true to display the selected item

            }

        });
    }
}