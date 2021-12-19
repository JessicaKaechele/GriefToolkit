package com.example.memorytracker.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.memorytracker.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class AudioView extends LinearLayout {
    TextView playerPosition, playerDuration;
    SeekBar seekBar;
    ImageView btPlay,btPause;

    MediaPlayer mediaPlayer;
    final Handler handler = new Handler();
    Runnable runnable;

    public AudioView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public AudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.audio_view, this);


        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.progress);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(this, 500);

                    }
                };
                int duration = mediaPlayer.getDuration();
                String sDuration = convertFormat(duration);
                playerDuration.setText(sDuration);
                btPlay.setOnClickListener(v -> {
                    btPlay.setVisibility(View.GONE);
                    btPause.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    handler.postDelayed(runnable, 0);
                });

                btPause.setOnClickListener(v -> {
                    btPause.setVisibility(View.GONE);
                    btPlay.setVisibility(View.VISIBLE);
                    mediaPlayer.pause();
                    handler.removeCallbacks(runnable);
                });

                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        if (fromUser){
                            mediaPlayer.seekTo(progress);
                        }
                        playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });

            }
        });


        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            btPause.setVisibility(View.GONE);
            btPlay.setVisibility(View.VISIBLE);
            mediaPlayer.seekTo(0);
        });
    }

    private String convertFormat(int duration) {
        return  String.format("%02d:%02d", TimeUnit.MICROSECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void setDataSource(Uri uri) throws IOException {
        mediaPlayer.setDataSource(getContext(), uri);
        mediaPlayer.prepare();
    }
}
