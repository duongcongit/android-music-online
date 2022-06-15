package com.duongcong.androidmusic;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class PlayMusicActivity extends AppCompatActivity {


    Animation animation;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        // animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

        imgView = (ImageView) findViewById(R.id.img_music);

        // Animation rotate image
        anim = ObjectAnimator.ofFloat(imgView, "rotation", 0, 360);
        anim.setDuration(20000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);


        // Control button
        btn_forward = (ImageButton) findViewById(R.id.btn_forward);
        btn_pause = (ImageButton) findViewById(R.id.btn_pause);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_repeat = (ImageButton) findViewById(R.id.btn_repeat);

        //
        txt_time_current = (TextView)findViewById(R.id.txt_time_current);
        txt_max_time = (TextView)findViewById(R.id.txt_max_time);


        // String PATH_TO_FILE = Environment.getExternalStorageDirectory().getPath() +  "/a.mp3";
        String PATH_TO_FILE = "https://www.mboxdrive.com/K391%20Alan%20Walker%20%20Ahrix%20%20End%20of%20Time%20Lyrics_320kbps.mp3";


        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        finalTime = mediaPlayer.getDuration();

        // Set parameter seekbar when start
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(true);
        seekbar.setMax((int) TimeUnit.MILLISECONDS.toSeconds((long) finalTime));
        oneTimeOnly = 1;

        // Display final time of music
        txt_max_time.setText(time_format(TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));



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


        /* b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5
                            seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5
                            seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5
                            seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5
                            seconds",Toast.LENGTH_SHORT).show();
                }
            }
        }); */

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