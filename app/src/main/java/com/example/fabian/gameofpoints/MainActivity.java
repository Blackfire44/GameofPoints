package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
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
        startMusic(); //Die Musik wird gestartet
        runProgress(); //Die ProgressBar wird angezeigt
    }

    @Override
    protected void onPause(){  //Wenn die App pausiert wird,  werden laufende Prozesse auch pausiert (Home-Button)
        super.onPause();
        pause=true;
        if(music!=null){
            music.pause();
        }
    }

    @Override
    protected void onResume(){  //Wenn die App (wieder)geöffnet wird,  werden laufende Prozesse auch (wieder) gestartet (App wird geöffnet)
        super.onResume();
        pause=false;
        runProgress();
        music.start();
    }

    @Override
    protected void onDestroy() { //Wenn die App geschlossen wird, werden laufende Prozesse auch beendt
        super.onDestroy();
        pause=true;
        music.stop();
    }

    private void runProgress() { //Die ProgressBar läuft anhand der Musik
            Thread logoTimer = new Thread() {
                public void run() {
                        progress = findViewById(R.id.progressBar);
                        progress.setMax(music.getDuration()); //Die Länge der Musik wird bestimmt und anhand dessen die ProgressBar definiert
                        while(music.isPlaying()){ //Solange die Musik spielt, läuft die ProgressBar ab
                                progress.setProgress(music.getCurrentPosition()); //Der Fortschritt der ProgressBar wird dem der Musik angepasst
                        }
                        if (!pause) { //Prüfung nach einer Pausierung durch zum Beispiel den Home-Button
                            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class); //GameActivity.java wird ausgewählt
                            startActivity(gameIntent); //Die Activity wird gestartet
                            finish(); //Diese Activity wird beendet
                        }
                }
            };
            logoTimer.start();
    }

    private void startMusic(){ //Die Musik wird gestartet
       music = MediaPlayer.create(this, R.raw.intro);
       music.start();
    }
}