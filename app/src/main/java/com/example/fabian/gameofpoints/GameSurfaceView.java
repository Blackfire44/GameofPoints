package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
        getHolder().addCallback((SurfaceHolder.Callback) this);
    }

    private Runnable renderer = new Runnable() {
        @Override
        public void run() {
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                synchronized (getHolder()){
                    doDraw(canvas);
                }
            }
            finally {
                getHolder().unlockCanvasAndPost(canvas);
            }
            //Log.d(getClass().getSimpleName(), Integer.toString(getFpS()) + " fps");
        }
    };

    protected void doDraw(Canvas canvas){
        if(t == 0){
            t = System.currentTimeMillis();
            frames++;
        }
        BitmapDrawable sterne = (BitmapDrawable) getResources().getDrawable(R.drawable.sterne);
        sterneField.setBounds(0,0,sterne.getBitmap().getWidth(), sterne.getBitmap().getHeight());
        //canvas.drawBitmap(sterne.getBitmap(), sterneField, canvas.getClipBounds(), paintBitmap);

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
