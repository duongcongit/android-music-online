package com.duongcong.androidmusic;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    protected HomeFragment homeFragment = new HomeFragment();
    protected DiscoveryFragment discoveryFragment = new DiscoveryFragment();
    protected BrowseFragment browseFragment = new BrowseFragment();
    protected AccountFragment accountFragment = new AccountFragment();
    protected PlayMusicFragment playMusicActivity = new PlayMusicFragment();

    List<Fragment> fragmentList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentList.add(homeFragment);
        fragmentList.add(discoveryFragment);
        fragmentList.add(browseFragment);
        fragmentList.add(accountFragment);
        fragmentList.add(playMusicActivity);

        displayFragment(homeFragment);

    }

    public int getFragmentIndex(Fragment fragment) {
        int index = -1;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragment.hashCode() == fragmentList.get(i).hashCode()){
                return i;
            }
        }
        return index;
    }

    public void displayFragment(Fragment fragment) {
        int index = getFragmentIndex(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment);
        }
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragmentList.get(i).isAdded() && i != index) {
                transaction.hide(fragmentList.get(i));
            }
        }
        transaction.commit();
    }


    public void displayPlayMusicFragment() {
        int index = getFragmentIndex(playMusicActivity);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (playMusicActivity.isAdded()) {
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(playMusicActivity);
        } else {
            transaction.add(R.id.fragment_container, playMusicActivity);
        }

        transaction.commit();
    }

    public void hidePlayMusicFragment() {
        int index = getFragmentIndex(playMusicActivity);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (playMusicActivity.isResumed()) {
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).hide(playMusicActivity);
            transaction.commit();
        }

    }


    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationBarView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.page_home:
                    displayFragment(homeFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_discovery:
                    displayPlayMusicFragment();
                    navigation.setVisibility(View.GONE);
                    break;
                case R.id.page_browse:
                    displayFragment(browseFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_account:
                    displayFragment(accountFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
            }

            return true;
        }
    };




}