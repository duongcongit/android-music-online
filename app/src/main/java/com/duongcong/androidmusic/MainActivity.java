package com.duongcong.androidmusic;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    BottomNavigationView navigation;

    protected HomeFragment homeFragment                     = new HomeFragment();
    protected DiscoveryFragment discoveryFragment           = new DiscoveryFragment();
    protected BrowseFragment browseFragment                 = new BrowseFragment();
    protected AccountFragment accountFragment               = new AccountFragment();
    protected PlayMusicFragment playMusicActivity           = new PlayMusicFragment();
    protected SongOnDeviceFragment songOnDeviceFragment     = new SongOnDeviceFragment();

    List<Fragment> fragmentList = new ArrayList<>();

    protected ConstraintLayout songPlayingBar;
    protected ObjectAnimator animImgSongPlaying;
    protected ImageView imgSongPlayingBar;
    protected ImageButton btnPlayBar, btnPauseBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        songPlayingBar      = findViewById(R.id.song_playing_bar);
        imgSongPlayingBar   = findViewById(R.id.imageView_song_playing);
        btnPlayBar          = findViewById(R.id.btn_play_bar);
        btnPauseBar         = findViewById(R.id.btn_pause_bar);

        fragmentList.add(homeFragment);
        fragmentList.add(discoveryFragment);
        fragmentList.add(browseFragment);
        fragmentList.add(accountFragment);
        fragmentList.add(playMusicActivity);
        fragmentList.add(songOnDeviceFragment);

        displayFragment(homeFragment);

        songPlayingBar.setVisibility(View.GONE);
        // Animation rotate image
        animImgSongPlaying = ObjectAnimator.ofFloat(imgSongPlayingBar, "rotation", 0, 360);
        animImgSongPlaying.setDuration(15000);
        animImgSongPlaying.setRepeatCount(Animation.INFINITE);
        animImgSongPlaying.setRepeatMode(ObjectAnimator.RESTART);

        songPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("playType", "resume play");
                playMusicActivity.setArguments(bundle);
                displayPlayMusicFragment();
            }
        });

        btnPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicActivity.mediaPlayer.start();
                playMusicActivity.setResumePlayState();
            }
        });

        btnPauseBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicActivity.mediaPlayer.pause();
                playMusicActivity.setPausePlayState();
            }
        });



    }

    //
    public int getFragmentIndex(Fragment fragment) {
        int index = -1;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragment.hashCode() == fragmentList.get(i).hashCode()){
                return i;
            }
        }
        return index;
    }


    //
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


    //
    public void displayPlayMusicFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!playMusicActivity.isAdded()) {
            transaction.add(R.id.fragment_container, playMusicActivity);
        }
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(playMusicActivity);
        transaction.commit();
    }

    //
    public void hidePlayMusicFragment() {
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
                    Bundle bundle = new Bundle();
                    bundle.putString("playType", "resume play");
                    playMusicActivity.setArguments(bundle);
                    displayPlayMusicFragment();
                    navigation.setVisibility(View.GONE);
                    break;
                case R.id.page_browse:
                    displayFragment(browseFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_account:
                    //Check user firebase
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if(firebaseUser!=null){
                        displayFragment(accountFragment);
                        navigation.setVisibility(View.VISIBLE);
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
            }

            return true;
        }
    };




}