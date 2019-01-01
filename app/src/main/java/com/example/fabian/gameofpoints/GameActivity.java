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
import android.media.AudioManager;
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
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.util.Log.d;

public class GameActivity extends Activity implements View.OnClickListener{
    private int live = 1;
    private int attack = 1;
    private int speed = 1;
    private int dontknow = 1;
    private int upgradePoints = 20;
    private int layout;
    private int world; //wird je nach Level auf 1, 2, 3, 4, 5.... gesetzt
    private int player = 0;
    private int scrollWidth;
    private int anzahlWelten = 7;
    private int playerselection;
    private int[] playerliste = {R.drawable.krokotest, 0, R.drawable.lava0, 100, R.drawable.p3b1, 200, R.drawable.schnee0, 300,R.drawable.objekt_0, 500,R.drawable.krokotest, 500,R.drawable.krokotest, 500,R.drawable.krokotest, 500};
    private String[] playernamen = {"kroko1","kroko2","kroko3","kroko14","krokoX","krokoX","krokoX","krokoX"};

    private ImageView mImageViewEmptying;
    private TextView tv;
    private GameSurfaceView gameview;
    private Engine engine;
    private SharedPreferences sp;
    private SharedPreferences.Editor e;
    private CustomDialog customDialog;
    private MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        löscheShared();
        pluscoins(1000);


