package com.duongcong.androidmusic;

import android.animation.ObjectAnimator;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.duongcong.androidmusic.account.AccountFragment;
import com.duongcong.androidmusic.browse.BrowseFragment;
import com.duongcong.androidmusic.discovery.DiscoveryFragment;
import com.duongcong.androidmusic.home.HomeFragment;
import com.duongcong.androidmusic.home.playlist.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.home.songondevice.SongOnDeviceFragment;
import com.duongcong.androidmusic.play.PlayMusicFragment;
import com.duongcong.androidmusic.home.playlist.SongOnPlaylistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView navigation;

    protected HomeFragment homeFragment                     = new HomeFragment();
    protected DiscoveryFragment discoveryFragment           = new DiscoveryFragment();
    protected BrowseFragment browseFragment                 = new BrowseFragment();
    protected AccountFragment accountFragment               = new AccountFragment();

    public PlayMusicFragment playMusicFragment              = new PlayMusicFragment();
    public SongMenuOptionFragment songMenuOptionFragment    = new SongMenuOptionFragment();

    public SongOnDeviceFragment songOnDeviceFragment        = new SongOnDeviceFragment();
    public SongOnPlaylistFragment songOnPlaylistFragment    = new SongOnPlaylistFragment();

    List<Fragment> fragmentList = new ArrayList<>();

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

        AudioModel song1 = new AudioModel();
        song1.setaName(songName);
        song1.setaPath(songPath);

        AudioModel song2 = new AudioModel();
        song2.setaName(songName2);
        song2.setaPath(songPath2);

        AudioModel song3 = new AudioModel();
        song3.setaName(songName3);
        song3.setaPath(songPath3);

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
            mediaPlayer.setDataSource(songPlaying.song.getaPath());
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
                        mediaPlayer.setDataSource(playlist.get(idxPlaying).song.getaPath());
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

        // fragmentList.add(playMusicFragment);
        // fragmentList.add(songMenuOptionFragment);

        fragmentList.add(songOnDeviceFragment);
        fragmentList.add(songOnPlaylistFragment);

        displayFragment(homeFragment);



        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(this);

        // mydb.createPlaylist("Playlist 1", "local");
        // mydb.insertDt("Playlist 1", "Song 4", "Artist", "Album", "/storage/emulated/0/Music/CUT K391 Alan Walker  Ahrix  End of Time Lyrics.mp3");
        // mydb.updatePlaylist("Playlist 3", "Playlist 1", "local");


        // List<String> a =  mydb.getPlaylist();
        // System.out.println(a.get());

        /* for (int i=0; i<a.size(); i++){
            System.out.println(a.get(i));

            List<AudioModel> listSong= mydb.getPlaylistData(a.get(i));
            for(int j=0; j<listSong.size(); j++){
                System.out.println("-----" + listSong.get(j).getaName());
            }
        } */

        // List<AudioModel> listSong= mydb.getPlaylistData("Playlist 1");

        // System.out.println(listSong.get(0).getaName());

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

    public void showMenuOption(){




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
                    // displayFragment(accountFragment);
                    // navigation.setVisibility(View.VISIBLE);
                    plays();
                    break;
            }

            return true;
        }
    };


}

class SongInPlayList {
    int index;
    AudioModel song;

    public SongInPlayList(int index, AudioModel song){
        this.index = index;
        this.song = song;
    }

}