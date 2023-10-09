package com.orbaic.miner;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView hamBurgIcon;
    View header;

    CircularImageView profileIcon;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationViewId);
        header = navigationView.getHeaderView(0);
        hamBurgIcon = findViewById(R.id.hamBurgMenuId);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileIcon = findViewById(R.id.profileIcon);
        setNavigationDrawerMenu();
        setBottomNavigationMenu();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        loadFragment( new Home());

        if (getIntent().hasExtra("referBy")) {
            String name = getIntent().getStringExtra("name");
            String referBy = getIntent().getStringExtra("referBy");
            Log.e("addIntoReferTeam", "name: "+ name);
            Log.e("addIntoReferTeam", "referBy: "+ referBy);
            if (referBy != null && !referBy.isEmpty()) {
                addIntoReferTeam(name, referBy);
            }

        }

    }

    private void addIntoReferTeam(String name, String referredBy) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();
        Query referredUsersQuery = databaseRef.child("users")
                .orderByChild("referral")
                .equalTo(referredBy);

        referredUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = "";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.e("addIntoReferTeam", "userSnapshot: "+ userSnapshot);
                    userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String miningStartTime = "-1";
                    if (userSnapshot.child("miningStartTime").exists()) {
                        miningStartTime = userSnapshot.child("miningStartTime").getValue(String.class);
                    }
                    Log.e("getMyTeam", "miningStartTime: "+miningStartTime );

                }

                Log.e("addIntoReferTeam", "userId: "+ userId);
                if (userId != null && !userId.isEmpty()) {
                    DatabaseReference referralRef = database.getReference("referralUser");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("status", "-1");
                    referralRef.child(userId).child(mAuth.getUid().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("addIntoReferTeam", "task: isSuccessful");
                            }
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void setBottomNavigationMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.homeMenu:
                            loadFragment( new Home());
                            return true;
                        case R.id.walletMenu:
                            loadFragment( new WalletFragment());
                            return true;
                        case R.id.supportMenu:
                            loadFragment( new Home());
                            return true;
                        case R.id.teamMenu:
                            loadFragment( new TeamFragment());
                            return true;
                    }
                    return false;
                });
    }

    private void setNavigationDrawerMenu() {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home){
                    loadFragment( new Home());
                } else if (id == R.id.miningRules){
                    loadFragment( new MiningRules());
                }else if (id == R.id.referralTeam){
                    loadFragment( new TeamReferral());
                }else if (id == R.id.whitePaper){
                    loadFragment( new WhitePaper());
                }else if (id == R.id.faq){
                    loadFragment( new faq());
                }else if (id == R.id.profile){
                    loadFragment( new Profile());
                }else if (id == R.id.logout){
                    logout();
                    //loadFragment( new Withdrawal());
                }else {
                    Toast.makeText(MainActivity2.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        hamBurgIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }

        });

        findViewById(R.id.holdeLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(MainActivity2.this, LoginLayout.class));
        Toast.makeText(MainActivity2.this, "Logout your Account", Toast.LENGTH_SHORT).show();
        clearAppData();
    }

    private void loadFragment(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            if (getSupportFragmentManager().getBackStackEntryCount()>1){
                getSupportFragmentManager().popBackStack();
            }else if (isTaskRoot()){
                backClick();
            }else {
                super.onBackPressed();
            }
        }

    }

    private void backClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}