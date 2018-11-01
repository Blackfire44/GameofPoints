package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Engine implements SensorEventListener {
    private float impactX;
    private float impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 45;
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
        for(int i = 0; i<Objekt.liste.size(); i++){
            Objekt.liste.get(i).setDirection(Objekt.liste.get(i).getDirection() + (int)(Math.random() * directionChange - directionChange / 2));
            Objekt.liste.get(i).setX((float)(Objekt.liste.get(i).getX() + Math.cos(Objekt.liste.get(i).getDirection()) * Objekt.liste.get(i).getSpeed()));
            Objekt.liste.get(i).setY((float)(Objekt.liste.get(i).getY() + Math.sin(Objekt.liste.get(i).getDirection()) * Objekt.liste.get(i).getSpeed()));
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
