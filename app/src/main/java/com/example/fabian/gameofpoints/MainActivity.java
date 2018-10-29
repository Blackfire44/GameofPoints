package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
    private int layout;
    private int welt;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showstartfragment();
       // startMusic();
    }

    private void startGame(){
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.activity_main, null));
        layout=4;
    }

    private void startMusic(){
        //music = MediaPlayer.create(this, R.raw.music);
        //music.setLooping(true);
        //music.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
       // if(music!=null){
       //     music.pause();
       // }
    }

    @Override
    protected void onResume(){
        super.onResume();
       // music.start();
    }

    @Override
    protected void onDestroy() {
        //stopgame();
        //music.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        switch(layout){
            case 0:super.onBackPressed();
                break;
            case 1:showstartfragment();
                break;
            case 2:showlevelfragment();
                break;
            case 3:showsettingfragment();
                break;
            case 4://showstopfragment();
                break;
            case 5://Gameover
                break;
        }
    }

    private void showstartfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.start, null));
        container.findViewById(R.id.start).setOnClickListener(this);
        layout=0;
    }

    private void showlevelfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null));
        container.findViewById(R.id.zuruek).setOnClickListener(this);
        container.findViewById(R.id.Level1).setOnClickListener(this);
        container.findViewById(R.id.Level2).setOnClickListener(this);
        layout=1;
    }

    private void showsettingfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        TextView text = (TextView)findViewById(R.id.level1name);
        String se = text.getText().toString();
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.settings, null));
        container.findViewById(R.id.zuruek2).setOnClickListener(this);
        container.findViewById(R.id.l1).setOnClickListener(this);
        container.findViewById(R.id.r1).setOnClickListener(this);
        fillTextView(R.id.planetsettings, se);
        layout=2;
    }

    /*private void showstopfragment(){
            //stopgame();
            ViewGroup container = (ViewGroup)findViewById(R.id.container);
            container.addView(getLayoutInflater().inflate(R.layout.stopp, null));
            container.findViewById(R.id.backtotitle).setOnClickListener(this);
            container.findViewById(R.id.Continue).setOnClickListener(this);
            layout=3;
    }*/

    private void showgameoverfragment(){
        //stopgame();
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.gameover, null));
        layout=5;
    }

    private void update(){

    }

    private void fillTextView(int id, String text){
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }
int zahl = 0;
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start){
            showlevelfragment();
        }else if(view.getId()==R.id.zuruek){
            showstartfragment();
        }else if(view.getId()==R.id.Level1){
            welt=1;
            showsettingfragment();
        }else if(view.getId()==R.id.Level2){
            welt=2;
            showsettingfragment();
        }else if(view.getId()==R.id.zuruek2){
            showlevelfragment();
        }else if(view.getId()==R.id.l1){
            String si = ""+zahl;
            fillTextView(R.id.Design, si);
            zahl--;
        }else if(view.getId()==R.id.r1){
            String si = ""+zahl;
            fillTextView(R.id.Design, si);
            zahl++;
        }
    }
}
