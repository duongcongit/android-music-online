package com.duongcong.androidmusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationBarView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.page_home:
                    System.out.println("1");

                    return true;
                case R.id.page_discovery:
                    System.out.println("2");

                    return true;
                case R.id.page_browse:
                    Intent switchActivityIntentBrowse = new Intent(getApplicationContext(), PlayMusicActivity.class);
                    startActivity(switchActivityIntentBrowse);

                    return true;
                case R.id.page_account:
                    Intent switchActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(switchActivityIntent);

                    return true;
            }

            return false;
        }
    };


}