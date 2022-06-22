package com.duongcong.androidmusic;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.duongcong.androidmusic.Account.AccountFragment;
import com.duongcong.androidmusic.Account.LoginActivity;
import com.duongcong.androidmusic.Browse.BrowseFragment;
import com.duongcong.androidmusic.Discovery.DiscoveryFragment;
import com.duongcong.androidmusic.Home.HomeFragment;
import com.duongcong.androidmusic.Home.playlist.SongOnPlaylistFragment;
import com.duongcong.androidmusic.Home.songondevice.SongOnDeviceFragment;
import com.duongcong.androidmusic.Model.LocalSongModel;
import com.duongcong.androidmusic.Play.PlayMusicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;

    public BottomNavigationView navigation;

    // PERMISSION CODE
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_ACCESS_MEDIA_LOCATION = 2;

    // Fragment
    List<Fragment> fragmentList = new ArrayList<>();

    protected HomeFragment homeFragment                     = new HomeFragment();
    protected DiscoveryFragment discoveryFragment           = new DiscoveryFragment();
    protected BrowseFragment browseFragment                 = new BrowseFragment();
    protected AccountFragment accountFragment               = new AccountFragment();

    public PlayMusicFragment playMusicFragment              = new PlayMusicFragment();
    public SongMenuOptionFragment songMenuOptionFragment    = new SongMenuOptionFragment();

    public SongOnDeviceFragment songOnDeviceFragment        = new SongOnDeviceFragment();
    public SongOnPlaylistFragment songOnPlaylistFragment    = new SongOnPlaylistFragment();


    // Bar showing the currently playing song
    public ConstraintLayout songPlayingBar;
    public ObjectAnimator animImgSongPlaying;
    protected ImageView imgSongPlayingBar;
    public ImageButton btnPlayBar, btnPauseBar;


    protected List<SongInPlayList> playlist;
    protected SongInPlayList songPlaying;




    public void plays(){
        // Song 1
        String songName     = "CUT K391 Alan Walker  Ahrix  End of Time Lyrics";
        int indexPl         = 1;
        String artist       = "A1";
        String album        = "A1";
        String songPath     = "/storage/emulated/0/Music/CUT K391 Alan Walker  Ahrix  End of Time Lyrics.mp3";

        // Song 2
        String songName2     = "CUT Faded异域 Jacla remix  Naxsy Douyin Version";
        int indexPl2         = 2;
        String artist2       = "A2";
        String album2        = "A2";
        String songPath2     = "/storage/emulated/0/Music/CUT Faded异域 Jacla remix  Naxsy Douyin Version.mp3";

        // Song 2
        String songName3     = "CUT Move up remix hay nhất";
        int indexPl3         = 3;
        String artist3       = "A3";
        String album3        = "A3";
        String songPath3     = "/storage/emulated/0/Music/CUT Move up remix hay nhất.mp3";

        LocalSongModel song1 = new LocalSongModel();
        song1.setName(songName);
        song1.setPath(songPath);

        LocalSongModel song2 = new LocalSongModel();
        song2.setName(songName2);
        song2.setPath(songPath2);

        LocalSongModel song3 = new LocalSongModel();
        song3.setName(songName3);
        song3.setPath(songPath3);

        SongInPlayList s1 = new SongInPlayList(1, song1);
        SongInPlayList s2 = new SongInPlayList(2, song2);
        SongInPlayList s3 = new SongInPlayList(3, song3);

        playlist = new ArrayList<>();
        playlist.add(s1);
        playlist.add(s2);
        playlist.add(s3);

        MediaPlayer mediaPlayer = new MediaPlayer();

        songPlaying = new SongInPlayList(1, song1);
        // Get song from path and play
        try {
            mediaPlayer.setDataSource(songPlaying.song.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();

        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                int idxPlaying = songPlaying.index;
                if(idxPlaying == playlist.size()){

                }
                if(idxPlaying < playlist.size()){
                    try {
                        mediaPlayer.setDataSource(playlist.get(idxPlaying).song.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        songPlaying = playlist.get(idxPlaying);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation bar
        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        // Bar showing the currently playing song
        songPlayingBar      = findViewById(R.id.song_playing_bar);
        imgSongPlayingBar   = findViewById(R.id.imageView_song_playing);
        btnPlayBar          = findViewById(R.id.btn_play_bar);
        btnPauseBar         = findViewById(R.id.btn_pause_bar);

        // Fragments
        fragmentList.add(homeFragment);
        fragmentList.add(discoveryFragment);
        fragmentList.add(browseFragment);
        fragmentList.add(accountFragment);

        // fragmentList.add(playMusicFragment);
        // fragmentList.add(songMenuOptionFragment);

        fragmentList.add(songOnDeviceFragment);
        fragmentList.add(songOnPlaylistFragment);

        displayFragment(homeFragment);

        // Request permission
        requestPermission();



        // Create media player
        playMusicFragment.mediaPlayer = new MediaPlayer();

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
                playMusicFragment.setArguments(bundle);
                displayPlayMusicFragment();
            }
        });

        btnPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicFragment.mediaPlayer.start();
                playMusicFragment.setResumePlayState();
            }
        });

        btnPauseBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicFragment.mediaPlayer.pause();
                playMusicFragment.setPausePlayState();
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
        if (!playMusicFragment.isAdded()) {
            transaction.add(R.id.fragment_container, playMusicFragment);
        }
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(playMusicFragment).addToBackStack(null).commit();
        //transaction.commit();
    }

    //
    public void hidePlayMusicFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (playMusicFragment.isResumed()) {
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).hide(playMusicFragment);
            transaction.remove(playMusicFragment);
            transaction.commit();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigation.setVisibility(View.VISIBLE);
                    songPlayingBar.setVisibility(View.VISIBLE);
                }
            }, 200);

        }
    }

    //
    public void displaySongMenuOptionFragment(String type, String playlistName, String songName, String songArtist, String songAlbum, String songPath) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();

        bundle.putString("type", type);
        bundle.putString("playlistName", playlistName);
        bundle.putString("songPath", songPath);
        bundle.putString("songName",songName);
        bundle.putString("songArtist",songArtist);
        bundle.putString("songAlbum",songAlbum);

        songMenuOptionFragment.setArguments(bundle);

        if (!songMenuOptionFragment.isAdded()) {
            transaction.add(R.id.fragment_container, songMenuOptionFragment);
        }
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(songMenuOptionFragment).addToBackStack(null);
        transaction.commit();



    }


    //
    public void hideSongMenuOptionFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (songMenuOptionFragment.isResumed()) {
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).hide(songMenuOptionFragment);
            transaction.remove(songMenuOptionFragment);
            transaction.commit();
            // Show bottom navigation bar and playing song bar
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigation.setVisibility(View.VISIBLE);
                    if(playMusicFragment.mediaPlayer.isPlaying()){
                        songPlayingBar.setVisibility(View.VISIBLE);
                    }
                }
            }, 100);
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
                    playMusicFragment.setArguments(bundle);
                    displayPlayMusicFragment();
                    navigation.setVisibility(View.GONE);
                    break;
                case R.id.page_browse:
                    displayFragment(browseFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_account:
                    // Check user firebase
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if(firebaseUser!=null){
                        displayFragment(accountFragment);
                        navigation.setVisibility(View.VISIBLE);
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
            }

            return true;
        }
    };



    // Request permission
    public void requestPermission(){

        // Permission read external storage
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
        // Permission write external storage
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        // Permission access media location
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, PERMISSION_REQUEST_ACCESS_MEDIA_LOCATION);
        }

    }

    // Result require permission
    /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, R.string.camera_permission_granted,
                                Snackbar.LENGTH_SHORT)
                        .show();
                startCamera();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.camera_permission_denied,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    } */


}

class SongInPlayList {
    int index;
    LocalSongModel song;

    public SongInPlayList(int index, LocalSongModel song){
        this.index = index;
        this.song = song;
    }

}