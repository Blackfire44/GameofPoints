package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Engine implements SensorEventListener {
    private float impactX, impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;
    private int msPerFrame = 30;
    private int timer;
    private int touched;

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
        boolean stammgut = false;
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
        if(stammgut==false||stammböse==false){
            if(stammböse==false){
                gameActivity.endGame(true, gameView.getCountdown());
            }else{
                gameActivity.endGame(false, gameView.getCountdown());
            }
        }else {
            for (int i = 0; i < Objekt.getListe().size(); i++) {
                if (Objekt.getObjekt(i).getLife() > 0 && Objekt.getObjekt(i).getControl()) {
                    setData(i);
                }
            }
        }
    }

    public void createObjekt(int x, int y, int membership, int life, int attack, int speed, int color){
        Objekt a = new Objekt(x, y, membership, life, attack, speed, color);
    }

    public void setSelect(int objekt){
        for(int i = 0; i<Objekt.getListe().size(); i++){
            Objekt.getObjekt(i).setControl(false);
        }
        Objekt.getObjekt(objekt).setControl(true);
        setData(objekt);
    }

    private void setData(int objekt){
        gameActivity.setData(Objekt.getObjekt(objekt).getLife(), Objekt.getObjekt(objekt).getAttack(), Objekt.getObjekt(objekt).getSpeed());
    }

    public void setRegion(float minX, float minY, float maxX, float maxY){
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public void prüfeTouch(int touchX, int touchY){
        double highest = 1000000;
        int objekt = -1;
        for(int i = 0; i<Objekt.getListe().size(); i++){
            if(Objekt.getObjekt(i).getMembership()==1&&Objekt.getObjekt(i).getLife()>0) {
                double länge = Math.sqrt((Objekt.getObjekt(i).getX() - touchX) * (Objekt.getObjekt(i).getX() - touchX) + (Objekt.getObjekt(i).getY() - touchY) * (Objekt.getObjekt(i).getY() - touchY));
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

    public void stop(){
        service.shutdown();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        impactX = -sensorEvent.values[0]*scaleA;
        impactY = sensorEvent.values[1]*scaleA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void moveObjects(){
        for(int i = 0; i<Objekt.getListe().size(); i++){
            if(Objekt.getObjekt(i).getLife()>0) {
                float x = Objekt.getObjekt(i).getX();
                float y = Objekt.getObjekt(i).getY();

                if(Objekt.getObjekt(i).getBreedTimer()<=200) {
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
                                if(Objekt.getObjekt(i).getMembership()==Objekt.getObjekt(o).getMembership()) {
                                    if(Objekt.getObjekt(i).getBreedState()==false) {
                                        if(Objekt.getObjekt(i).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedState()==false) {
                                            Objekt.getObjekt(i).setBreedTimer(400);
                                            Objekt.getObjekt(i).setPartner(o);
                                            Objekt.getObjekt(o).setPartner(i);
                                            Objekt.getObjekt(o).setBreedState(true);
                                        }
                                    }else {
                                        Objekt.getObjekt(i).setBreedTimer(400);
                                    }
                                }else {
                                    while(Objekt.getObjekt(i).getLife()>0&&Objekt.getObjekt(o).getLife()>0) {
                                        Objekt.getObjekt(o).setLife(Objekt.getObjekt(o).getLife()-Objekt.getObjekt(i).getAttack());
                                        if(Objekt.getObjekt(o).getBreedTimer()<200) {
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-Objekt.getObjekt(o).getAttack());
                                        }else {
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-(float)Objekt.getObjekt(o).getAttack()/3);
                                            if(Objekt.getObjekt(o).getLife()<=0) {
                                                Objekt.getObjekt(Objekt.getObjekt(o).getPartner()).setBreedTimer(0);
                                            }
                                        }
                                    }
                                    testFinish();
                                }
                                Objekt.getObjekt(i).setDirection((int)(Math.acos((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())/(Math.sqrt((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())*(Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())+(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())*(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY()))))*180/Math.PI));
                                if((Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())>0){
                                    Objekt.getObjekt(i).setDirection(-Objekt.getObjekt(i).getDirection());
                                }
                                touched = 2;
                            }
                        }
                    }
                    if(Objekt.getObjekt(i).getGrow()==2) {
                        Objekt.getObjekt(i).setGrow(1);
                    }
                    if(touched==0) {
                        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection() + (int)(Math.random() * directionChange - directionChange / 2));
                        if(Objekt.getObjekt(i).getGrow()!=0) {
                            Objekt.getObjekt(i).setNewR();
                        }
                    }else {
                        if(Objekt.getObjekt(i).getGrow()!=0&&touched==2) {
                            Objekt.getObjekt(i).setGrow(2);
                        }
                        touched = 0;
                    }
                }
                if(Objekt.getObjekt(i).getBreedTimer()>0&&Objekt.getObjekt(i).getGrow()!=2) {
                    Objekt.getObjekt(i).setBreedTimer(Objekt.getObjekt(i).getBreedTimer()-1);
                }
                if(Objekt.getObjekt(i).getBreedTimer()==200&&Objekt.getObjekt(i).getGrow()==0) {
                    if(Objekt.getObjekt(i).getBreedState()==false&&Objekt.getAnzViech()<=20) {
                        //new Viech
                        Objekt b = new Objekt((int)(Objekt.getObjekt(i).getX() + Math.cos((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), (int)(Objekt.getObjekt(i).getY() + Math.sin((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), Objekt.getObjekt(i).getMembership());
                        if((int)(Math.random()*2)==0) {
                            b.setLife(Objekt.getObjekt(i).getLifeSafe());
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

    private void setObjektToBorder(int i){
        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection()-180);
        touched = 1;
    }
}
