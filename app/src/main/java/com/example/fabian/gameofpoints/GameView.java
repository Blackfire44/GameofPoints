package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class GameView extends View implements IGameView{
    private  final static float size = 0;
    private float scale, Vx, Vy;
    private int points;
    private Rect viechRect = new Rect();
    private RectF drawRect = new RectF();
    private BitmapDrawable viech;
    private Paint paintBitmap = new Paint();
    private long t, frames;
    
    
    public GameView(Context context) {
        super(context);
        viech = (BitmapDrawable) getResources().getDrawable(R.drawable.krokotest);
        paintBitmap.setAntiAlias(true);
        for(int i = 0; i<Objekt.getListe().size(); i++) {
            drawRect.set(Vx - size * scale / 2, Vy - size * scale / 2, Vx + size * scale / 2, Vy + size * scale / 2);
            viechRect.set((int) (Objekt.getObjekt(i).getX()-(Objekt.getObjekt(i).getR()/2)),(int) (Objekt.getObjekt(i).getY()-(Objekt.getObjekt(i).getR()/2)),(int) Objekt.getObjekt(i).getR(),(int) Objekt.getObjekt(i).getR());
            }
        frames++;
    }

    @Override
    public void setPosition(float x, float y) {
        Vx = x;
        Vy = y;
        invalidate();       
    }

    @Override
    public void setPoints(int points) {

    }

    @Override
    public float getBaseDimension() {
        return scale*size;
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        if(t == 0) t = System.currentTimeMillis();
        canvas.drawBitmap(viech.getBitmap(),viechRect,drawRect,paintBitmap);
    }

    @Override
    public int getFps() {
        long delta = System.currentTimeMillis() - t;
        if(delta<1000) return 0;
        return (int) (frames/(delta /1000));
    }

}
