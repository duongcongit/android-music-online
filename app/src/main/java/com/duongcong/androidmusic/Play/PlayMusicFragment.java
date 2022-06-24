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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayMusicFragment extends Fragment {

    // View
    ImageView imgView;
    private ImageButton btn_forward,btn_pause,btn_play,btn_repeat;
    private TextView txt_songName, txt_songArtist;

    // Song detail
    private String songId, songName, songPath, songAlbum, songArtist, songCategory, songDuration, songType ;
    private double startTime = 0;
    private double finalTime = 0;

    // Media player
    public MediaPlayer mediaPlayer;

    protected String repeatMode = "NO";

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
        return rootLayout;
    }

    // Listen when hide or show play music fragment
    @Override
    public void onHiddenChanged(boolean hidden) {
        // Get play type send from another fragment
        Bundle bundle = this.getArguments();
        String playType = "";
        if(bundle != null){
            playType = bundle.getString("playType");
        }
        super.onHiddenChanged(hidden);
        if (hidden) {
            // Show bottom navigation and playing bar
           ((MainActivity)getActivity()).navigation.setVisibility(View.VISIBLE);
           ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.VISIBLE);
        } else {
            // Hide bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);

            // Set new song if play type is "new play"
            if(Objects.equals(playType, "new play")){
                // If a song is playing, reset media player
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                }
                getSong();
                setViewSongDetail(getView());
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSong();
        setViewSongDetail(view);

        // Control button
        btn_forward = (ImageButton) view.findViewById(R.id.btn_forward);

        // Button back/hide fragment
        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hidePlayMusicFragment();

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

        // When click button set repeat
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isLooping()){
                    setRepeatMode("NO");
                }
                else {
                    setRepeatMode("ONE");
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

    // Function format time of song
    public String time_format(long minute, long second){
        String min = String.format("%d", minute);
        String sec = String.format("%d", second);

        if(second < 10){
            sec = String.format("0%d", second);
        }
        return min + ":" +sec;
    }

    // Set path and other detail of song to prepare play
    public void getSong(){
        // Receive song info
        Bundle bundle = this.getArguments();
        if(bundle != null){
            songId          = bundle.getString("songID");
            songName        = bundle.getString("songName");
            songPath        = bundle.getString("songPath");
            songAlbum       = bundle.getString("songAlbum");
            songArtist      = bundle.getString("songArtist");
            songCategory    = bundle.getString("songCategory");
            songDuration    = bundle.getString("songDuration");
            songType        = bundle.getString("songType");
        }

        // Set path of song
        String PATH_TO_FILE = songPath;

        // Create media player
        mediaPlayer = new MediaPlayer();

        // Get song from path and play
        try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Update when song is playing
            myHandler.postDelayed(UpdateSongTime,100);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Set detail view song
    public void setViewSongDetail(View view){

        // Textview
        txt_songName = view.findViewById(R.id.txtView_songName);
        txt_songArtist = view.findViewById(R.id.txtView_songArtist);
        txt_time_current = (TextView)view.findViewById(R.id.txt_time_current);
        txt_max_time = (TextView)view.findViewById(R.id.txt_max_time);

        imgView = (ImageView) view.findViewById(R.id.img_music);

        // Control button
        btn_play = (ImageButton) view.findViewById(R.id.btn_play); // Btn play
        btn_pause = (ImageButton) view.findViewById(R.id.btn_pause); // Btn pause
        btn_repeat = (ImageButton) view.findViewById(R.id.btn_repeat); // Btn repeat

        // Animation auto scroll song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(false);
        txt_songName.setSingleLine(true);

        // Animation rotate image while playing
        anim = ObjectAnimator.ofFloat(imgView, "rotation", 0, 360);
        anim.setDuration(20000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);

        txt_songName.setText(songName); // Set textview name
        txt_songArtist.setText(songArtist); // Set textview artist

        // Display final time of music
        finalTime = mediaPlayer.getDuration();
        txt_max_time.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));

        // Set parameter for seekbar
        seekbar = (SeekBar)view.findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        seekbar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((long) finalTime));
        oneTimeOnly = 1;

        // Set start play state for view
        setStartPlayState();
        // Set repeat mode
        setRepeatMode(repeatMode);
    }

    // Set start play state for view when start play
    public void setStartPlayState(){
        // When start a new play
        btn_play.setVisibility(View.INVISIBLE); // Hide btn play
        btn_pause.setVisibility(View.VISIBLE); // Display btn pause
        anim.start(); // Start animation rotate img
        // Enable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        // Enable animation textview song name on bar
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);
        ((MainActivity)getActivity()).animImgSongPlaying.start(); // Start animation rotate img on bar
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE); // Hide btn play on bar
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE); // Display btn pause on bar
    }

    // Set start play state for view when pause
    public void setPausePlayState(){
        btn_play.setVisibility(View.VISIBLE); // Display btn play
        btn_pause.setVisibility(View.INVISIBLE); // Hide btn pause
        anim.pause(); // Start animation rotate img
        // Disable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(false);
        txt_songName.setSingleLine(true);

        // Disable animation textview song name on bar
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(false);
        txtSongPlayingName.setSingleLine(true);
        ((MainActivity)getActivity()).animImgSongPlaying.pause(); // Pause animation rotate img on bar
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.VISIBLE); // Display btn play
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.INVISIBLE); // Hide btn pause
    }

    // Set start play state for view when resume play
    public void setResumePlayState(){
        btn_play.setVisibility(View.INVISIBLE); // Hide btn play
        btn_pause.setVisibility(View.VISIBLE); // Display btn pause
        anim.resume(); // Resume animation rotate img
        // Enable animation textview song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        // Enable animation textview song name on bar
        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);
        ((MainActivity)getActivity()).animImgSongPlaying.resume(); // Resume animation rotate img on bar
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE); // Hide btn play on bar
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE); // Display btn pause on bar
    }

    // Set repeat mode
    public void setRepeatMode(String rpMode){
        if(rpMode == "NO"){
            mediaPlayer.setLooping(false);
            btn_repeat.setBackgroundResource(R.drawable.ic_repeat_btn);
            repeatMode = rpMode;
        }
        else if(rpMode == "ONE"){
            mediaPlayer.setLooping(true);
            btn_repeat.setBackgroundResource(R.drawable.ic_repeat_yellow);
            repeatMode = rpMode;
        }
        else if(rpMode == "ALL"){

        }
    }

    // Update when playing
    private Runnable UpdateSongTime = new Runnable() {
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
