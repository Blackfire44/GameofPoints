package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
    private int layout;
    private int welt;
    private int scrollWidth;
    private ImageView imageView;
    private MediaPlayer music;
    private MasterView gameview;
    private Engine engine;

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

        gameview = new MasterView(this);
        gameview.setVisibility(View.VISIBLE);
        float basedimension = gameview.getBaseDimension();

        engine = new Engine((SensorManager)getSystemService(Context.SENSOR_SERVICE), gameview, this);
        engine.setRegion(basedimension/2, basedimension/2, container.getWidth()-basedimension/2, container.getHeight()-basedimension/2); //Rand abstecken mit der halben Basedimension/ deklariert den Rand mit einberechnung des Ballradiuses???? eventuell ändern....
        /*for(int i = 0; i<Objekt.liste.size(); i++){                aus Xml Datei die Anfangslage holen.
            Objekt.liste.get(i).setX(x);
            Objekt.liste.get(i).setY(y);
        }*/
        engine.moveObjects();
        //container.addView(gameview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        container.findViewById(R.id.levelauswahl).setOnClickListener(this);
        layout=1;
            scroll();
        //((TransitionDrawable) imageView.getDrawable()).startTransition(200);
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
        update();
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
        if(layout==2) {
            String si = ""+zahl;
            fillTextView(R.id.t1, si);
            fillTextView(R.id.t2, si);
            fillTextView(R.id.t3, si);
            fillTextView(R.id.t4, si);
        }
    }

    private void fillTextView(int id, String text){
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }

    private void countSettings(int id, int richtung){
        zahl=zahl+richtung;
        String si = ""+zahl;
        fillTextView(id, si);
    }

    int zahl = 0;//statt Zahl dann speed, attack, live etc.

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start){
            showlevelfragment();
        }else if(view.getId()==R.id.zuruek){
            saveScrollWidth();
            showstartfragment();
        }else if(view.getId()==R.id.Level1){
            welt=0;
            saveScrollWidth();
            showsettingfragment();
        }else if(view.getId()==R.id.Level2){
            welt=1;
            saveScrollWidth();
            showsettingfragment();
        }else if(view.getId()==R.id.zuruek2){
            showlevelfragment();
        }else if(view.getId()==R.id.l1){
            countSettings(R.id.t1, -1);
        }else if(view.getId()==R.id.r1){
            countSettings(R.id.t1, 1);
        }else if(view.getId()==R.id.levelauswahl){
            startGame();
        }
    }

    public void scroll(){
        zahl = scrollWidth;
        findViewById(R.id.scroll).scrollTo(scrollWidth, 0);
    }

    public void saveScrollWidth(){
        scrollWidth = findViewById(R.id.scroll).getScrollX();
    }
}