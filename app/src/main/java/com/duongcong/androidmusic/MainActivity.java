package com.duongcong.androidmusic;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duongcong.androidmusic.Account.AccountFragment;
import com.duongcong.androidmusic.Account.LoginActivity;
import com.duongcong.androidmusic.Browse.BrowseFragment;
import com.duongcong.androidmusic.DBHelper.RecentLocalDBHelper;
import com.duongcong.androidmusic.Discovery.DiscoveryFragment;
import com.duongcong.androidmusic.Home.Album.AlbumFragment;
import com.duongcong.androidmusic.Home.Download.DownloadFragment;
import com.duongcong.androidmusic.Home.HomeFragment;
import com.duongcong.androidmusic.Home.Songs.SongsFragment;
import com.duongcong.androidmusic.Home.Upload.UploadFragment;
import com.duongcong.androidmusic.Home.playlist.SongOnPlaylistFragment;
import com.duongcong.androidmusic.Home.songondevice.SongOnDeviceFragment;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.Play.PlayMusicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {


    // Firebase
    // public FirebaseDatabase database = FirebaseDatabase.getInstance();;
    // public DatabaseReference myFirebaseRef = database.getReference();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public String appExternalStorageFolder;
    public String appExternalStoragePath;

    public BottomNavigationView navigation;

    // PERMISSION CODE
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_ACCESS_MEDIA_LOCATION = 2;

    // Fragment
    List<Fragment> fragmentList = new ArrayList<>();

    public HomeFragment homeFragment                     = new HomeFragment();
    protected DiscoveryFragment discoveryFragment           = new DiscoveryFragment();
    protected BrowseFragment browseFragment                 = new BrowseFragment();
    public AccountFragment accountFragment               = new AccountFragment();

    public PlayMusicFragment playMusicFragment              = new PlayMusicFragment();
    public SongMenuOptionFragment songMenuOptionFragment    = new SongMenuOptionFragment();

    // Home page
    public SongsFragment songsFragment                      = new SongsFragment();
    public UploadFragment uploadFragment                    = new UploadFragment();
    public DownloadFragment downloadFragment                = new DownloadFragment();
    public AlbumFragment albumFragment                      = new AlbumFragment();
    public SongOnDeviceFragment songOnDeviceFragment        = new SongOnDeviceFragment();
    public SongOnPlaylistFragment songOnPlaylistFragment    = new SongOnPlaylistFragment();


    // Bar showing the currently playing song
    public ConstraintLayout songPlayingBar;
    public ObjectAnimator animImgSongPlaying;
    protected ImageView imgSongPlayingBar;
    public ImageButton btnPlayBar, btnPauseBar;



    //
    public FloatingActionButton btnPlayPlaylist;
    //
    public MediaPlayer mediaPlayer;
    public ArrayList<SongModel> currentPlaylist;
    public SongModel songPlaying;
    public int songPlayingIndexInCurrentPlaylist;


    //
    public void playNewPlaylist(ArrayList<SongModel> playlist, int position){

        currentPlaylist = playlist;
        setSong(playlist, position);
        songPlayingIndexInCurrentPlaylist = position;

        displayPlayMusicFragment();

        // Event when finish play a song
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // If shuffle mode is on
                if(Objects.equals(playMusicFragment.shuffleMode, "YES")){
                    // Set new index of song is random number from 0 to size of playlist - 1
                    songPlayingIndexInCurrentPlaylist = ThreadLocalRandom.current().nextInt(0,playlist.size()-1);
                    setSong(playlist, songPlayingIndexInCurrentPlaylist);
                    playMusicFragment.playSong();

                }
                // Else if song is last in list
                else if(songPlayingIndexInCurrentPlaylist == playlist.size()-1){
                    // If repeat all
                    if(Objects.equals(playMusicFragment.repeatMode, "ALL")){
                        songPlayingIndexInCurrentPlaylist = 0;
                        setSong(playlist, songPlayingIndexInCurrentPlaylist);
                        playMusicFragment.playSong();
                    }
                    //
                }
                else {
                    songPlayingIndexInCurrentPlaylist++;
                    setSong(playlist, songPlayingIndexInCurrentPlaylist);
                    playMusicFragment.playSong();
                }
            }

        });

    }

    // Set song data for send to play music fragment to play
    public void setSong(ArrayList<SongModel> playlist, int position){
        // Get song
        songPlaying = playlist.get(position);
        // Reset
        if(mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }

        // Set artist if artist is <unknown>
        String songName = songPlaying.getName();
        String songArtist = songPlaying.getArtist();
        if(songArtist == null || songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }

        // Set bundle to send song data to play music fragment
        Bundle bundle = new Bundle();
        bundle.putString("playType", "new play");
        bundle.putString("songId", songPlaying.getId());
        bundle.putString("songName",songPlaying.getName());
        bundle.putString("songPath", songPlaying.getPath());
        bundle.putString("songArtist",songArtist);
        bundle.putString("songAlbum",songPlaying.getAlbum());
        bundle.putString("songCategory",songPlaying.getCategory());
        bundle.putString("songDuration",songPlaying.getDuration());
        bundle.putString("songType", songPlaying.getType());
        playMusicFragment.setArguments(bundle);
        // Add song to recent list
        RecentLocalDBHelper recentDB = new RecentLocalDBHelper(this);
        recentDB.addOrUpdateSongInRecent(songPlaying);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set app folder in sdcard
        appExternalStorageFolder = "Music";
        appExternalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/" + appExternalStorageFolder;
        File appExternalStorageDir = new File(appExternalStoragePath);

        // Create app folder in sdcard if not exists
        if(!appExternalStorageDir.exists() || !appExternalStorageDir.isDirectory()) {
            appExternalStorageDir.mkdir();
        }

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //
        btnPlayPlaylist = findViewById(R.id.floating_btn_play_playlist);
        btnPlayPlaylist.setVisibility(View.INVISIBLE);


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

        // Home page
        fragmentList.add(songsFragment);
        fragmentList.add(uploadFragment);
        fragmentList.add(downloadFragment);
        fragmentList.add(albumFragment);
        fragmentList.add(songOnDeviceFragment);
        fragmentList.add(songOnPlaylistFragment);

        displayFragment(homeFragment);

        // Request permission
        requestPermission();

        // Create media player
        mediaPlayer = new MediaPlayer();

        // SONG PLAYING BAR
        songPlayingBar.setVisibility(View.GONE);
        // Animation rotate image
        animImgSongPlaying = ObjectAnimator.ofFloat(imgSongPlayingBar, "rotation", 0, 360);
        animImgSongPlaying.setDuration(15000);
        animImgSongPlaying.setRepeatCount(Animation.INFINITE);
        animImgSongPlaying.setRepeatMode(ObjectAnimator.RESTART);

        // When click in bar, open play music fragment
        songPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("playType", "resume play");
                if(songOnDeviceFragment.isVisible() || songOnPlaylistFragment.isVisible() || songsFragment.isVisible()){
                    bundle.putString("isInPlaylist", "yes");
                }
                else {
                    bundle.putString("isInPlaylist", "no");
                }

                playMusicFragment.setArguments(bundle);
                displayPlayMusicFragment();
            }
        });

        // Event click button in song playing bar
        // Button play
        btnPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicFragment.mediaPlayer.start();
                playMusicFragment.setResumePlayState();
            }
        });

        // Button pause
        btnPauseBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicFragment.mediaPlayer.pause();
                playMusicFragment.setPausePlayState();
            }
        });

    }

    // Get fragment index in list
    public int getFragmentIndex(Fragment fragment) {
        int index = -1;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragment.hashCode() == fragmentList.get(i).hashCode()){
                return i;
            }
        }
        return index;
    }

    // Display a fragment
    public void displayFragment(Fragment fragment) {
        int index = getFragmentIndex(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager;
        if (fragment.isAdded()) {
            transaction.show(fragment);
            if(!homeFragment.isVisible() && !discoveryFragment.isVisible() && !browseFragment.isVisible() && !accountFragment.isVisible()){
                transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
            }
        } else {
            transaction.add(R.id.fragment_container, fragment);
        }
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragmentList.get(i).isAdded() && i != index) {
                transaction.hide(fragmentList.get(i));
            }
        }
        transaction.addToBackStack(null).commit();
    }

    // Display play music fragment
    public void displayPlayMusicFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!playMusicFragment.isAdded()) {
            transaction.add(R.id.fragment_container, playMusicFragment);
        }
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(playMusicFragment).addToBackStack(null);
        transaction.commit();
    }

    // Hide play music fragment
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

    // Display menu option fragment
    public void displaySongMenuOptionFragment(HashMap<String, String> songHashMap) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        Set<String> keySet = songHashMap.keySet();
        for (String key : keySet) {
            bundle.putString(key, songHashMap.get(key));
        }

        songMenuOptionFragment.setArguments(bundle);
        if (!songMenuOptionFragment.isAdded()) {
            transaction.add(R.id.fragment_container, songMenuOptionFragment);
        }
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down).show(songMenuOptionFragment).addToBackStack(null);
        transaction.commit();



    }

    // Display menu option fragment
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
                    if(mediaPlayer.isPlaying()){
                        songPlayingBar.setVisibility(View.VISIBLE);
                    }
                }
            }, 100);
        }
    }


    // Event click bottom navigation items
    private NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.page_home: // Home
                    displayFragment(homeFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_discovery: // Discovery
                    displayFragment(discoveryFragment);
                    break;
                case R.id.page_browse: // Browse
                    displayFragment(browseFragment);
                    navigation.setVisibility(View.VISIBLE);
                    break;
                case R.id.page_account: // Account
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
