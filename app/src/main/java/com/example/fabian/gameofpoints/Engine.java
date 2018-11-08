package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Engine implements SensorEventListener {
    private float impactX;
    private float impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;

    private MasterView masterview;
    private SensorManager sensorManager;

    public Engine(SensorManager sensorManager, MasterView masterview, MainActivity mainActivity){
        this.masterview = masterview;
        this.sensorManager = sensorManager;




        for(int i = 0; i<10; i++){
            Objekt a = new Objekt(100, 100, 20, 0, 1, 3, 2, 0xffffff00);
        }
    }

    public void createObjekt(int x, int y, int r, int membership, int live, int attack, int speed, int color){
        Objekt a = new Objekt(x, y, r, membership, live, attack, speed, color);
    }

    public void moveObjects(){
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        impactX = -sensorEvent.values[0]*scaleA;
        impactY = sensorEvent.values[1]*scaleA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
