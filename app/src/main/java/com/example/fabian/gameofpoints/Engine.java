package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Engine implements SensorEventListener {
    private float impactX;
    private float impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;
    private int msPerFrame = 30;
    private int anzViech = 10;
    private float posx,posy,vx,vy,size;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private GameSurfaceView gameSurfaceView;
    private SensorManager sensorManager;
    private ScheduledExecutorService service;

    public Engine(SensorManager sensorManager, GameSurfaceView gameSurfaceView, GameActivity gameActivity){ //Für Klassendaigramm noch überprüfen, vielleicht alles in der Engine machen (Masteview wird in der Engine deklariert)
        this.gameSurfaceView = gameSurfaceView;
        this.sensorManager = sensorManager;
        for(int i = 0; i<10; i++){
            Objekt a = new Objekt(100, 100, 20, 0, 1, 3, 2, 0xffffff00);
            repaintAction();
        }
    }

    public void start(){
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, msPerFrame, msPerFrame, TimeUnit.MILLISECONDS);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void createObjekt(int x, int y, int r, int membership, int live, int attack, int speed, int color){
        Objekt a = new Objekt(x, y, r, membership, live, attack, speed, color);
    }

    public void moveObjects(){ //aus eclipse einfügen
        for(int i = 0; i<Objekt.getListe().size(); i++){
            Objekt.getListe().get(i).setDirection(Objekt.getListe().get(i).getDirection() + (int)(Math.random() * directionChange - directionChange / 2));
            Objekt.getListe().get(i).setX((float)(Objekt.getListe().get(i).getX() + Math.cos(Objekt.getListe().get(i).getDirection()*Math.PI/180) * Objekt.getListe().get(i).getSpeed()));
            Objekt.getListe().get(i).setY((float)(Objekt.getListe().get(i).getY() + Math.sin(Objekt.getListe().get(i).getDirection()*Math.PI/180) * Objekt.getListe().get(i).getSpeed()));
        }
    }

    public void setRegion(float minX, float minY, float maxX, float maxY){
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public void stop(){
        service.shutdown();
        sensorManager.unregisterListener(this);
    }

    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            /*
            posx += vx;
            posy += vy;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) gameSurfaceView.getLayoutParams();
            params.width = Math.round(size);
            params.height = Math.round(size);
            params.leftMargin = Math.round(posx);
            params.rightMargin = Math.round(posy);
         */
            repaintAction();
            //Aufruf, dass er die Viecher mahlt an einem random Ort

        }
    };


    public void repaintAction() {
        int fps = 3;
        final Runnable beeper = new Runnable() {
            public void run() {
                //int zähler = 0;
                System.out.println("beep"); //+ zähler); DAS WIRD NICHT GESTOPPT; WENN APP NICHTMEHR IM VORDERGRUND IST ODER SO!!! MUSS NOOCH GEÄNDERT WERDEN!!!!!
                //ähler++;
                moveObjects(); // stimmt die Methode?´bzw. reicht die?
            } //AUFRUFE Für Doku: 21.12.18 ca. eine Stunde für das zu verwirklichen und mich in Programm eunzulesen, das Fabi geschrieben hat. APK wollte mal wieder nicht auf mein Handy. Ich weiß noch nicht so ganz in wie fern ich die Liste einbinden solll
        };
        final ScheduledFuture<?> beeperHandle =
                //scheduler.scheduleAtFixedRate(beeper, gameSurfaceView.getFpS(), gameSurfaceView.getFpS(), TimeUnit.MILLISECONDS);
                scheduler.scheduleAtFixedRate(beeper, 1, 1, TimeUnit.MILLISECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 1, TimeUnit.HOURS); //länge derausführung
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        impactX = -sensorEvent.values[0]*scaleA;
        impactY = sensorEvent.values[1]*scaleA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
