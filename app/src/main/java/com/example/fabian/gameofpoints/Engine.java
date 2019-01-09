package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Engine implements SensorEventListener {
    private float impactX, impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;
    private int msPerFrame = 30;
    private int timer;
    private int touched;

    private Objekt a;

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor sensor;
    private GameActivity gameActivity;
    private ScheduledExecutorService service;


    public Engine(SensorManager sensorManager, GameView gameView, GameActivity gameActivity){
        this.gameView = gameView;
        this.sensorManager = sensorManager;
        this.gameActivity = gameActivity;
    }

    public void start(){
        service = Executors.newSingleThreadScheduledExecutor();

        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                moveObjects();
                timer++;
                if(timer>1000/msPerFrame){
                    timer=0;
                    gameView.setCountdown();
                }
                gameView.setData();
            }
        };
        service.scheduleAtFixedRate(runnable, msPerFrame, msPerFrame, TimeUnit.MILLISECONDS);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void testFinish(){
        boolean stammgut = false;       //geht alle Viecher durch, solange ein Viech aus beiden Stämmen lebt, werde beide Variablen wieder auf true gesetzt und Süiel geht weiter
        boolean stammböse = false;
        for(int i = 0; i<Objekt.getListe().size();i++){
            if(Objekt.getObjekt(i).getLife()>0){
                if(Objekt.getObjekt(i).getMembership()==1){
                    stammgut=true;
                }else{
                    stammböse=true;
                }
            }
        }
        if(stammgut==false||stammböse==false){ //Wenn ein Stamm ausgelöscht ist
            if(stammböse==false){
                gameActivity.endGame(true, gameView.getCountdown()); //Speichert Countdown
            }else{
                gameActivity.endGame(false, gameView.getCountdown());
            }
        }else {
            for (int i = 0; i < Objekt.getListe().size(); i++) {
                if (Objekt.getObjekt(i).getLife() > 0 && Objekt.getObjekt(i).getControl()) { //Wenn gute Viecher gewonnen haben
                    setData(i);
                }
            }
        }
    }

    public void createObjekt(int x, int y, int membership, int life, int attack, int speed, int color){     //erstellt neues Viech
        a = new Objekt(x, y, membership, life, attack, speed, color);
    }

    public void setSelect(int objekt){      //wenn angetipptes Viech lieb, dann wird es ausgewählt
        for(int i = 0; i<Objekt.getListe().size(); i++){
            Objekt.getObjekt(i).setControl(false);
        }
        Objekt.getObjekt(objekt).setControl(true);
        setData(objekt);
    }

    private void setData(int objekt){
        gameActivity.setData(Objekt.getObjekt(objekt).getLife(), Objekt.getObjekt(objekt).getAttack(), Objekt.getObjekt(objekt).getSpeed()); //gibt Daten von Viech
    }

    public void setRegion(float minX, float minY, float maxX, float maxY){  //Setzt Position
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public void prüfeTouch(int touchX, int touchY){
        double highest = 1000000;
        int objekt = -1;
        for(int i = 0; i<Objekt.getListe().size(); i++){  //geht alle Viecehr durch
            if(Objekt.getObjekt(i).getMembership()==1&&Objekt.getObjekt(i).getLife()>0) { //Alle Viecher die mehr als 0 Leben haebn
                double länge = Math.sqrt((Objekt.getObjekt(i).getX() - touchX) * (Objekt.getObjekt(i).getX() - touchX) + (Objekt.getObjekt(i).getY() - touchY) * (Objekt.getObjekt(i).getY() - touchY)); //wählt das aus, das am nächsten am Touch war
                if (highest > länge) {
                    highest = länge;
                    objekt = i;
                }
            }
        }
        if(objekt!=-1) {
            setSelect(objekt);
        }
    }

    public void stop(){ //stoppt Spiel
        service.shutdown();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { //fragt Werte von Sensor ab
        impactX = -sensorEvent.values[0]*scaleA;
        impactY = sensorEvent.values[1]*scaleA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void moveObjects(){ //berechent, wie sich die Objekte bewegen sollen
        for(int i = 0; i<Objekt.getListe().size(); i++){ //geht alle Objekte durch
            if(Objekt.getObjekt(i).getLife()>0) { //wenndas Viech noch lebt
                float x = Objekt.getObjekt(i).getX(); //setzt X- und Y-Koordinaten
                float y = Objekt.getObjekt(i).getY();

                if(Objekt.getObjekt(i).getBreedTimer()<=200) { //Objekt nicht Paarungsfäig oder an Border ist, stoßen sie sich ab
                    boolean rand = false;
                    if(Objekt.getObjekt(i).getX()<minX+Objekt.getObjekt(i).getR()/2) {
                        Objekt.getObjekt(i).setX(minX+Objekt.getObjekt(i).getR()/2);
                        setObjektToBorder(i);
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getX()>maxX-Objekt.getObjekt(i).getR()/2) {
                        Objekt.getObjekt(i).setX(maxX-Objekt.getObjekt(i).getR()/2);
                        setObjektToBorder(i);
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getY()<minY+Objekt.getObjekt(i).getR()/2) {
                            Objekt.getObjekt(i).setY(minY + Objekt.getObjekt(i).getR() / 2);
                        setObjektToBorder(i);
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getY()>maxY-Objekt.getObjekt(i).getR()/2) {
                            Objekt.getObjekt(i).setY(maxY - Objekt.getObjekt(i).getR() / 2);
                        setObjektToBorder(i);
                        rand=true;
                    }
                    if(!rand) {
                        for(int o = 0; o<Objekt.getListe().size(); o++) {
                            if(i!=o && Objekt.getObjekt(o).getLife()>0 && Math.sqrt((Objekt.getObjekt(i).getX()-Objekt.getObjekt(o).getX())*(Objekt.getObjekt(i).getX()-Objekt.getObjekt(o).getX())+(Objekt.getObjekt(i).getY()-Objekt.getObjekt(o).getY())*(Objekt.getObjekt(i).getY()-Objekt.getObjekt(o).getY()))<Objekt.getObjekt(i).getR()/2+Objekt.getObjekt(o).getR()/2) { //bei radius nicht durch zwei machen, genauso wie unten bei der neuen Creation
                                if(Objekt.getObjekt(i).getMembership()==Objekt.getObjekt(o).getMembership()) { //Wenn Viecher aus selben Stamm
                                    if(Objekt.getObjekt(i).getBreedState()==false) { //Wenn sie Paarungsfähig sind
                                        if(Objekt.getObjekt(i).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedState()==false) {
                                            Objekt.getObjekt(i).setBreedTimer(400); //Cooldown für die Paarung wird hoch gesetzt
                                            Objekt.getObjekt(i).setPartner(o); //Viechpartner werden gegenseitig gesetzt
                                            Objekt.getObjekt(o).setPartner(i);
                                            Objekt.getObjekt(o).setBreedState(true);
                                        }
                                    }else {
                                        Objekt.getObjekt(i).setBreedTimer(400); //Wenn nicht Paarungsfähig, wird cooldown wieder hoch gesetzt
                                    }
                                }else {
                                    while(Objekt.getObjekt(i).getLife()>0&&Objekt.getObjekt(o).getLife()>0) { //Wenn Viecher aus unterschiedlcihen Stämmen und Viech i mehr Leben als Viech o hat und Viech o mehr als 0 Leben hat
                                        Objekt.getObjekt(o).setLife(Objekt.getObjekt(o).getLife()-Objekt.getObjekt(i).getAttack()); //Viech wird so viele Leben abgezogenn wie anderes Viech Leben hat
                                        if(Objekt.getObjekt(o).getBreedTimer()<200) { //wenn Vermehrungs-Cooldown halb vorbei
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-Objekt.getObjekt(o).getAttack());//Viech wird so viele Leben abgezogenn wie anderes Viech Leben hat
                                        }else {
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-(float)Objekt.getObjekt(o).getAttack()/3); //sonst wird 1/3 der Attackpunkte vom iene Viech dem andern abgezogen
                                            if(Objekt.getObjekt(o).getLife()<=0) {
                                                Objekt.getObjekt(Objekt.getObjekt(o).getPartner()).setBreedTimer(0); //Wenn Objekt tot, wir Vortpflanzungscool-down von früherer Partenr  = 0
                                            }
                                        }
                                    }
                                    testFinish(); //testet, ob gestorbenes Viech das Letzte von einem Stamm war
                                }
                                Objekt.getObjekt(i).setDirection((int)(Math.acos((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())/(Math.sqrt((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())*(Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())+(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())*(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY()))))*180/Math.PI)); //Setzt Richtung
                                if((Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())>0){
                                    Objekt.getObjekt(i).setDirection(-Objekt.getObjekt(i).getDirection()); //Richtungswechsel
                                }
                                touched = 2;
                            }
                        }
                    }
                    if(Objekt.getObjekt(i).getGrow()==2) {
                        Objekt.getObjekt(i).setGrow(1);
                    }
                    if(touched==0) {    //Wenn Objekt nichtmehr von Spieler ausgewählt
                        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection() + (int)(Math.random() * directionChange - directionChange / 2));    //zufällige Richtung in eiem bestimmten Winkelbereich
                        if(Objekt.getObjekt(i).getGrow()!=0) {  //Während Viech wächst
                            Objekt.getObjekt(i).setNewR(); //setzt Wert von Radius neu
                        }
                    }else {
                        if(Objekt.getObjekt(i).getGrow()!=0&&touched==2) {
                            Objekt.getObjekt(i).setGrow(2); //WEnn ausgewählt aber noch nicht ausgewachsen, wird es auf ausgewachsen gesetzt
                        }
                        touched = 0;
                    }
                }
                if(Objekt.getObjekt(i).getBreedTimer()>0&&Objekt.getObjekt(i).getGrow()!=2) { //WEnn nicht ausgewachesen oder noch im Paarungs-Cooldown
                    Objekt.getObjekt(i).setBreedTimer(Objekt.getObjekt(i).getBreedTimer()-1); //Cooldwon wird runtergesetzt
                }
                if(Objekt.getObjekt(i).getBreedTimer()==200&&Objekt.getObjekt(i).getGrow()==0) {
                    if(Objekt.getObjekt(i).getBreedState()==false&&Objekt.getAnzViech()<=20) {
                        //new Viech
                        Objekt b = new Objekt((int)(Objekt.getObjekt(i).getX() + Math.cos((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), (int)(Objekt.getObjekt(i).getY() + Math.sin((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), Objekt.getObjekt(i).getMembership()); //setzt alle Werte
                        if((int)(Math.random()*2)==0) {
                            b.setLife(Objekt.getObjekt(i).getLifeSafe()); //bekommt zufällig die WErte von Elternteil pro wert zugewiesen
                            b.setLifeSafe(Objekt.getObjekt(i).getLifeSafe());
                        }else {
                            b.setLife(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getLifeSafe());
                            b.setLifeSafe(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getLifeSafe());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setAttack(Objekt.getObjekt(i).getAttack());
                        }else {
                            b.setAttack(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getAttack());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setSpeed(Objekt.getObjekt(i).getSpeed());
                        }else {
                            b.setSpeed(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getSpeed());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setColor(Objekt.getObjekt(i).getColor());
                        }else {
                            b.setColor(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getColor());
                        }
                        //new Viech
                    }
                    Objekt.getObjekt(i).setBreedState(false);
                }
            }
        }
        for(int i = 0; i<Objekt.getListe().size(); i++){
            if(Objekt.getObjekt(i).getBreedTimer()<=201&&Objekt.getObjekt(i).getLife()>0&&Objekt.getObjekt(i).getControl()==false) {
                Objekt.getObjekt(i).setX((float)(Objekt.getObjekt(i).getX() + Math.cos(Objekt.getObjekt(i).getDirection()*Math.PI/180) * Objekt.getObjekt(i).getSpeed()));//float statt int
                Objekt.getObjekt(i).setY((float)(Objekt.getObjekt(i).getY() + Math.sin(Objekt.getObjekt(i).getDirection()*Math.PI/180) * Objekt.getObjekt(i).getSpeed()));//float statt int
            }
            if(Objekt.getObjekt(i).getControl()==true&&Objekt.getObjekt(i).getBreedTimer()<=201&&Objekt.getObjekt(i).getLife()>0) {
                //Bewegung des Gesteuerten Objects
                if(impactX>40){
                    Objekt.getObjekt(i).setX(Objekt.getObjekt(i).getX()+Objekt.getObjekt(i).getSpeed());
                }
                if(impactX<-40){
                    Objekt.getObjekt(i).setX(Objekt.getObjekt(i).getX()-Objekt.getObjekt(i).getSpeed());
                }
                if(impactY>40){
                    Objekt.getObjekt(i).setY(Objekt.getObjekt(i).getY()+Objekt.getObjekt(i).getSpeed());
                }
                if(impactY<-40){
                    Objekt.getObjekt(i).setY(Objekt.getObjekt(i).getY()-Objekt.getObjekt(i).getSpeed());
                }
            }
        }
    }

    private void setObjektToBorder(int i){ //180Grad Wende
        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection()-180);
        touched = 1;
    }
}
