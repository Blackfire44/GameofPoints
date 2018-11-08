package com.example.fabian.gameofpoints;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
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
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
    private int live = 1;
    private int attack = 1;
    private int speed = 1;
    private int dontknow = 1;
    private int upgradePoints = 20;
    private int layout;
    private int world;
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
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.Level1).setOnClickListener(this);
        container.findViewById(R.id.Level2).setOnClickListener(this);
        container.findViewById(R.id.Level3).setOnClickListener(this);
        layout=1;
        scroll();
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        //((AnimationDrawable) imageView.getDrawable()).start();
        //((TransitionDrawable) imageView.getDrawable()).startTransition(200);
    }

    private void showsettingfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.settings, null));
        container.findViewById(R.id.zuruekSettings).setOnClickListener(this);
        container.findViewById(R.id.l1).setOnClickListener(this);
        container.findViewById(R.id.r1).setOnClickListener(this);
        container.findViewById(R.id.l2).setOnClickListener(this);
        container.findViewById(R.id.r2).setOnClickListener(this);
        container.findViewById(R.id.l3).setOnClickListener(this);
        container.findViewById(R.id.r3).setOnClickListener(this);
        container.findViewById(R.id.l4).setOnClickListener(this);
        container.findViewById(R.id.r4).setOnClickListener(this);
        layout=2;
        setPlanet(R.id.planetsettings);
        update();
    }

    private void showstopfragment(){
            //stopgame();
            ViewGroup container = (ViewGroup)findViewById(R.id.container);
            container.addView(getLayoutInflater().inflate(R.layout.stopp, null));
            container.findViewById(R.id.backtotitle).setOnClickListener(this);
            container.findViewById(R.id.Continue).setOnClickListener(this);
            layout=3;
    }

    private void showgameoverfragment(){
        //stopgame();
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.gameover, null));
        layout=5;
    }

    private void setPlanet(int id){
        switch(world){
            case 0:
                fillTextView(id, "Trius");
                break;
            case 1:
                fillTextView(id, "Quatron");
                break;
            case 2:
                fillTextView(id,"Planet3");
            default:
        }
    }

    private void update(){
        if(layout==2) {
            fillTextView(R.id.t1, "Live: "+live);
            fillTextView(R.id.t2, "Attack: "+attack);
            fillTextView(R.id.t3, "Speed: "+speed);
            fillTextView(R.id.t4, "dont know: "+dontknow);
            fillTextView(R.id.upgradePoints, "Upgradepoints left: "+upgradePoints);
        }
    }

    private void fillTextView(int id, String text){
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start:
                showlevelfragment();
                break;
            case R.id.zuruekLevel:
                saveScrollWidth();
                showstartfragment();
                break;
            case R.id.Level1:
                world=0;
                saveScrollWidth();
                showsettingfragment();
                break;
            case R.id.Level2:
                world=1;
                saveScrollWidth();
                showsettingfragment();
                break;
            case R.id.Level3:
                world=1;
                saveScrollWidth();
                showsettingfragment();
                break;
            case R.id.zuruekSettings:
                showlevelfragment();
                break;
            case R.id.startgame:
                startGame();
                break;
            case R.id.l1:
                proofSettings(11);
                break;
            case R.id.r1:
                proofSettings(12);
                break;
            case R.id.l2:
                proofSettings(21);
                break;
            case R.id.r2:
                proofSettings(22);
                break;
            case R.id.l3:
                proofSettings(31);
                break;
            case R.id.r3:
                proofSettings(32);
                break;
            case R.id.l4:
                proofSettings(41);
                break;
            case R.id.r4:
                proofSettings(42);
                break;
            default:
        }
    }

    private void proofSettings(int click){
        switch(click){
            case 11:
                if(live>1) {
                    live--;
                    upgradePoints++;
                }else{

                }
                break;
            case 12:
                if(upgradePoints>0) {
                    live++;
                    upgradePoints--;
                }else{

                }
                break;
            case 21:
                if(attack>1) {
                    attack--;
                    upgradePoints++;
                }else{

                }
                break;
            case 22:
                if(upgradePoints>0) {
                    attack++;
                    upgradePoints--;
                }else{

                }
                break;
            case 31:
                if(speed>1) {
                    speed--;
                    upgradePoints++;
                }else{

                }
                break;
            case 32:
                if(upgradePoints>0) {
                    speed++;
                    upgradePoints--;
                }else{

                }
                break;
            case 41:
                if(dontknow>1) {
                    dontknow--;
                    upgradePoints++;
                }else{

                }
                break;
            case 42:
                if(upgradePoints>0) {
                    dontknow++;
                    upgradePoints--;
                }else{

                }
                break;
            default:
        }
        update();
    }

    private void scroll(){
        findViewById(R.id.scroll).scrollTo(scrollWidth, 0);
    }

    private void saveScrollWidth(){
        scrollWidth = findViewById(R.id.scroll).getScrollX();
    }
}