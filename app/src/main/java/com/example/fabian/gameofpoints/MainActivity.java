package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends Activity{
    private ProgressBar progress;
    private boolean pause=false;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMusic();
        runProgress();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pause=true;
        if(music!=null){
            music.pause();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        pause=false;
        runProgress();
        music.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause=true;
        music.stop();
    }

    private void runProgress() {
            Thread logoTimer = new Thread() {
                public void run() {
                        progress = findViewById(R.id.progressBar);
                        progress.setMax(music.getDuration());
                        while(music.isPlaying()){
                                progress.setProgress(music.getCurrentPosition());
                        }
                        if (!pause) {
                            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                            startActivity(gameIntent);
                            finish();
                        }
                }
            };
            logoTimer.start();
    }

    private void startMusic(){
        music = MediaPlayer.create(this, R.raw.intro);
        music.start();
    }
}