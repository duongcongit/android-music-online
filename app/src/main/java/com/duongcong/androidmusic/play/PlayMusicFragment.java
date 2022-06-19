package com.duongcong.androidmusic.play;

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

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayMusicFragment extends Fragment {

    // Animation animation;
    ImageView imgView;

    private ImageButton btn_forward,btn_pause,btn_play,btn_repeat;
    private TextView txt_songName, txt_songArtist;

    // Song detail
    private String songPath, songName, songID, songArtist, songAlbum;

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
            //
        } else {
            // Hide bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);

            // Set new song if play type is "new play"
            if(playType == "new play"){
                // If a song is playing, reset media player
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                }
                getSong();
                setViewSongDetail(getView());
            }
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

    // Set path and other detail of song to prepare play
    public void getSong(){

        // Receive song info
        Bundle bundle = this.getArguments();
        if(bundle != null){
            songPath = bundle.getString("songPath");
            songName = bundle.getString("songName");
            // songID = bundle.getString("songID");
            songArtist = bundle.getString("songArtist");
            songAlbum = bundle.getString("songAlbum");
        }

        // Set path
        String PATH_TO_FILE = songPath;
        // String PATH_TO_FILE = "https://www.mboxdrive.com/K391%20Alan%20Walker%20%20Ahrix%20%20End%20of%20Time%20Lyrics_320kbps.mp3";

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

    // Set view song detail
    public void setViewSongDetail(View view){

        // Textview
        txt_songName = view.findViewById(R.id.txtView_songName);
        txt_songArtist = view.findViewById(R.id.txtView_songArtist);
        txt_time_current = (TextView)view.findViewById(R.id.txt_time_current);
        txt_max_time = (TextView)view.findViewById(R.id.txt_max_time);

        imgView = (ImageView) view.findViewById(R.id.img_music);

        // Button
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_pause = (ImageButton) view.findViewById(R.id.btn_pause);
        btn_repeat = (ImageButton) view.findViewById(R.id.btn_repeat);

        // Animation auto scroll song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(false);
        txt_songName.setSingleLine(true);

        // Animation rotate image
        anim = ObjectAnimator.ofFloat(imgView, "rotation", 0, 360);
        anim.setDuration(20000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);

        // Set view name and artist of song
        if(songArtist == "<unknown>"){
            Toast.makeText(getActivity().getApplicationContext(), songArtist, Toast.LENGTH_SHORT).show();
        }

        txt_songName.setText(songName);
        txt_songArtist.setText(songArtist);

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
        setRepeatMode(repeatMode);
    }

    // Set start play state for view when start play
    public void setStartPlayState(){
        btn_play.setVisibility(View.INVISIBLE);
        btn_pause.setVisibility(View.VISIBLE);
        anim.start();

        // Enable scrolling song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);

        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE);
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).animImgSongPlaying.start();
    }

    // Set start play state for view when pause
    public void setPausePlayState(){
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.INVISIBLE);
        anim.pause();

        // Disable scrolling song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(false);
        txt_songName.setSingleLine(true);

        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(false);
        txtSongPlayingName.setSingleLine(true);

        ((MainActivity)getActivity()).animImgSongPlaying.pause();
        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.INVISIBLE);
    }

    // Set start play state for view when resume play
    public void setResumePlayState(){
        btn_pause.setVisibility(View.VISIBLE);
        btn_play.setVisibility(View.INVISIBLE);
        anim.resume();

        // Enable scrolling song name
        txt_songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txt_songName.setSelected(true);
        txt_songName.setSingleLine(true);

        TextView txtSongPlayingName;
        txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
        txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtSongPlayingName.setSelected(true);
        txtSongPlayingName.setSingleLine(true);

        ((MainActivity)getActivity()).btnPlayBar.setVisibility(View.INVISIBLE);
        ((MainActivity)getActivity()).btnPauseBar.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).animImgSongPlaying.resume();
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

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity)getActivity()).navigation.setVisibility(View.VISIBLE);
                        ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.VISIBLE);
                    }
                }, 200);

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
