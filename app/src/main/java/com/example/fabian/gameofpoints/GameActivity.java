package com.example.fabian.gameofpoints;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.util.Log.d;

public class GameActivity extends Activity implements View.OnClickListener{
    private int live = 1;
    private int attack = 1;
    private int speed = 1;
    private int dontknow = 1;
    private int upgradePoints = 20;
    private int layout;
    private int world; //wird je nach Level auf 1, 2, 3, 4, 5.... gesetzt
    private int scrollWidth;
    private MediaPlayer music;
    private GameSurfaceView gameview;
    private Engine engine;

    private ImageView mImageViewEmptying;
    SharedPreferences sp;
    SharedPreferences.Editor e;
    CustomDialog customDialog;

    private int anzahlWelten = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        löscheShared();

        switch((int)(Math.random()*3+1)){
            case 1:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund1);
                break;
            case 2:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund2);
                break;
            case 3:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund3);
                break;
            default:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund1);
        }
        showstartfragment();


        // startMusic();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startGame(){
        ViewGroup container = (ViewGroup) findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.activity_game, null));
        container.findViewById(R.id.zuruekLevel2).setOnClickListener(this);
        gameview = new GameSurfaceView(this) {

        };
        gameview.setVisibility(View.VISIBLE);
        float basedimension = gameview.getBaseDimension();

        engine = new Engine((SensorManager)getSystemService(Context.SENSOR_SERVICE), gameview, this);
        engine.setRegion(0, 0, container.getWidth(), container.getHeight()); //Rand abstecken mit der halben Basedimension/ deklariert den Rand mit einberechnung des Ballradiuses???? eventuell ändern....
        /*for(int i = 0; i<Objekt.liste.size(); i++){                aus Xml Datei die Anfangslage holen.
            Objekt.liste.get(i).setX(x);
            Objekt.liste.get(i).setY(y);
        }*/
        //loadBackground();
        //container.addView(gameview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout=7;
        for(int i = 0; i <10; i++ ){
            int x = (int) (Math.random() * (container.getHeight()));
            int y = (int) (Math.random() * (container.getWidth()));
            engine.createObjekt(x, y, 1, 2, 3, 4, 5);
        }
        //engine.repaintAction();
        engine.start();

    }


    private void endGame(){
        prüfeStars();
    }

    private void prüfeStars() { //nur nach Levelende!!!!
        sp = getPreferences(MODE_PRIVATE);
        e = sp.edit();
        world = 1; //weg machen, nur zu Testzwecken
        //get Time
        int time = 15;
        int timergrenze = 30;
        //getLevel(welt).getZeitMissionen

        for(int stern = 0; stern<4; stern++) {
            if (time <= timergrenze && sp.getBoolean("star" + stern + world, false) == false) {
                e.putBoolean("star" + stern + world, true);
            }
            timergrenze-=10;
        }
        e.commit();
            for (int rubin = 0; rubin < 4; rubin++) {
                if(sp.getBoolean("star" + rubin + world, false)==true){ //stern41 4ter Stern der 1ten Welt
                    imageStar(R.id.star11+rubin, rubin);
                }
            }

    }

    private void setStars(){
        sp = getPreferences(MODE_PRIVATE);
        for(int welt = 1; welt<=anzahlWelten; welt++) {
            for (int rubin = 0; rubin < 4; rubin++) {
                if(sp.getBoolean("star" + rubin + welt, false)==true){ //stern41 4ter Stern der 1ten Welt
                    imageStar(R.id.star11+rubin, rubin);
                }
            }
        }
    }

    private void imageStar(int rubin, int vier){
        mImageViewEmptying =(ImageView) findViewById(rubin);
        if(vier!=4) {
            mImageViewEmptying.setImageResource(R.drawable.star1);
        }else{
            mImageViewEmptying.setImageResource(R.drawable.star1);//4er Stern
        }
    }

    private void löscheShared(){ //noch entfernen beim Start
        sp = getPreferences(MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    public void setDiagramm(int stamm1, int stammgesamt){
        ProgressBar progress = findViewById(R.id.progressBar4);
        progress.setProgress((int)stamm1/stammgesamt*100);
    }

    private void showDialog(String titel, String text){
        customDialog = new CustomDialog(this, titel, text);
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
            case 2:showstartfragment();
                break;
            case 3:showstartfragment();
                break;
            case 4:showstartfragment();
                break;
            case 5:showstartfragment();
                break;
            case 6:showloadfragment();
                break;
            case 7:showsettingfragment();//showstopfragment();
                break;
            case 8://Weiter spielen
                break;
            case 9:showloadfragment();//Game Over
                break;
            default:showstartfragment();
        }
    }

    private void showstartfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.start, null));
        container.findViewById(R.id.start).setOnClickListener(this);
        layout=0;
    }

    private void showlevel1fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null)); //level1
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=1;
    }

    private void showlevel2fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null)); //level2
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=1;
    }

    private void showlevel3fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null));
        findViewById(R.id.scroll).setVisibility(View.INVISIBLE);
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.rotate1).setOnClickListener(this);
        container.findViewById(R.id.rotate2).setOnClickListener(this);
        container.findViewById(R.id.rotate3).setOnClickListener(this);
        container.findViewById(R.id.rotate4).setOnClickListener(this);
        container.findViewById(R.id.rotate5).setOnClickListener(this);
        container.findViewById(R.id.rotate6).setOnClickListener(this);
        container.findViewById(R.id.star11).setOnClickListener(this);
        container.findViewById(R.id.star21).setOnClickListener(this);
        container.findViewById(R.id.star31).setOnClickListener(this);
        container.findViewById(R.id.star41).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=3;
        setStars();
        scroll();
        startanimation();
        //Log.d(getClass().getSimpleName(), Integer.toString(gameview.getFpS())+ " fps");
    }

    private void showlevel4fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null)); //level4
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=4;
    }

    private void showlevel5fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null)); //level5

        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        layout=5;
    }

    private void showloadfragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.load, null));
        if(layout!=1&&layout!=2&&layout!=4&&layout!=5){
            container.findViewById(R.id.item1).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item2).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item3).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item4).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item5).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.leiste).setVisibility(View.INVISIBLE);
        }
        load();
    }


    private void showsettingfragment(){
        outoflevel();
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
        container.findViewById(R.id.startgame).setOnClickListener(this);
        layout=6;
        setPlanet(R.id.planetsettings);
        update();
    }

    private void showstopfragment(){
        //stopgame();
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.stopp, null));
        container.findViewById(R.id.backtotitle).setOnClickListener(this);
        container.findViewById(R.id.Continue).setOnClickListener(this);
        layout=8;
    }

    private void showgameoverfragment(){
        //stopgame();
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.gameover, null));
        layout=9;
    }

    private void load(){
        findViewById(R.id.container).post(new Runnable() {
            public void run() {
                long l = System.currentTimeMillis();
                showlevel3fragment();
                try {
                    l = System.currentTimeMillis()-l;
                    if(l<1500){
                        Thread.sleep(1500-l);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPlanet(int id){
        switch(world){
            case 1:
                fillTextView(id, "World 1: Trius");
                break;
            case 2:
                fillTextView(id, "World 2: Quatron");
                break;
            case 3:
                fillTextView(id,"World 3: Planet3");
                break;
            default:
                fillTextView(id, "Irgendwas anderes!");
        }
    }
    private void loadBackground(){
        switch (world){
            case 1:
                break;
            case 2:
                break;
            default:

        }
    }

    private void outoflevel(){
        stopanimation();
        saveScrollWidth();
    }

    private void startanimation(){
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate1);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate2);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate3);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate4);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate5);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate6);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).start();
        //Log.d("CREATION", Integer.toString(gameview.getFpS()));
        //Log.d(getClass().getSimpleName(), Integer.toString(gameview.getFpS()) + " fps");
        //Log.d(getClass().getSimpleName(), "Funktioniert das?");
    }

    private void stopanimation(){
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate1);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate2);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate3);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate4);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate5);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate6);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
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
                showloadfragment();
                break;
            case R.id.zuruekLevel:
                if(layout!=1&&layout!=2&&layout!=4&&layout!=5) {
                    outoflevel();
                }
                showstartfragment();
                break;
            case R.id.rotate1:
                world=1;
                showsettingfragment();
                break;
            case R.id.rotate2:
                world=2;
                showsettingfragment();
                break;
            case R.id.rotate3:
                world=3;
                showsettingfragment();
                break;
            case R.id.rotate4:
                world=4;
                showsettingfragment();
                break;
            case R.id.rotate5:
                world=5;
                showsettingfragment();
                break;
            case R.id.rotate6:
                world=5;
                showsettingfragment();
                break;
            case R.id.star11:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star21:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star31:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star41:
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.zuruekSettings:
                showloadfragment();
                break;
            case R.id.startgame:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startGame();
                }
                break;
            case R.id.zuruekLevel2:
                showloadfragment();
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
            case R.id.item1:
                showlevel1fragment();
                break;
            case R.id.item2:
                showlevel2fragment();
                break;
            case R.id.item3:
                showloadfragment();
                break;
            case R.id.item4:
                showlevel4fragment();
                break;
            case R.id.item5:
                showlevel5fragment();
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
        findViewById(R.id.scroll).post(new Runnable() {
            public void run() {
                findViewById(R.id.scroll).scrollTo(scrollWidth, 0);
                findViewById(R.id.scroll).setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveScrollWidth(){
        scrollWidth = findViewById(R.id.scroll).getScrollX();
    }
}
/*Hintergründe von GameActivity
Time-Counter
Tortendiagramm (wie viel Prozent von Planeten schon eingenommen)               \/
Gravity                                                                          ?
Sterne (Ein Stern für gelöst, zwei sehr schnell, drei extrem schnell)
	Nach x Sternen bekommt man y
	Sterne bringen coins
	Coinsystem (langfristig InApp- Käufe)
	Tränke/ Designs/ Upgratepoints/…
Musik
xml-Dateien mit Libary in GameActivity abrufen (Minigolf App)
Eigene Viecher mahlen (Drehbewegung)
Viecher simulieren
Viecher anklicken können, damit man Daten (Art, versch. Punkte, …) ablesen kann
Immer angeklicktes Viech wird von Spieler beeinflusst, dessen Daten können abgelesen werden
random die Höhe der Planeten ändern                                              ??
*/