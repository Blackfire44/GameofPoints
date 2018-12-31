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

import static java.util.concurrent.Executor.*;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder{

    private final static float size = 32;
    private float scale;
    private long t;
    private long frames;
    private BitmapDrawable sterneField;
    private ScheduledExecutorService executorService;

    public GameSurfaceView (Context context){
        super(context);
        scale = getResources().getDisplayMetrics().density;
        //getHolder().addCallback((SurfaceHolder.Callback) this);

    }
/*
    private Runnable renderer = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {


            //Log.d(getClass().getSimpleName(), Integer.toString(getFpS()) + " fps");
        }
    };
    */
    protected void draw1(int objektX, int objektY, int objektR, int objektC, int objektL, int objektA) {
      /* Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            synchronized (getHolder()) {
                doDraw(canvas);
            }
        } catch (Exception e) {
            getHolder().unlockCanvasAndPost(canvas);
        }*/
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(R.drawable.krokotest, null);
            for(int i = 0; i<=Objekt.getListe().size();i++) {
// Creating bitmap with attaching it to the buffer-canvas, it means that all the changes // done with the canvas are captured into the attached bitmap
                Canvas tempCanvas= new Canvas();
                Canvas buffCanvas = new Canvas();
                tempCanvas.setBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888));
                buffCanvas.setBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888));
// and then you lock main canvas
                System.out.println("TEST8");

                //buffCanvas.drawRect(40,60,70,10,p);

                buffCanvas = getHolder().lockCanvas();
                Canvas canvas = new Canvas();
                Paint pa1= new Paint();
                pa1.setColor(Color.RED);
                pa1.setStyle(Paint.Style.FILL);
                pa1.setStrokeWidth(50);

                canvas.drawLine(0, 0, 100, 100, pa1);
                //tempCanvas.drawBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888),4,5,p); // and etc
                System.out.println("TEST8");
// then you draw the attached bitmap into the main canvas
                //buffCanvas.drawBitmap(Bitmap.createBitmap((int) Objekt.getObjekt(i).getX(), (int) Objekt.getObjekt(i).getY(), Bitmap.Config.ARGB_8888), 3,2, p);
                System.out.println("TEST8");

// then unlocking canvas to let it be drawn with main mechanisms
                getHolder().unlockCanvasAndPost(buffCanvas);
                System.out.println("TEST9");
                //Bitmap b = BitmapFactory.decodeResource(getResources(), i);
                //drawable.draw(drawable);
                System.out.println("TEST10");
            }//https://stackoverflow.com/questions/6538423/double-buffering-in-java-on-android-with-canvas-and-surfaceview#6538623
        }
       // drawable.setBounds(objektX, objektY, objektR, objektC);
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

    public int getFpS(){
        long delta = System.currentTimeMillis() - t;
        if(delta<1000) {
            return 10;
        }
        else{
            return (int) (frames/(delta/1000));
        }
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
        return null;
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
