package com.techkets.balajiconfectioners.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.techkets.balajiconfectioners.R;

public class MainDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    private int i = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("BalajiConfectioners", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //default fragment calling Home Fragment-------------------------
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.drawerContainer, new HomeFragment(), "HomeFragment").commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (i != 0) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.drawerContainer, new HomeFragment()).commit();
            i = 0;

        } else {
            super.onBackPressed();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shopping_cart) {
            startActivity(new Intent(MainDrawer.this, OrderList.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_home:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.drawerContainer, new HomeFragment(), "HomeFragment").addToBackStack(null).commit();
                i = 0;
                break;
            case R.id.nav_category:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.drawerContainer, new CategoryFragment()).commit();
                i = 1;
                break;
            case R.id.nav_item:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.drawerContainer, new ItemFragment()).commit();
                i = 1;
                break;
            case R.id.logout:
                if (currentuser != null && firebaseAuth != null) {
                    editor.putString("Username", null);
                    editor.putString("Password", null);
                    editor.commit();
                    firebaseAuth.signOut();
                    startActivity(new Intent(MainDrawer.this, SignInActivity.class));
                    finish();
                }
                break;
            case R.id.nav_addUser:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.drawerContainer, new AddUserFragment()).commit();
                i = 1;
                break;
            case R.id.userDetails:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.drawerContainer, new UserDetails()).commit();
                i = 1;
                break;
            case R.id.nav_confirm_orders:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.drawerContainer, new ConfirmOrders()).commit();
                i = 1;
                break;
            case R.id.aboutDeveloper:
                startActivity(new Intent(MainDrawer.this,AboutDeveloper.class));


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
