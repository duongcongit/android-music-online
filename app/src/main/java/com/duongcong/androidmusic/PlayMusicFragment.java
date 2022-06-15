package com.duongcong.androidmusic;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayMusicFragment extends Fragment {

    // Animation animation;
    ImageView imgView;

    private ImageButton btn_forward,btn_pause,btn_play,btn_repeat;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView txt_time_current, txt_max_time;

    public static int oneTimeOnly = 0;

    private ObjectAnimator anim;


    // Function format time of song
    public String time_format(long minute, long second){
        String min = String.format("%d", minute);
        String sec = String.format("%d", second);

        if(second < 10){
            sec = String.format("0%d", second);
        }
        return min + ":" +sec;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout =  inflater.inflate(R.layout.fragment_play_music, container, false);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.GONE);
        //
        return rootLayout;


    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

        ImageButton btn_back = (ImageButton)view.findViewById(R.id.btn_back);




        imgView = (ImageView) view.findViewById(R.id.img_music);

        // Animation rotate image
        anim = ObjectAnimator.ofFloat(imgView, "rotation", 0, 360);
        anim.setDuration(20000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);


        // Control button
        btn_forward = (ImageButton) view.findViewById(R.id.btn_forward);
        btn_pause = (ImageButton) view.findViewById(R.id.btn_pause);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_repeat = (ImageButton) view.findViewById(R.id.btn_repeat);

        //
        txt_time_current = (TextView)view.findViewById(R.id.txt_time_current);
        txt_max_time = (TextView)view.findViewById(R.id.txt_max_time);


        // String PATH_TO_FILE = Environment.getExternalStorageDirectory().getPath() +  "/a.mp3";
        String PATH_TO_FILE = "https://www.mboxdrive.com/K391%20Alan%20Walker%20%20Ahrix%20%20End%20of%20Time%20Lyrics_320kbps.mp3";

        mediaPlayer = new MediaPlayer();


        try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();

        }


        MediaPlayer.TrackInfo[] infM = mediaPlayer.getTrackInfo();



        finalTime = mediaPlayer.getDuration();

        System.out.println(finalTime);

        // Set parameter seekbar when start
        seekbar = (SeekBar)view.findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        seekbar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((long) finalTime));
        oneTimeOnly = 1;


        // Display final time of music
        txt_max_time.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayFragment(new HomeFragment());
            }
        });


        // When click button play music
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(anim.isPaused()){
                    anim.resume();
                }
                else {
                    anim.start();
                }
                startTime = mediaPlayer.getCurrentPosition();
                txt_time_current.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
                seekbar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((long) startTime));
                myHandler.postDelayed(UpdateSongTime,100);
                btn_pause.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.INVISIBLE);
            }
        });

        // When click button pause music
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                anim.pause();

                btn_pause.setVisibility(View.INVISIBLE);
                btn_play.setVisibility(View.VISIBLE);
            }
        });


        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                    btn_repeat.setBackgroundResource(R.drawable.ic_repeat_btn);
                }
                else {
                    mediaPlayer.setLooping(true);
                    btn_repeat.setBackgroundResource(R.drawable.ic_repeat_yellow);
                }
            }
        });



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



    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            startTime = mediaPlayer.getCurrentPosition();
            txt_time_current.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
            seekbar.setProgress((int) TimeUnit.MILLISECONDS.toSeconds((long) startTime));
            myHandler.postDelayed(this, 100);

            if(!mediaPlayer.isPlaying()){
                btn_pause.setVisibility(View.INVISIBLE);
                btn_play.setVisibility(View.VISIBLE);
                anim.pause();
            }
        }
    };


}
