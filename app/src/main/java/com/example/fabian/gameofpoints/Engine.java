package com.example.fabian.gameofpoints;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.style.UpdateLayout;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Engine implements SensorEventListener {
    private float impactX;
    private float impactY;
    private int stamm1;// lieb == 1
    private int stamm2;// böse == 2
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;
    private int msPerFrame = 30;
    private int anzViech = 10;
    private int touched;
    private ArrayList<Integer> stamm = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private GameSurfaceView gameSurfaceView;
    private SensorManager sensorManager;
    private GameActivity gameActivity;
    private ScheduledExecutorService service;

    public Engine(SensorManager sensorManager, GameSurfaceView gameSurfaceView, GameActivity gameActivity){ //Für Klassendaigramm noch überprüfen, vielleicht alles in der Engine machen (Masteview wird in der Engine deklariert)
        this.gameSurfaceView = gameSurfaceView;
        this.sensorManager = sensorManager;
        this.gameActivity = gameActivity;
        for(int i = 0; i<8; i++){
            int e = 450+i*50;
            Objekt a = new Objekt(800, e, 1, 2, 1, 1, 5);
        }
    }

    public void start(){
        service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
               moveObjects();
               repaintAction();
            }
        };
        service.scheduleAtFixedRate(runnable, msPerFrame, msPerFrame, TimeUnit.MILLISECONDS);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        aktualisiereDiagramm();
    }

    public void createObjekt(int x, int y, int membership, int life, int attack, int speed, int color){
        Objekt a = new Objekt(x, y, membership, life, attack, speed, color);
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

    public void repaintAction() {
        System.out.println("beep");
        for(int i = 0; i<10;i++){ //Objekt.getListe().size()
            System.out.print("beep2");
            float a = (float) Objekt.getObjekt(i).getX();
            float b = (float) Objekt.getObjekt(i).getY();
            float c = (float) Objekt.getObjekt(i).getR();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                gameSurfaceView.draw(a,b,c);
                System.out.print("IRGENDWIE FUNKTIONIERT ES; ABER DANN WIEDER DOCH NCIHT :(!!!!");
            }
            else{
                System.out.print("DAA DRAW ZEUG IST DOOOOOOOOOOOOOOOOOOOOOOOOOOOOOF!!!");
            }

            //  draw.drawCircle(Objekt.getObjekt(i).getX(), Objekt.getObjekt(i).getY(), Objekt.getObjekt(i).getR(), paint);
       }

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
                    if(Objekt.getObjekt(i).getX()<minX) {
                        Objekt.getObjekt(i).setX(minX);
                        setObjektToBorder(i);
                    }else {
                        if(Objekt.getObjekt(i).getX()>maxX) {
                            Objekt.getObjekt(i).setX(maxX);
                            setObjektToBorder(i);
                        }else {
                            if(Objekt.getObjekt(i).getY()<minY) {
                                Objekt.getObjekt(i).setY(minY);
                                setObjektToBorder(i);
                            }else {
                                if(Objekt.getObjekt(i).getY()>maxY) {
                                    Objekt.getObjekt(i).setY(maxY);
                                    setObjektToBorder(i);
                                }else{
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
                                                aktualisiereDiagramm();
                                            }
                                            Objekt.getObjekt(i).setDirection((int)(Math.acos((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())/(Math.sqrt((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())*(Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())+(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())*(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY()))))*180/Math.PI));
                                            if((Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())>0){
                                                Objekt.getObjekt(i).setDirection(-Objekt.getObjekt(i).getDirection());
                                            }
                                            touched = 2;
                                        }
                                    }
                                }
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
                    if(Objekt.getObjekt(i).getBreedState()==false) {
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
            //if(Objekt.getObjekt(i).getControl()==true) {
                //Bewegung des Gesteuerten Objects (wahrscheinlich mit den Neigungspunkten den Winkel ausrechnen und dann damit die X und Y Werte ausrechnen) (Oder die jeweiligen Werte mlal den Speed rechnen)
               // Objekt.getObjekt(i).setX(Objekt.getObjekt(i).getX()+controlX/1);//noch auf 1 zurück/vorrechnen
               // Objekt.getObjekt(i).setY(Objekt.getObjekt(i).getY()+controlY/1);//noch auf 1 zurück/vorrechnen
            //}
        }
    }

    private void setObjektToBorder(int i){
        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection()-180);
        touched = 1;
    }

    private void aktualisiereDiagramm(){
        stamm1 = 0;
        stamm2 = 0;
        for(int i = 0; i<Objekt.getListe().size(); i++) {
            switch(Objekt.getObjekt(i).getMembership()) {
                case 1:stamm1++;
                    break;
                case 2:stamm2++;
                    break;
                default:
            }
           gameActivity.setDiagramm(stamm1, stamm2+stamm1);
        }
    }
}
