package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

public class MainActivity extends Activity{
    private ProgressBar progress;
    private boolean pause=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runProgress();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pause=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        pause=false;
        runProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause=true;
    }

    private void runProgress() {
        Thread logoTimer = new Thread(){
            public void run(){
                try{
                    progress = findViewById(R.id.progressBar);
                    for(int i=0; i<100; i++){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progress.setProgress(i);
                        }
                        sleep(20);
                    }
                    if(!pause) {
                        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                        startActivity(gameIntent);
                        finish();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        logoTimer.start();
    }
}