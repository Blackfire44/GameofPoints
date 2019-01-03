package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CheckedOutputStream;

import static android.util.Log.*;
import static java.util.concurrent.Executor.*;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder{

    private final static float size = 32;
    private float scale,x,y;
    private long t;
    private long frames;
    private ScheduledExecutorService executorService;
    private boolean able;
    private Thread thread = null;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;

    public GameSurfaceView (Context context){
        super(context);
        scale = getResources().getDisplayMetrics().density;
       // sterneField = BitmapFactory.decodeResource(getResources(),R.drawable.krokotest);
        //getHolder().addCallback((SurfaceHolder.Callback) this);



    }
/*
    private Runnable renderer = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {


            //Log.d(getClass().getSimpleName(), Integer.toString(getFpS()) + " fps");
        }
    };*/

    public Bitmap sterneField;
    protected void draw1() {

        //Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            sterneField = BitmapFactory.decodeResource(getResources(),R.drawable.krokotest);
            //drawable = getResources().getDrawable(R.drawable.krokotest, null);
            for (int i = 0; i <= Objekt.getListe().size(); i++) {
                surfaceHolder = getHolder();
               // Canvas tempCanvas = new Canvas();
                //Canvas buffCanvas = new Canvas();
               // tempCanvas.setBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888));
               // buffCanvas.setBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888));
                //buffCanvas.drawRect(40,60,70,10,p);

               // if(Objekt.getObjekt(i).getMembership()==1) {
                canvas = surfaceHolder.lockCanvas();
                try {
                    canvas.drawBitmap(sterneField, Objekt.getObjekt(i).getX()-(Objekt.getObjekt(i).getR()/2), Objekt.getObjekt(i).getY()-(Objekt.getObjekt(i).getR()/2), null); //Diese zeile funktioniert nicht, obwohl sie das, laut wirklich allem was ich bisher finden konnte, wirklich tuen sollte. Deshalb geht die nächte Zeile auch nicht, weil nicht im surfce steht, was released werden könnte!
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("CREATION", "TEST");
                try {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("CREATION", "TEST");
            }//https://stackoverflow.com/questions/6538423/double-buffering-in-java-on-android-with-canvas-and-surfaceview#6538623
        }
    }

    protected void doDraw(Canvas canvas){
        if(t == 0){
            t = System.currentTimeMillis();
            frames++;

        }
        /*
        BitmapDrawable sterne = (BitmapDrawable) getResources().getDrawable(R.drawable.hintergrund1);
        Log.d("CREATION", "TEST-0.5");
        sterneField.setBounds(0,0,sterne.getBitmap().getWidth(), sterne.getBitmap().getHeight());
        canvas.drawBitmap(sterne.getBitmap(), sterneField, canvas.getClipBounds(), paintBitmap);
        Log.d("CREATION", "TEST-0.25");
        */

    }
    public float getBaseDimension(){
        return(scale*size);
    }
/*
    public int getFpS(){
        long delta = System.currentTimeMillis() - t;
        if(delta<1000) {
            return 10;
        }
        else{
            return (int) (frames/(delta/1000));
        }
    }*/

    public void stop(){
        able = false;
        while (true){
            try {
                thread.join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            break;
        }
        thread  = null;
    }

    @Override
    public void addCallback(Callback callback) {

    }

    @Override
    public void removeCallback(Callback callback) {

    }

    @Override
    public boolean isCreating() {
        return false;
    }

    @Override
    public void setType(int i) {

    }

    @Override
    public void setFixedSize(int i, int i1) {

    }

    @Override
    public void setSizeFromLayout() {

    }

    @Override
    public void setFormat(int i) {

    }

    @Override
    public Canvas lockCanvas() {
        return canvas;
    }

    @Override
    public Canvas lockCanvas(Rect rect) {
        return null;
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {

    }

    @Override
    public Rect getSurfaceFrame() {
        return null;
    }

    @Override
    public Surface getSurface() {
        return null;
    }
}
