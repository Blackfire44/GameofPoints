package com.example.fabian.gameofpoints;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class GameActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
    private int life = 1;
    private int attack = 1;
    private int speed = 1;
    private int upgradePoints = 20;
    private int layout;
    private int rubine;
    private int world; //wird je nach Level auf 1, 2, 3, 4, 5.... gesetzt
    private int player = 0;
    private int scrollWidth;
    private int anzahlWelten = 7;
    private int timer = 0;
    private boolean win;
    
    private int playerselection;
    private int[] playerliste = {R.drawable.krokotest, 0, R.drawable.viech1, 100, R.drawable.viech2, 200, R.drawable.viech3, 300,R.drawable.viech4, 500,R.drawable.viech5, 500,R.drawable.viech6, 500,R.drawable.viech7, 500};
    private String[] playernamen = {"Kroko", "Lofi", "Gemini", "Kaozi", "Skit", "Blu", "Eggsea", "Enigma" };
    private int[] background = {R.drawable.trieusoberflaeche, R.drawable.quatronoberflaeche,R.drawable.p3oberflaeche,R.drawable.tuerlisoberflaeche,R.drawable.schneeoberflaeche,R.drawable.lavaoberflaeche,R.drawable.sonneoberflaeche};
    private int[] lifelistcomp = {3, 19, 4, 8, 5, 2, 20};
    private int[] attacklistcomp = {3, 1, 10, 7, 4, 18, 5};
    private int[] speedlistcomp = {15, 1, 7, 6, 12, 1, 2};
    private int[] lifelist = {11, 4, 10, 7, 9, 10, 5};
    private int[] attacklist = {6, 9, 3, 5, 4, 18, 10};
    private int[] speedlist = {3, 7, 7, 5, 1, 6, 5};
    private int[] boesecolorliste = {R.drawable.viechboese,R.drawable.viechboese2,R.drawable.viechboese,R.drawable.viechboese2,R.drawable.viechboese,R.drawable.viechboese2,R.drawable.viechboese,R.drawable.viechboese2,R.drawable.viechboese,R.drawable.viechboese2};

    private ImageView mImageViewEmptying;
    private TextView tv;
    private GameView gameview;
    private Engine engine;
    private SharedPreferences sp;
    private SharedPreferences.Editor e;
    private CustomDialog customDialog;
    private MediaPlayer music;

    private MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            randomMusic(); //Startet eine zufällige Musik
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setPlayer1(); //Der erste Charakter wird freigeschaltet

        switch((int)(Math.random()*3+1)){ //Der Hintergrund wird zufällig gesetzt
            case 1:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund1);
                break;
            case 2:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund2);
                break;
            case 3:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund3);
                break;
            case 4:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund4);
                break;
            default:findViewById(R.id.container).setBackgroundResource(R.drawable.hintergrund1);
        }
        showstartfragment(); //start.xml wird angezeigt
        startMusic(R.raw.hoch, true);//Die Musik wird gestartet
    }

    private void setPlayer1(){ //Der erste Charakter wird direkt freigeschaltet
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player0", false)==false&&player==0) { //Prüfung, ob er schon freigeschaltet ist
            e=sp.edit();
            e.putBoolean("player0", true);
            e.commit();
            pluscoins(1000); //Man bekommt am Anfang 1000 Münzen
        }
        playerselect(); //Dieser Charakter wird ausgewählt
    }

    private void startGame(){ //Das Spiel wird gestartet
        Objekt.getListe().clear(); //Die Objektliste wird geleert
        ViewGroup container = (ViewGroup)findViewById(R.id.container); //Eine ViewGroup wird gebildet und sucht alle container
        container.removeAllViews(); //Alle conatiner werden entfernt
        container.addView(getLayoutInflater().inflate(R.layout.activity_game, null)); //Das activity_game.xml Layout wird aufgerufen
        container.findViewById(R.id.zuruekLevel2).setOnClickListener(this); //OnClickListener werden gesetzt
        container.findViewById(R.id.schalten).setOnClickListener(this);
        container.findViewById(R.id.container).setOnTouchListener(this); //ein OnTouchListener wird gesetzt, da nur davon die X- und Y-Werte abgelesen werden können
        findViewById(R.id.background).setBackgroundResource(background[world-1]); //Der Hintergrund wird je nach Level gesetzt

        gameview = new GameView(this); //Ein neuer GameView wird erzeugt
        gameview.setVisibility(View.VISIBLE); //Dieser wird sichtbar gemacht
        container.addView(gameview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); //Der GameView wird zur ViewGroup hinzugefügt

        engine = new Engine((SensorManager)getSystemService(Context.SENSOR_SERVICE), gameview, this); //Eine neue Engine wird erzeugt
        engine.setRegion(0, 0, container.getWidth(), container.getHeight()); //Der Rand wird anhand des Containers abgesteckt

        engine.createObjekt(container.getWidth()-200, 200, 2, lifelistcomp[world-1], attacklistcomp[world-1], speedlistcomp[world-1],boesecolorliste[world-1]); //Die Objekte werden anhand der Daten aus den Listen erzeugt, dabei entstehen drei gegnerische und zwei auf der eigenen Seite
        engine.createObjekt(container.getWidth()/2, 200, 2, lifelistcomp[world-1], attacklistcomp[world-1], speedlistcomp[world-1],boesecolorliste[world-1]);
        engine.createObjekt(200, 200, 2, lifelistcomp[world-1], attacklistcomp[world-1], speedlistcomp[world-1],boesecolorliste[world-1]);

        engine.createObjekt(container.getWidth()-200, container.getHeight()-200, 1, lifelist[world-1], attacklist[world-1], speedlist[world-1],playerliste[playerselection]); //Viecher von Spieler
        engine.createObjekt(200, container.getHeight()-200, 1, lifelist[world-1], attacklist[world-1], speedlist[world-1],playerliste[playerselection]);

        engine.createObjekt(container.getWidth()/2, container.getHeight()-200, 1, life, attack, speed, playerliste[playerselection]); //Der eigene Charakter wird anhand der selbst ausgesuchten Daten erstellt
        engine.setSelect(Objekt.getListe().size()-1); //Der eigene Charakter wird zum Steuern ausgewählt

        timer=1000; //Die timer-Variable wird auf einen hohen Wert gesetzt, das ist kein richtiger Timer dient nur für die Funktionen später
        engine.start(); //Das Spiel wird gestartet
        layout=7; //Das Layout wird gesetzt
        randomMusic(); //Die zufällige Musik wird gestartet
    }

    public void setData(float life, int attack, int speed){ //Die Anzeige für die Eigenschaften des Charakters, während eines Spiels, wird aktualisiert
        fillTextView(R.id.life, "Life: "+(int)life);
        fillTextView(R.id.attack, "Attack: "+attack);
        fillTextView(R.id.speed, "Speed: "+speed);
    }

    private void einschalten(){ //Die Anzeige für die Eigenschaften des Charakters, während eines Spiels, wird sichtbar gemacht
        fillTextView(R.id.schalten, "turn off");
        findViewById(R.id.life).setVisibility(View.VISIBLE);
        findViewById(R.id.attack).setVisibility(View.VISIBLE);
        findViewById(R.id.speed).setVisibility(View.VISIBLE);
    }

    private void ausschalten(){ //Die Anzeige für die Eigenschaften des Charakters, während eines Spiels, wird unsichtbar gemacht
        fillTextView(R.id.schalten, "turn on");
        findViewById(R.id.life).setVisibility(View.INVISIBLE);
        findViewById(R.id.attack).setVisibility(View.INVISIBLE);
        findViewById(R.id.speed).setVisibility(View.INVISIBLE);
    }

    public void endGame(boolean which, int timer){ //Wird nach Ende eines Spiels aufgerufen
        win = which;
        this.timer = timer; //Der Timer wird zum Anzeigen von dem GameView geholt
        engine.stop(); //Die laufenden Aktionen werden gestoppt
        stopMusic(); //Die Musik wird angehalten
        findViewById(R.id.container).post(new Runnable() {
            public void run() {
                showgameoverfragment(); //showgameoverfragment.xml wird aufgerufen
            }
        });
    }

    private void prüfeStars() { //Nach Beendung eines Levels wird geprüft, welche Rubine freigeschaltet wurden
        sp = getPreferences(MODE_PRIVATE);
        e = sp.edit();
        rubine = 10;
        pluscoins(10);

        int timergrenze = 90; //Die Leistung für den ersten Rubin ist bei 90 Sekunden

        for(int stern = 1; stern<4; stern++) { //Die ersten drei Rubine werden bei bestimmten Zeiten, die immer knapper werden, erspielt
            if (timer<=timergrenze && !sp.getBoolean("star" + world + stern, false)) { //Prüfung, ob der Stern schon freigespielt wurde und jetzt freigeschaltet werden darf
                e.putBoolean("star" + world + stern, true);
                e.commit();
                pluscoins(50); //Man bekommt zusätzlich 50 Münzen
                rubine += 50; //Für die Anzeige werden die Rubine gezählt
            }
            timergrenze-=30; //Die benötigte Zeit wird knapper
        }
        if(upgradePoints>=10&&!sp.getBoolean("star" + world + 4, false)){ //Der 4.Rubin wird freigespielt, wenn mindestens 10 upgradePoints noch übrig sind
            e.putBoolean("star" + world + 4, true);
            e.commit();
            pluscoins(100); //Man bekommt zusätzlich 100 Münzen
            rubine += 10; //Für die Anzeige werden die Rubine gezählt
        }
        fillTextView(R.id.time, "You earned "+rubine); //Es wird angezeigt, wie viele Münzen gewonnen wurden
    }

    private void setStars(){ //Alle Rubine werden für alle Planeten aktualisiert
        sp = getPreferences(MODE_PRIVATE);
        for(int welt = 0; welt<anzahlWelten; welt++) {
            for (int rubin = 1; rubin < 5; rubin++) {
                if(sp.getBoolean("star" + (welt+1) + rubin, false)){ //star14 steht kodiert für :4. Stern der 1. Welt
                    imageStar(R.id.star11+4*welt+rubin-1, rubin-1);
                }
            }
        }
    }

    private void imageStar(int rubin, int vier){  //Die Bilder der Rubine werden in die entsprechenden ImageViews gesetzt
        if(vier!=3) {
            setImage(rubin, R.drawable.star1);
        }else{
            setImage(rubin, R.mipmap.kreislogo4); //Der vierte Rubin wird als Medaille angezeigt
        }
    }

    private void löscheShared(){ //Löscht alle fest gespeicherten Werte, ist hier nur noch drinnen, um so etwas, wie Käufe noch einmal austesten zu können
        sp = getPreferences(MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    private void showDialog(String titel, String text){ //Ein Dialog wird angezeigt
        customDialog = new CustomDialog(this, titel, text);
    }

    private void startMusic(int i, boolean loop){ //Die Musik wird gestartet
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("music", false)==true) {//Prüfung, ob die Musik gestartet werden darf
            if (music != null) { //Wenn noch Musik läuft, wird diese beendet
                music.release();
            }
            music = MediaPlayer.create(this, i); //Der Track wird eingestellt
            if (loop) { //Eine dauerhafte Wiederholung kann eingestellt werden
                music.setLooping(true);
            } else {
                music.setOnCompletionListener(listener); //Der OnCompletionListener wird gesetzt
            }
            music.start(); //Die Musik wird gestartet
        }
    }

    private void stopMusic(){ //die Musik wird angehalten
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("music", false)==true) {//Prüfung, ob die Musik gestoppt werden darf
            if (music != null) {
                music.stop();
            }
        }
    }

    private void randomMusic(){ //Eine zufällige Musik wird ausgewählt
        if (music != null) { //Die laufende Musik wird noch geschlossen, falls sie noch läuft oder fertig, aber noch nicht beendet ist
            music.release();
        }
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("music", false)==true) {//Prüfung, ob die Musik gestartet werden darf
            switch ((int) Math.random() * 3) { //Zufällig wird ein neuer Track ausgewählt
                case 0:
                    startMusic(R.raw.pirates, false);
                    break;
                case 1:
                    startMusic(R.raw.frog, false);
                    break;
                case 2:
                    startMusic(R.raw.trash, false);
                    break;
                default:
                    startMusic(R.raw.pirates, false);
            }
        }
    }

    @Override
    protected void onPause(){ //Wenn die App pausiert wird, werden laufende Prozesse auch pausiert (Home-Button)
        if(layout==7) {
            engine.stop(); //Die laufenden Prozesse werden gestoppt, wenn das Spiel gestartet ist
            showstopfragment(); //stopp.xml wird aufgerufen
        }
        stopMusic(); //Die Musik wird gestoppt
        super.onPause();
    }

    @Override
    protected void onResume(){ //Wenn die App (wieder)geöffnet wird,  werden laufende Prozesse auch gestartet (App wird geöffnet)
        super.onResume();
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("music", false)==true) {//Prüfung, ob die Musik gestartet werden darf
            startMusic(R.raw.hoch, true); //Musik wird wieder gestartet
        }
    }

    @Override
    protected void onDestroy() { //Wenn die App geschlossen wird, werden laufende Prozesse auch beendt
        stopMusic(); //Die Musik wird gestoppt
        super.onDestroy();
    }

    @Override
    protected void onStop() { //Wenn die App gestoppt wird, werden laufende Prozesse auch gestoppt (Speicher ist voll)
        stopMusic(); //Die Musik wird angehalten
        super.onStop();
    }

    @Override
    public void onBackPressed() { //Bei Berührung der Zurücktaste wird nicht direkt die App geschlossen, sondern in das dafür vorgesehene, vorhergehende Layout geleitet
        switch(layout){
            case 0:super.onBackPressed();
                break;
            case 1:showstartfragment();
                break;
            case 2:showstartfragment();
                break;
            case 3:
                outoflevel(); //Die laufenden Aktivitäten werden beendet
                showstartfragment();
                break;
            case 4:showstartfragment();
                break;
            case 5:showstartfragment();
                break;
            case 6:showloadfragment();//(in setting.xml)
                break;
            case 7:showstopfragment();//(nach Start)
                break;
            case 9:showloadfragment();//(in gamover.xml)
                break;
            default:showstartfragment();
        }
    }

    private void showstartfragment(){ //start.xml wird angezeigt
        ViewGroup container = (ViewGroup)findViewById(R.id.container); //Eine ViewGroup wird gebildet und sucht alle container
        container.removeAllViews(); //Alle conatiner werden entfernt
        container.addView(getLayoutInflater().inflate(R.layout.start, null)); //Das Layout, das man haben will, wird gestartet
        container.findViewById(R.id.container).setOnClickListener(this);//Ein OnClickListener wird gesetzt, um den View anklickbar zu machen
        layout=0; //Es wird gespeichert, in welchem Layout man sich gerade befindet
    }

    private void showlevel1fragment(){ //level1.xml wird angezeigt
        if(layout==3){ //Wenn man aus dem level.xml Layout kommt, werden die laufenden Funktionen gestoppt
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level1, null)); //level1
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.zurueckLevel).setOnClickListener(this);
        layout=1; //Es wird gespeichert, in welchem Layout man sich gerade befindet
    }

    private void showlevel2fragment(){ //level2.xml wird angezeigt
        if(layout==3){ //Wenn man aus dem level.xml Layout kommt, werden die laufenden Funktionen gestoppt
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level2, null)); //level2
        container.findViewById(R.id.item1).setOnClickListener(this);//OnClickListener werden gesetzt, um die Views anklickbar zu machen
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.zurueckLevel).setOnClickListener(this);
        container.findViewById(R.id.player1).setOnClickListener(this);
        container.findViewById(R.id.player3).setOnClickListener(this);
        container.findViewById(R.id.buy).setOnClickListener(this);
        layout=2; //Es wird gespeichert, in welchem Layout man sich gerade befindet
        player=playerselection; //Der zu letzt ausgewählte Charakter wird wieder gesetzt
        update(); //Alles wird wieder aktualisiert angezeigt, da die Layouts bei Austritt keine Änderungen speichern
    }

    private void showlevel3fragment(){ //level.xml wird angezeigt
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level, null));
        findViewById(R.id.scroll).setVisibility(View.INVISIBLE); //Der ScrollView wird unsichtbar gesetzt, um den einen Frame, der vor dem Anzeigen der letzten Scrollweite noch gezeigt wird, nicht zu zeigen;//OnClickListener werden gesetzt, um die Views anklickbar zu machen
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.zurueckLevel).setOnClickListener(this);
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
        container.findViewById(R.id.star34).setOnClickListener(this);
        container.findViewById(R.id.star41).setOnClickListener(this);
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
        layout=3; //Es wird gespeichert, in welchem Layout man sich gerade befindet
        setStars(); //Die Sterne werden wieder aktualisiert, da die Layouts bei Austritt keine Änderungen speichern
        scroll(); //Es wird zur alten Weite gescrollt
        startanimation(); //Die Animationen werden gespeichert
    }

    private void showlevel4fragment(){ //level4.xml wird angezeigt
        if(layout==3){ //Wenn man aus dem level.xml Layout kommt, werden die laufenden Funktionen gestoppt
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level4, null)); //level4//OnClickListener werden gesetzt, um die Views anklickbar zu machen
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item5).setOnClickListener(this);
        container.findViewById(R.id.zurueckLevel).setOnClickListener(this);
        layout=4; //Es wird gespeichert, in welchem Layout man sich gerade befindet
    }

    private void showlevel5fragment(){ //level5.xml wird angezeigt
        if(layout==3){ //Wenn man aus dem level.xml Layout kommt, werden die laufenden Funktionen gestoppt
            outoflevel();
        }
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.level5, null)); //level5//OnClickListener werden gesetzt, um die Views anklickbar zu machen
        container.findViewById(R.id.item1).setOnClickListener(this);
        container.findViewById(R.id.item2).setOnClickListener(this);
        container.findViewById(R.id.item3).setOnClickListener(this);
        container.findViewById(R.id.item4).setOnClickListener(this);
        container.findViewById(R.id.turnonoff).setOnClickListener(this);
        container.findViewById(R.id.zurueckLevel).setOnClickListener(this);
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("music", false)==false){ //Der TextView für die Musikeinstellung wird auf "ON" gesetzt, wenn die Musik an ist und auf "OFF", wenn die Musik aus ist
            fillTextView(R.id.turnonoff, "OFF");
        }else{
            fillTextView(R.id.turnonoff, "ON");
        }
        layout=5; //Es wird gespeichert, in welchem Layout man sich gerade befindet
    }

    private void showloadfragment(){ //load.xml wird angezeigt
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.load, null));
        if(layout!=1&&layout!=2&&layout!=4&&layout!=5){ //Bei level1.xml, level2.xml, level4.xml oder level5.xml wird die Navigationbar nicht ausgeblendet, um eine durchgängig vorhandene Navigation BAr zu simulieren
            container.findViewById(R.id.item1).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item2).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item3).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item4).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.item5).setVisibility(View.INVISIBLE);
            container.findViewById(R.id.leiste).setVisibility(View.INVISIBLE);
        }
        load(); //Es wird das Laden gestartet
    }


    private void showsettingfragment(){ //setting.xml wird angezeigt
        outoflevel(); //Laufende Funktionen aus level.xml werden abgebrochen
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.settings, null));
        container.findViewById(R.id.zuruekSettings).setOnClickListener(this); //OnClickListener werden gesetzt, um die Views anklickbar zu machen
        container.findViewById(R.id.l1).setOnClickListener(this);
        container.findViewById(R.id.r1).setOnClickListener(this);
        container.findViewById(R.id.l2).setOnClickListener(this);
        container.findViewById(R.id.r2).setOnClickListener(this);
        container.findViewById(R.id.l3).setOnClickListener(this);
        container.findViewById(R.id.r3).setOnClickListener(this);
        container.findViewById(R.id.startgame).setOnClickListener(this);
        layout=6; //Es wird gespeichert, in welchem Layout man sich gerade befindet
        setPlanet(R.id.planetsettings);
        setPlayerImage(R.id.player, playerselection); //Der ausgewählte Charakter wird angezeigt
        update(); //Eine Aktualisierung wird durchgeführt, da die Layouts bei Austritt keine Änderungen speichern
    }

    private void showstopfragment(){ //stopp.xml wird angezeigt
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.addView(getLayoutInflater().inflate(R.layout.stopp, null));
        container.findViewById(R.id.backtotitle).setOnClickListener(this); //OnClickListener wird gesetzt, um den View anklickbar zu machen
        layout=8; //Es wird gespeichert, in welchem Layout man sich gerade befindet
    }

    private void showgameoverfragment(){ //gameover.xml wird angezeigt
        ViewGroup container = (ViewGroup)findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.gameover, null));
        container.findViewById(R.id.back).setOnClickListener(this); //OnClickListener wird gesetzt, um den View anklickbar zu machen
        if(win){
            prüfeStars(); //Die gewonnenen rubine werden hinzugefügt
            fillTextView(R.id.endscreen, "Level completed!"); //Das Level wurde geschafft
        }else{
            fillTextView(R.id.endscreen, "Game over!"); //Das Level wurde verloren
            fillTextView(R.id.time, "You earned 0"); //Es wird angezeigt, wie viele Münzen gewonnen wurden
        }
        layout=9; //Es wird gespeichert, in welchem Layout man sich gerade befindet
        startMusic(R.raw.hoch, true); //Die Musik für die Levelauswahl wird wieder gestartet
    }

    private void load(){ //Ein Ladebildschirm wird angezeigt, um das Laden der drehenden Planeten nicht als Standbildschirm dastehen zu lassen.
        findViewById(R.id.container).post(new Runnable() {
            public void run() {
                long l = System.currentTimeMillis(); //Die reelle Zeit wird abgenommen
                showlevel3fragment(); //Level.xml wird angezeigt
                try {
                    l = System.currentTimeMillis()-l; //Wenn weniger, als 1,5 Sekunden geladen wurde, wird noch bis 1,5 Sekunden gewartet
                    if(l<1500){
                        Thread.sleep(1500-l);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPlanet(int id){ //In settings.xml wird je nach ausgewählter Welt derren Name angezeigt
        switch(world){
            case 1:
                fillTextView(id, "World 1: Trius");
                break;
            case 2:
                fillTextView(id, "World 2: Quatron");
                break;
            case 3:
                fillTextView(id,"World 3: Odana");
                break;
            case 4:
                fillTextView(id,"World 4: Xondir");
                break;
            case 5:
                fillTextView(id,"World 5: Tempenix");
                break;
            case 6:
                fillTextView(id,"World 6: Fulmir");
                break;
            case 7:
                fillTextView(id,"World 7: Nano");
                break;
            default:
                fillTextView(id, "Irgendwas anderes!");
        }
    }

    private void setPlayer(){ //Die Charaktere werden wieder an richtiger Position angezeigt
        if(player==0){ //Bei dem linken Charakter wird noch geprüft, ob der Mittlere am Rand der Liste steht, um den nächsten dann am anderen Ende auszuwählen
            setPlayerImage(R.id.player1, playerliste.length-2);
        }else{
            setPlayerImage(R.id.player1, player-2);
        }
        setPlayerImage(R.id.player2, player); //Der Mittlere wird gesetzt
        if(player==playerliste.length-2){ //Bei dem rechten Charakter wird noch geprüft, ob der Mittlere am Rand der Liste steht, um den nächsten dann am anderen Ende auszuwählen
            setPlayerImage(R.id.player3, 0);
        }else{
            setPlayerImage(R.id.player3, player+2);
        }
        updateFilter(); //Die Filter für die Leiste werden aktualisiert
        updateBought(); //Die Anzeige für die Kosten werden aktualisiert
        fillTextView(R.id.playername, playernamen[player/2]); //Der Name des mittleren Cgarakters wird aktualisiert
    }

    private void updateBought(){ //Der Text unter den Charakteren mit den Kosten wird aktualisiert
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player"+player/2, false)==false){ //Prüfung, ob der Charakter gekauft wurde
            fillTextView(R.id.cost, ""+playerliste[player+1]); //Die Kosten werden im textView angezeigt
            setImage(R.id.money, R.drawable.coin); //Ein Geldstück wird neben den Kosten gezeigt
        }else{
            if(player==playerselection){ //Prüfung, ob der Spieler ausgewählt ist, wenn er schon gekauft ist
                fillTextView(R.id.cost, "selected"); //Er ist ausgewählt
            }else{
                fillTextView(R.id.cost, "tap to select"); //Er ist noch nicht ausgewählt
            }
            setImage(R.id.money, R.drawable.haken); //Wenn er schon gekauft ist, wird ein Haken daneben gesetzt
        }
    }

    private void playerselect(){ //Ein Spieler wird für das nächste Spiel ausgewählt
        if(sp.getBoolean("player"+player/2, false)){ //Prüfung, ob er schon gekauft wurde
            playerselection = player;
        }
    }

    private void updateFilter(){ //Die Filter für die Charakterleiste wird aktualisiert
        sp = getPreferences(MODE_PRIVATE);
        for(int choose = 0; choose<playerliste.length/2; choose++) { //Für alle Charaktere der Leiste (Sind alle in der Liste gespeichert)
            if (sp.getBoolean("player" + choose, false)) { //Prüfung, ob er schon gekauft wurde
                if (choose*2 == playerselection) { //Prüfung, ob er für das nächste Spiel ausgewählt wurde
                    setImage(R.id.filter1 + choose, R.drawable.filtergelb); //Filter für die Auswahl
                } else {
                    setImage(R.id.filter1 + choose, R.drawable.filterweiss); //Filter für nicht gekauft oder ausgewählt
                }
            }

        }
    }

    private void setBought(){ //Ein Charakter wird gekauft.
        sp = getPreferences(MODE_PRIVATE);
        if(sp.getBoolean("player"+player/2, false)==false) { //Prüfung, ob er schon gekauft wurde
            if (sp.getInt("coins", 0) - playerliste[player + 1] >= 0) {  //Prüfung, ob das Geld noch reicht
                e = sp.edit();
                e.putInt("coins", sp.getInt("coins", 0) - playerliste[player + 1]); //Das Geld abziehen
                e.putBoolean("player" + player / 2, true); //Den Charakter auf "gekauft" setzen
                e.commit();
                updateCoins(); //Die Geldanzeige aktualisieren
            } else {
                showDialog("", "You have not enough coins to buy "+playernamen[player/2]);
            }
        }
        playerselect(); //Der neue Charakter wird ausgewählt
        setPlayer(); //Das Notwendige wird angepasst
    }

    private void pluscoins(int bonus){ //Das Geld wird in den SharedPreferences um den hinzugefügten Bonus erhöht.
        sp = getPreferences(MODE_PRIVATE);
        e = sp.edit();
        e.putInt("coins", sp.getInt("coins", 0)+bonus);
        e.commit();
    }

    private void updateCoins(){ //Die Anzeige für die Anzahl des vorhandenen Geldes wird aus den SharedPreferences aktualisiert
        sp = getPreferences(MODE_PRIVATE);
        fillTextView(R.id.coins, "Charakter:   "+sp.getInt("coins", 0));
    }

    private void setPlayerImage(int id, int number){ //Setzt ein Bild für einen Spieler aus der playerliste
        setImage(id, playerliste[number]);
    }

    private void setImage(int id, int recource){ //Setze ein bestimmtes Bild in ein ImageView ein
        mImageViewEmptying = findViewById(id);
        mImageViewEmptying.setImageResource(recource);
    }

    private void outoflevel(){ //Bei Austritt von level.xml wird die Animation gestoppt und die Weite, die gescrollt wurde gespeichert
        stopanimation();
        saveScrollWidth();
    }

    private void startanimation(){ //Die Animationen der Planeten werden gestartet
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
    }

    private void stopanimation(){ //Die Animationen der Planeten werden gestoppt
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

    private void update(){ //Aktualisiert anhand des im Moment angezeigten Layouts eben dieses
        switch(layout) {
            case 2: //level2.xml wird aktualisiert
                setPlayer();
                updateCoins();
                break;
            case 6: //settings.xml wird aktualisiert
                fillTextView(R.id.t1, "Life: " + life);
                fillTextView(R.id.t2, "Attack: " + attack);
                fillTextView(R.id.t3, "Speed: " + speed);
                fillTextView(R.id.upgradePoints, "Upgradepoints left: " + upgradePoints);
                break;
        }
    }

    private void fillTextView(int id, String text){ //Setzt einen Text in einen bestimmten TextView ein
        tv = (TextView)findViewById(id);
        tv.setText(text);
    }

    @Override
    public void onClick(View view) { //Die onClick-Funktion verarbeitet Klicks auf bestimmte Views
        switch(view.getId()){ //Der Switch sucht anhand des berührten Views die Funktion, die er erfüllen soll.
            case R.id.container: //level.xml wird aufgerufen (über den Load-Umweg, da es eine Weile lädt und dafür noch ein Ladebildschirm angezeigt werden soll)  (in start.xml)
                if(layout==0) {
                    showloadfragment();
                }
                break;
            case R.id.zurueckLevel: //start.xml wird aufgerufen (in level.xml)
                if(layout!=1&&layout!=2&&layout!=4&&layout!=5) {
                    outoflevel();
                }
                showstartfragment();
                break;
            case R.id.backtotitle: //start.xml wird aufgerufen (nach Start)
                stopMusic(); //Die Musik wird angehalten
                showloadfragment();
                break;
            case R.id.rotate1: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=1;
                showsettingfragment();
                break;
            case R.id.rotate2: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=2;
                showsettingfragment();
                break;
            case R.id.rotate3: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=3;
                showsettingfragment();
                break;
            case R.id.rotate4: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=4;
                showsettingfragment();
                break;
            case R.id.rotate5: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=5;
                showsettingfragment();
                break;
            case R.id.rotate6: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=6;
                showsettingfragment();
                break;
            case R.id.rotate7: //settings.xml wird aufgerufen, die Welt, also das jeweilige Level wird gesetzt (in level.xml)
                world=7;
                showsettingfragment();
                break;
            case R.id.star11: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star12: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star13: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star14: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star21: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star22: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star23: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star24: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star31: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star32: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star33: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star34: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star41: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star42: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star43: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star44: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star51: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star52: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star53: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star54: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star61: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star62: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star63: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star64: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.star71: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 1:","try to finish within 90 seconds");
                break;
            case R.id.star72: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 2:","try to finish within 60 seconds");
                break;
            case R.id.star73: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Rubin 3:","try to finish within 30 seconds");
                break;
            case R.id.star74: //Ein Dialog wird gezeigt, der mitteilt, wie schnell ein Level beendet werden muss (in level.xml)
                showDialog("Special Medal:","Use just 10 Upgradepoints to win this match.");
                break;
            case R.id.zuruekSettings: //level.xml wird aufgerufen (über den Load-Umweg, da es eine Weile lädt und dafür noch ein Ladebildschirm angezeigt werden soll) (in settings.xml)
                showloadfragment();
                break;
            case R.id.startgame: //Das Spiel wird gestartet (in settings.xml)
                    startGame();
                break;
            case R.id.zuruekLevel2: //stop.xml wird aufgerufen und die laufenden Aktionen der engine werden gestoppt (in game_activity.xml)
                engine.stop(); //Die laufenden Aktionen werden gestoppt
                showstopfragment(); //stopp.xml wird aufgerufen
                break;
            case R.id.back: //level.xml wird aufgerufen (über den Load-Umweg, da es eine Weile lädt und dafür noch ein Ladebildschirm angezeigt werden soll) (in activity_game.xml) (in gameover.xml)
                showloadfragment();
                break;
            case R.id.l1: //Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(11);
                break;
            case R.id.r1://Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(12);
                break;
            case R.id.l2://Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(21);
                break;
            case R.id.r2://Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(22);
                break;
            case R.id.l3://Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(31);
                break;
            case R.id.r3://Es iwrd weitergegeben, welcher Wert aus life, attack und speed sich erhöhen oder erniedrigen soll. (in settings.xml)
                proofSettings(32);
                break;
            case R.id.item1: //level1.xml wird aufgerufen
                showlevel1fragment();
                break;
            case R.id.item2:  //level2.xml wird aufgerufen
                showlevel2fragment();
                break;
            case R.id.item3:  //level.xml wird aufgerufen
                showloadfragment();
                break;
            case R.id.item4:  //level4.xml wird aufgerufen
                showlevel4fragment();
                break;
            case R.id.item5:  //level5.xml wird aufgerufen
                showlevel5fragment();
                break;
            case R.id.player1: //Lässt player an der Liste entlanglaufen, um einen Chrakakter weiter nach links zu wechseln (in level2.xml)
                player-=2;
                if(player<0){
                    player = playerliste.length-2;
                }
                setPlayer();
                break;
            case R.id.player3: //Lässt player an der Liste entlanglaufen, um einen Chrakakter weiter nach rechts zu wechseln (in level2.xml)
                player+=2;
                if(player>playerliste.length-2){
                    player = 0;
                }
                setPlayer();
                break;
            case R.id.buy: //Ruft Methode setBought() auf (in level2.xml)
                setBought();
                break;
            case R.id.schalten: //Schaltet die Anzeigen für die Eigenschaften der Objekte ein und aus, je nach dem, ob sie schon sichtbar oder unsichtbar sind (in activity_game.xml)
                if(findViewById(R.id.life).getVisibility()==View.VISIBLE){
                    ausschalten();
                }else{
                    einschalten();
                }
                break;
            case R.id.turnonoff: //Die Musik wird in den Einstellungen ein oder ausgeschaltet, was auch im Handy gespeichert wird, um beim nächsten Öffnen der App immer noch so ausgerichtet zu sein. (in level5.xml)
                sp = getPreferences(MODE_PRIVATE);
                e = sp.edit();
                if(sp.getBoolean("music", false)){
                    e.putBoolean("music", false);
                    e.commit();
                    fillTextView(R.id.turnonoff, "OFF");
                    stopMusic(); //Die Musik wird angehalten
                }else{
                    e.putBoolean("music", true);
                    e.commit();
                    fillTextView(R.id.turnonoff, "ON");
                    startMusic(R.raw.hoch, true); //Die Musik wird gestartet
                }
                break;
            default:showDialog("Error", "Wrong OnClickListener!");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) { //Wenn das Spiel gestartet ist, wird hier nach Bildschirmberührungen geprüft, dessen X- und Y-Werte dann an die Engine über prüfeTouch weitergegeben werden.
        if(layout==7) {
            try {
                engine.prüfeTouch((int)event.getX(), (int)event.getY());
            }catch (Exception e){

            }
        }
        return false;
    }

    private void proofSettings(int click){ //Erhöht oder Erniedrigt die Werte von life, attack und speed, was in Abhängigkeit von upgradePoints steht, da diese auf die ersten Werte aufgeteilt werden, so können auch keine Werte erhöht werden, wenn keine upgradePoints mehr zur Verfügung stehen. Außerdem wird noch geprüft, ob die WErte unter 1 fallen.
        switch(click){
            case 11:
                if(life>1) {
                    life--;
                    upgradePoints++;
                }else{

                }
                break;
            case 12:
                if(upgradePoints>0) {
                    life++;
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
            default:
        }
        update();
    }

    private void scroll(){ //Setzt die Weite, die das letzte Mal gescrollt wurde, ,aus scrollWidth wieder ein. Setzt den ScrollView wieder sichtbar, um nicht für einen Frame am Anfang noch den ersten Planeten anzuzeigen.
        findViewById(R.id.scroll).post(new Runnable() {
            public void run() {
                findViewById(R.id.scroll).scrollTo(scrollWidth, 0);
                findViewById(R.id.scroll).setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveScrollWidth(){ //Sichert die Weite, die gescrollt wurde in scrolWidth
        scrollWidth = findViewById(R.id.scroll).getScrollX();
    }
}