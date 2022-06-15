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

    private HomeFragment homeFragment = new HomeFragment();
    private DiscoveryFragment discoveryFragment = new DiscoveryFragment();
    private BrowseFragment browseFragment = new BrowseFragment();
    private AccountFragment accountFragment = new AccountFragment();
    private PlayMusicFragment playMusicActivity = new PlayMusicFragment();


    List<Fragment> fragmentList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);


        displayFragment(homeFragment);


        fragmentList.add(homeFragment);
        fragmentList.add(discoveryFragment);
        fragmentList.add(browseFragment);
        fragmentList.add(accountFragment);
        fragmentList.add(playMusicActivity);



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
                    displayFragment(playMusicActivity);
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