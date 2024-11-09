package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ImageView playPauseIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Отсутствует подключение к интернету. Запуск в автономном режиме.", Toast.LENGTH_LONG).show();
        }

        playPauseIcon = findViewById(R.id.btPause);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.stuff);
        seekBar = findViewById(R.id.seekBar);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);
    }

    public void previous(View view) {
        if (mediaPlayer != null) {
            seekBar.setProgress(0);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            playPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void pause(View view) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            } else {
                mediaPlayer.start();
                playPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24);
            }
        }
    }

    public void next(View view) {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getDuration());
            mediaPlayer.seekTo(mediaPlayer.getDuration());
            mediaPlayer.pause();
            playPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void open(View view) {
        Intent intent = new Intent(MainActivity.this, RadioActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SongFetchReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20000, pendingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
