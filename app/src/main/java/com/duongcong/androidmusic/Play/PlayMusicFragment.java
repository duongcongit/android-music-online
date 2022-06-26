package com.duongcong.androidmusic.Play;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PlayMusicFragment extends Fragment {

    // View
    ImageView imgView;
    private ImageButton btn_forward, btn_next,btn_pause,btn_play,btn_repeat, btn_shuffle;
    private TextView txt_songName, txt_songArtist;

    // Song detail
    private String songId, songName, songPath, songImg, songAlbum, songArtist, songCategory, songDuration, songType ;
    private double startTime = 0;
    private double finalTime = 0;

    // Media player
    public MediaPlayer mediaPlayer;

    public String repeatMode    = "NO";
    public String shuffleMode   = "NO";

    //
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    // View song detail
    private SeekBar seekbar;
    private TextView txt_time_current, txt_max_time;

    public static int oneTimeOnly = 0;

    // Animation rotate image when playing
    private ObjectAnimator anim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout =  inflater.inflate(R.layout.fragment_play_music, container, false);
        // Hide bottom navigation bar
        ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
        // Hide playing song bar
        ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);
        //
        ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        return rootLayout;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // If hide play music screen
        if (hidden) {
            // Show bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.VISIBLE);
            Bundle bundle = this.getArguments();
            // If in a playlist song, display button play playlist
            if(!Objects.equals(bundle.getString("isInPlaylist"), "no")){
                ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
            }
        }
        // If display
        else {
            // Get song data and set player, views, mode,...
            // If just display song is playing and not play mew song, do nothing
            // Receive song info
            Bundle bundle = this.getArguments();
            if(bundle != null){
                // If is play new song, reset player and views
                if(Objects.equals(bundle.getString("playType"), "new play")){
                    playSong();
                }
                else {
                    // If just show song is playing, not reset
                    // Set view
                    setViewSongDetail(getView());
                    // Set start play state for view
                    setResumePlayState();
                }
            }

            // Hide bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mediaPlayer = ((MainActivity)getActivity()).mediaPlayer;

        // Textview
        txt_songName        = view.findViewById(R.id.txtView_songName);
        txt_songArtist      = view.findViewById(R.id.txtView_songArtist);
        txt_time_current    = (TextView)view.findViewById(R.id.txt_time_current);
        txt_max_time        = (TextView)view.findViewById(R.id.txt_max_time);

        imgView = (ImageView) view.findViewById(R.id.img_music);

        // Control button
        btn_play    = (ImageButton) view.findViewById(R.id.btn_play); // Btn play
        btn_pause   = (ImageButton) view.findViewById(R.id.btn_pause); // Btn pause
        btn_repeat  = (ImageButton) view.findViewById(R.id.btn_repeat); // Btn repeat
        btn_shuffle = (ImageButton) view.findViewById(R.id.btn_shuffle); // Btn shuffle
        btn_forward = (ImageButton) view.findViewById(R.id.btn_forward); // Btn forward
        btn_next    = (ImageButton) view.findViewById(R.id.btn_next);   // Btn next

        // Get song data and set player, views, mode,...
        playSong();
        // setViewSongDetail(view);


        // Button back/hide fragment
        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hidePlayMusicFragment();
                Bundle bundle = getArguments();
                // If in a playlist song, display button play playlist
                if(!Objects.equals(bundle.getString("isInPlaylist"), "no")){
                    ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
                }

            }
        });


        // When click button play music
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                setResumePlayState();
                myHandler.postDelayed(UpdateSongTime,100);

            }
        });

        // When click button pause music
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                setPausePlayState();
            }
        });

        // When click button forward
        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist;
                ArrayList<SongModel> currentPlaylist = ((MainActivity) requireActivity()).currentPlaylist;

                if(Objects.equals(shuffleMode, "YES")){
                    // Set new index of song is random number from 0 to size of playlist - 1
                    position = ThreadLocalRandom.current().nextInt(0,currentPlaylist.size()-1);
                    ((MainActivity) requireActivity()).setSong(currentPlaylist, position);
                    playSong();

                }
                else if(position == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "Đã về đầu danh sách!", Toast.LENGTH_SHORT).show();
                }
                else {
                    position--;
                    ((MainActivity) requireActivity()).setSong(currentPlaylist, position);
                    playSong();
                }
                //
                ((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist = position;
                // System.out.println(((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist);
            }
        });

        // When click button next
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist;
                ArrayList<SongModel> currentPlaylist = ((MainActivity) requireActivity()).currentPlaylist;

                if(Objects.equals(shuffleMode, "YES")){
                    // Set new index of song is random number from 0 to size of playlist - 1
                    position = ThreadLocalRandom.current().nextInt(0,currentPlaylist.size()-1);
                    ((MainActivity) requireActivity()).setSong(currentPlaylist, position);
                    playSong();

                }
                else if(position == currentPlaylist.size()-1){
                    if(Objects.equals(repeatMode, "ALL")){
                        position = 0;
                        ((MainActivity) requireActivity()).setSong(currentPlaylist, position);
                        playSong();
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(), "Đã tới cuối danh sách!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    position++;
                    ((MainActivity) requireActivity()).setSong(currentPlaylist, position);
                    playSong();
                }
                //
                ((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist = position;
                // System.out.println(((MainActivity) requireActivity()).songPlayingIndexInCurrentPlaylist);
            }
        });

        // When click button shuffle
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(shuffleMode, "NO")){
                    btn_shuffle.setBackgroundResource(R.drawable.ic_shuffle_yellow);
                    shuffleMode = "YES";
                }
                else if (Objects.equals(shuffleMode, "YES")){
                    btn_shuffle.setBackgroundResource(R.drawable.ic_shuffle_btn);
                    shuffleMode = "NO";
                }

            }
        });

        // When click button set repeat
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(repeatMode, "NO")){
                    setRepeatMode("ALL");
                }
                else if(Objects.equals(repeatMode, "ALL")){
                    setRepeatMode("ONE");
                }
                else if(mediaPlayer.isLooping() || Objects.equals(repeatMode, "ONE")){
                    setRepeatMode("NO");
                }
            }
        });

        // When change value of seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(progressChangedValue*1000);
                startTime = mediaPlayer.getCurrentPosition();
                seekbar.setProgress(progressChangedValue);
            }
        });


    }


    // Get song data from bundle
    public void getSong(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            // If is play new song, get new data
            if (Objects.equals(bundle.getString("playType"), "new play")) {
                songId          = bundle.getString("songID");
                songName        = bundle.getString("songName");
                songPath        = bundle.getString("songPath");
                songImg         = bundle.getString("songImg");
                songAlbum       = bundle.getString("songAlbum");
                songArtist      = bundle.getString("songArtist");
                songCategory    = bundle.getString("songCategory");
                songDuration    = bundle.getString("songDuration");
                songType        = bundle.getString("songType");
            }
        }
    }

    // Set path and other detail of song to prepare play
    public void playSong(){
        // Receive song info
        Bundle bundle = this.getArguments();
        if(bundle != null){
            // If is play new song, reset player and views
            if(Objects.equals(bundle.getString("playType"), "new play")){
                // If a song is playing, reset media player
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                }
                // Get song data
                getSong();
                // Set path of song
                String PATH_TO_FILE = songPath;
                // Get song from path and play
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(PATH_TO_FILE);
                    if(Objects.equals(songType, "local")){
                        mediaPlayer.prepare();
                    }
                    else {
                        mediaPlayer.prepareAsync();
                    }
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            // Set start play state for view
                            setStartPlayState();

                        }
                    });

                    // Update when song is playing
                    myHandler.postDelayed(UpdateSongTime,100);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Set view
                setViewSongDetail(getView());



            }

            // If just show song is playing, not reset
        }

    }

    // Set detail view song and reset view to start state
    public void setViewSongDetail(View view){

        // =================== Play music screen ====================
        // Set name and artist
        txt_songName.setText(songName);
        txt_songArtist.setText(songArtist);


        // Set image
        if(Objects.equals(songType, "online") || !Objects.equals(songImg, "null")){
            Glide.with(getContext()).load(songImg).placeholder(R.drawable.blue_pink_music_circle).circleCrop().into(imgView);
        }

        // Animation rotate image while playing
        anim = ObjectAnimator.ofFloat(imgView, "rotation", 0, 360);
        anim.setDuration(20000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);

        // Display final time of music
        if(Objects.equals(songDuration, "null")){
            finalTime = mediaPlayer.getDuration();
            txt_max_time.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
        }
        else {
            txt_max_time.setText(songDuration);
        }


        // Set parameter for seekbar
        seekbar = (SeekBar)view.findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        seekbar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((long) finalTime));
        oneTimeOnly = 1;

        // Set repeat mode
        setRepeatMode(repeatMode);

        // =================== PLAYING BAR ===================
        // Set song name and artist
        TextView txtSongPlayingName, txtSongPlayingArtist;
        txtSongPlayingName      = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingArtist    = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_artist);
        txtSongPlayingName.setText(songName);
        txtSongPlayingArtist.setText(songArtist);

    }

    // Set start play state for view when start play
    public void setStartPlayState(){
        // ========== Play music screen ===========
        btn_play.setVisibility(View.INVISIBLE); // Hide btn play
        btn_pause.setVisibility(View.VISIBLE);  // Display btn pause
        anim.start();                           // Start animation rotate img
        // Enable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        // =========== PLAYING BAR ==============
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE); // Hide btn play on bar
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE); // Display btn pause on bar
        // Start animation rotate img on bar
        ((MainActivity)getActivity()).animImgSongPlaying.start();
        // Enable animation textview song name
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);


    }

    // Set start play state for view when pause
    public void setPausePlayState(){
        // ========== Play music screen ===========
        btn_play.setVisibility(View.VISIBLE);       // Display btn play
        btn_pause.setVisibility(View.INVISIBLE);    // Hide btn pause
        anim.pause();                               // Start animation rotate img
        // Disable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(false);
        txt_songName.setSingleLine(true);

        // =========== PLAYING BAR ==============
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.VISIBLE); // Display btn play
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.INVISIBLE); // Hide btn pause
        // Pause animation rotate img on bar
        ((MainActivity)getActivity()).animImgSongPlaying.pause();
        // Disable animation textview song name on bar
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(false);
        txtSongPlayingName.setSingleLine(true);


    }

    // Set start play state for view when resume play
    public void setResumePlayState(){
        // ========== Play music screen ===========
        btn_play.setVisibility(View.INVISIBLE); // Hide btn play
        btn_pause.setVisibility(View.VISIBLE);  // Display btn pause
        // Resume animation rotate img
        if(anim.isPaused()){
            anim.resume();
        }
        else { // If anim had been reset by hidePlayMusicFragment(), start over
            anim.start();
        }

        // Enable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        // =========== PLAYING BAR ==============
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE); // Hide btn play on bar
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE); // Display btn pause on bar
        // Resume animation rotate img on bar
        ((MainActivity)getActivity()).animImgSongPlaying.resume();
        // Enable animation textview song name on bar
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);


    }

    // Set repeat mode
    public void setRepeatMode(String rpMode){
        if(Objects.equals(rpMode, "NO")){
            mediaPlayer.setLooping(false);
            btn_repeat.setBackgroundResource(R.drawable.ic_repeat_btn);
            repeatMode = rpMode;
        }
        else if(Objects.equals(rpMode, "ALL")){
            mediaPlayer.setLooping(false);
            btn_repeat.setBackgroundResource(R.drawable.ic_repeat_yellow);
            repeatMode = rpMode;
        }
        else if(Objects.equals(rpMode, "ONE")){
            mediaPlayer.setLooping(true);
            btn_repeat.setBackgroundResource(R.drawable.ic_repeat_one_yellow);
            repeatMode = rpMode;
        }

    }

    // Function format time of song
    public String time_format(long minute, long second){
        String min = String.format("%d", minute);
        String sec = String.format("%d", second);

        if(second < 10){
            sec = String.format("0%d", second);
        }
        return min + ":" +sec;
    }

    // Update when playing
    private final Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            txt_time_current.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
            seekbar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((long) startTime));
            myHandler.postDelayed(this, 100);

            if(!mediaPlayer.isPlaying()){
                setPausePlayState();
            }
        }
    };


}
