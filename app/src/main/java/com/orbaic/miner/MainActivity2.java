package com.orbaic.miner;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

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
                            loadFragment( new Home());
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