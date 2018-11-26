package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.View;

public class MasterView extends View{
    private final static float size = 32;
    private float scale;
    private long t;
    private long frames;

    public MasterView(Context context){
        super(context);
        scale = getResources().getDisplayMetrics().density;
    }


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
        float getBaseDimension();
        void setTypeface(Typeface typeface);
        int getFpS();

    }

}
