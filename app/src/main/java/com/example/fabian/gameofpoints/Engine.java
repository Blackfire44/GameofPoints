package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Engine implements SensorEventListener {
    private float impactX;
    private float impactY;
    private float minX, maxX, minY, maxY;
    private float scaleA = 100f;

    private MasterView masterview;
    private SensorManager sensorManager;

    public Engine(SensorManager sensorManager, MasterView masterview, MainActivity mainActivity){
        this.masterview = masterview;
        this.sensorManager = sensorManager;




        for(int i = 0; i<10; i++){
            Objekt a = new Objekt(100, 100, 20, 20, 0, 1, 3, 2, 0xffffff00);
        }
    }

    public void moveObjects(){
        for(int i = 0; i<Objekt.liste.size(); i++){

            //Rechnung für die Bewegung
            //Mit einberechnet: Richtung(2), Richtungsänderung(Kann Teil von Speed sein), speed, Position(x,y)

            Objekt.liste.get(i).setX(1);
            Objekt.liste.get(i).setY(1);
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
