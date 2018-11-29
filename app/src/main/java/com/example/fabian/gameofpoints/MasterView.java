package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public abstract class MasterView extends SurfaceView{
    private final static float size = 32;
    private float scale;
    private long t;
    private long frames;

    public MasterView(Context context){
        super(context);
        scale = getResources().getDisplayMetrics().density;
        getHolder().addCallback((SurfaceHolder.Callback) this);
    }

    public abstract void surfaceCreated(SurfaceHolder surfaceHolder);

    public abstract void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

    public abstract void surfaceDestroyd(SurfaceHolder surfaceHolder);

    public float getBaseDimension(){
        return(scale*size);
    }
    protected void onDraw(Canvas canvas){
        if(t==0) t = System.currentTimeMillis();
        frames++;
    }
    public int getFpS(){
        long delta = System.currentTimeMillis() - t;
        if(delta<1000) {
            return 0;
        }
        else{
            return (int) (frames/(delta/1000));
        }
    }

    public interface IGameView{
        void clearOpstacles();
        void setCount(int Count);
        void setPoints(int Points);
        void setTotalPoints(int totalPoints);
        void setStars( int stars);
        void setTotalStars(int totalStars);

        void surfaceCreated(SurfaceHolder surfaceHolder);

        void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

        void surfaceDestroyd(SurfaceHolder surfaceHolder);

        float getBaseDimension();
        void setTypeface(Typeface typeface);
        int getFpS();

    }

}
