package com.techkets.balajiconfectioners.view.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.techkets.balajiconfectioners.R;
import com.techkets.balajiconfectioners.model.UserFetch;
import com.techkets.balajiconfectioners.view.SignInActivity;

public class UserView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private int i = 0;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view2);
        sharedPreferences=getSharedPreferences("BalajiConfectioners", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.user_drawer);
        NavigationView navigationView = findViewById(R.id.user_nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_drawerContainer, new UserHome()).commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shopping_cart) {
            startActivity(new Intent(UserView.this, UserCart.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.user_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (i != 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.user_drawerContainer, new UserHome()).commit();
            i = 0;

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_user_home:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.user_drawerContainer, new UserHome()).commit();
                i = 0;
                break;
            case R.id.nav_user_profile:
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.user_drawerContainer, new ProfileUser()).commit();
                i = 1;
                break;
            case R.id.nav_user_orders:
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.user_drawerContainer, new PreviousOrder()).commit();
                i = 1;
                break;
            case R.id.user_logout:
                if (firebaseUser != null) {
                    editor.putString("Username",null);
                    editor.putString("Password",null);
                    editor.commit();
                    firebaseAuth.signOut();
                    startActivity(new Intent(UserView.this, SignInActivity.class));
                    finish();
                }
                break;
        }


        DrawerLayout drawer = findViewById(R.id.user_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