        setPlayer1();

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
        startMusic(R.raw.intro, false);
        startRandomMusic();
    }

    private void setPlayer1(){
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player0", false)==false&&player==0) {
            e=sp.edit();
            e.putBoolean("player0", true);
            e.commit();
        }
        playerselect();
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
        //get Time
        int time = 15;
        int timergrenze = 30;
        //getLevel(welt).getZeitMissionen

        for(int stern = 1; stern<5; stern++) {
            if (time<=timergrenze && sp.getBoolean("star" + world + stern, false) == false) {
                e.putBoolean("star" + world + stern, true);
                pluscoins(50);
            }
            timergrenze-=10;
        }
        e.commit();
        showDialog("", "4444");
            for (int rubin = 0; rubin < 4; rubin++) {
                if(sp.getBoolean("star" + world + (rubin+1), false)==true){ //stern41 4ter Stern der 1ten Welt
                    imageStar(R.id.star11+4*(world-1)+rubin, rubin);
                    showDialog("", "2222");
                }
            }
    }

    private void setStars(){
        sp = getPreferences(MODE_PRIVATE);
        for(int welt = 0; welt<anzahlWelten; welt++) {
            for (int rubin = 1; rubin < 5; rubin++) {
                if(sp.getBoolean("star" + welt + rubin, false)==true){ //stern14 4ter Stern der 1ten Welt
                    imageStar(R.id.star11+4*welt+rubin-1, rubin-1);
                    showDialog("", "2222");
                }
            }
        }
    }

    private void imageStar(int rubin, int vier){
        if(vier!=3) {
            setImage(rubin, R.drawable.star1);
        }else{
            setImage(rubin, R.drawable.star1);//4er Stern
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

    private void startMusic(int i, boolean loop){
        music = MediaPlayer.create(this, i);
        if(loop){
            music.setLooping(true);
        }
        music.start();
    }

    private void startRandomMusic(){
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(music!=null) {
                    music.release();
                }
                switch((int)Math.random()*5){
                    case 0:startMusic(R.raw.intro, false);
                        break;
                    case 1:startMusic(R.raw.intro, false);
                        break;
                    case 2:startMusic(R.raw.intro, false);
                        break;
                    case 3:startMusic(R.raw.intro, false);
                        break;
                    case 4:startMusic(R.raw.intro, false);
                        break;
                    default:startMusic(R.raw.intro, false);
                }
                startRandomMusic();
            }
        });
    }

    @Override
    protected void onPause(){
        //pausegame();
        super.onPause();
        if(music!=null){
            music.pause();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        music.start();
    }

    @Override
    protected void onDestroy() {
        //stopgame();
        music.stop();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //stopgame();
        music.stop();
        super.onStop();
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
            case 3:
                outoflevel();
                showstartfragment();
                break;
            case 4:showstartfragment();
                break;
            case 5:showstartfragment();
                break;
            case 6:showloadfragment();//Setting
                break;
            case 7:showsettingfragment();//showstopfragment();//start
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
        container.findViewById(R.id.container).setOnClickListener(this);
        layout=0;
    }

    private void showlevel1fragment(){
        if(layout==3){
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level1, null)); //level1
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=1;
    }

    private void showlevel2fragment(){
        if(layout==3){
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level2, null)); //level2
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.player1).setOnClickListener(this);
        container.findViewById(R.id.player3).setOnClickListener(this);
        container.findViewById(R.id.buy).setOnClickListener(this);
        layout=2;
        player=playerselection;
        update();
    }

    private void showlevel3fragment(){
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null));
        findViewById(R.id.scroll).setVisibility(View.INVISIBLE);
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.rotate1).setOnClickListener(this);
        container.findViewById(R.id.rotate2).setOnClickListener(this);
        container.findViewById(R.id.rotate3).setOnClickListener(this);
        container.findViewById(R.id.rotate4).setOnClickListener(this);
        container.findViewById(R.id.rotate5).setOnClickListener(this);
        container.findViewById(R.id.rotate6).setOnClickListener(this);
        container.findViewById(R.id.rotate7).setOnClickListener(this);
        container.findViewById(R.id.star11).setOnClickListener(this);
        container.findViewById(R.id.star12).setOnClickListener(this);
        container.findViewById(R.id.star13).setOnClickListener(this);
        container.findViewById(R.id.star14).setOnClickListener(this);
        container.findViewById(R.id.star21).setOnClickListener(this);
        container.findViewById(R.id.star22).setOnClickListener(this);
        container.findViewById(R.id.star23).setOnClickListener(this);
        container.findViewById(R.id.star24).setOnClickListener(this);
        container.findViewById(R.id.star31).setOnClickListener(this);
        container.findViewById(R.id.star32).setOnClickListener(this);
        container.findViewById(R.id.star33).setOnClickListener(this);
        container.findViewById(R.id.star34).setOnClickListener(this);container.findViewById(R.id.star41).setOnClickListener(this);
        container.findViewById(R.id.star42).setOnClickListener(this);
        container.findViewById(R.id.star43).setOnClickListener(this);
        container.findViewById(R.id.star44).setOnClickListener(this);
        container.findViewById(R.id.star51).setOnClickListener(this);
        container.findViewById(R.id.star52).setOnClickListener(this);
        container.findViewById(R.id.star53).setOnClickListener(this);
        container.findViewById(R.id.star54).setOnClickListener(this);
        container.findViewById(R.id.star61).setOnClickListener(this);
        container.findViewById(R.id.star62).setOnClickListener(this);
        container.findViewById(R.id.star63).setOnClickListener(this);
        container.findViewById(R.id.star64).setOnClickListener(this);
        container.findViewById(R.id.star71).setOnClickListener(this);
        container.findViewById(R.id.star72).setOnClickListener(this);
        container.findViewById(R.id.star73).setOnClickListener(this);
        container.findViewById(R.id.star74).setOnClickListener(this);
        layout=3;
        setStars();
        scroll();
        startanimation();
        //Log.d(getClass().getSimpleName(), Integer.toString(gameview.getFpS())+ " fps");
    }

    private void showlevel4fragment(){
        if(layout==3){
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level4, null)); //level4
        container.findViewById(R.id.zuruekLevel).setOnClickListener(this);
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        layout=4;
    }

    private void showlevel5fragment(){
        if(layout==3){
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level5, null)); //level5
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
        setPlayerImage(R.id.player, playerselection);
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
            case 4:
                fillTextView(id,"World 4: Planet4");
                break;
            case 5:
                fillTextView(id,"World 5: Planet5");
                break;
            case 6:
                fillTextView(id,"World 6: Planet6");
                break;
            case 7:
                fillTextView(id,"World 7: Planet7");
                break;
            default:
                fillTextView(id, "Irgendwas anderes!");
        }
    }

    private void setPlayer(){
        if(player==0){
            setPlayerImage(R.id.player1, playerliste.length-2);
        }else{
            setPlayerImage(R.id.player1, player-2);
        }
        setPlayerImage(R.id.player2, player);
        if(player==playerliste.length-2){
            setPlayerImage(R.id.player3, 0);
        }else{
            setPlayerImage(R.id.player3, player+2);
        }
        updateFilter();
        updateBought();
        fillTextView(R.id.playername, playernamen[player/2]);
    }

    private void updateBought(){//Text unten
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player"+player/2, false)==false){
            fillTextView(R.id.cost, ""+playerliste[player+1]);
            setImage(R.id.money, R.drawable.coin);
        }else{
            if(player==playerselection){
                fillTextView(R.id.cost, "selected");
            }else{
                fillTextView(R.id.cost, "tap to select");
            }
            setImage(R.id.money, R.drawable.haken);
        }
    }

    private void playerselect(){
        if(sp.getBoolean("player"+player/2, false)){
            playerselection = player;
        }
    }

    /*private void updatebackground(int id, int choose){
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player"+choose/2, false)){
            if(choose==playerselection){
                setBackground(id, R.drawable.playerbackground3);
            }else{
                setBackground(id, R.drawable.playerbackground2);
            }
        }else{
            setBackground(id, R.drawable.playerbackground1);
        }
    }*/

    private void updateFilter(){
        sp = getPreferences(MODE_PRIVATE);
        for(int choose = 0; choose<playerliste.length/2; choose++) {
            if (sp.getBoolean("player" + choose, false)) {
                    if (choose*2 == playerselection) {
                        setImage(R.id.filter1 + choose, R.drawable.filtergelb);
                    } else {
                        setImage(R.id.filter1 + choose, R.drawable.filterweiss);
                    }
            }

        }
    }

    private void setBought(){
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player"+player/2, false)==false) {
            if (sp.getInt("coins", 0) - playerliste[player + 1] >= 0) {
                e = sp.edit();
                e.putInt("coins", sp.getInt("coins", 0) - playerliste[player + 1]);
                e.putBoolean("player" + player / 2, true);
                e.commit();
                updateCoins();
            } else {
                showDialog("", "You have not enough coins to buy "+playernamen[player/2]);
            }
        }
        playerselect();
        setPlayer();
    }

    private void pluscoins(int bonus){
        sp = getPreferences(MODE_PRIVATE);
        e = sp.edit();
        e.putInt("coins", sp.getInt("coins", 0)+bonus);
        e.commit();
    }

    private void updateCoins(){
        sp = getPreferences(MODE_PRIVATE);
        fillTextView(R.id.coins, "Charakter:   "+sp.getInt("coins", 0));
    }

    private void setPlayerImage(int id, int number){
        setImage(id, playerliste[number]);
    }

    private void setImage(int id, int recource){
        mImageViewEmptying = findViewById(id);
        mImageViewEmptying.setImageResource(recource);
    }

   /* private void setBackground(int id, int recource){
        mImageViewEmptying = findViewById(id);
        mImageViewEmptying.setBackgroundResource(recource);
    }*/

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
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate7);
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
        mImageViewEmptying = (ImageView) findViewById(R.id.rotate7);
        ((AnimationDrawable) mImageViewEmptying.getBackground()).stop();
    }

    private void update(){
        switch(layout) {
            case 2:
                setPlayer();
                updateCoins();
                break;
            case 6:
                fillTextView(R.id.t1, "Live: " + live);
                fillTextView(R.id.t2, "Attack: " + attack);
                fillTextView(R.id.t3, "Speed: " + speed);
                fillTextView(R.id.t4, "dont know: " + dontknow);
                fillTextView(R.id.upgradePoints, "Upgradepoints left: " + upgradePoints);
                break;
        }
    }

    private void fillTextView(int id, String text){
        tv = (TextView)findViewById(id);
        tv.setText(text);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.container:
                if(layout==0) {
                    showloadfragment();
                }
                break;
            case R.id.zuruekLevel:
                if(layout!=1&&layout!=4&&layout!=5) {
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
                world=6;
                showsettingfragment();
                break;
            case R.id.rotate7:
                world=7;
                showsettingfragment();
                break;
            case R.id.star11:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star12:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star13:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star14:
                world = 1; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star21:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star22:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star23:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star24:
                world = 2; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star31:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star32:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star33:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star34:
                world = 3; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star41:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star42:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star43:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star44:
                world = 4; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star51:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star52:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star53:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star54:
                world = 5; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star61:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star62:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star63:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star64:
                world = 6; //weg machen, nur zu Testzwecken
                prüfeStars();
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star71:
                showDialog("Rubin 1:","try to finish within 0 seconds");
                break;
            case R.id.star72:
                showDialog("Rubin 2:","try to finish within 10 seconds");
                break;
            case R.id.star73:
                showDialog("Rubin 3:","try to finish within 20 seconds");
                break;
            case R.id.star74:
                world = 7; //weg machen, nur zu Testzwecken
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
                engine.stop();
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
            case R.id.player1:
                player-=2;
                if(player<0){
                    player = playerliste.length-2;
                }
                setPlayer();
                break;
            case R.id.player3:
                player+=2;
                if(player>playerliste.length-2){
                    player = 0;
                }
                setPlayer();
                break;
            case R.id.buy:
                setBought();
                break;
            default:showDialog("Error", "Wrong OnClickListener!");
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