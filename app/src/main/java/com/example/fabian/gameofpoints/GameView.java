package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class GameView extends View {
    private  final static float size = 32;
    private float scale, Vx, Vy;
    private int countdown;

    private BitmapDrawable color;
    private BitmapDrawable background;

    private Rect viechRect = new Rect();
    private RectF drawRect = new RectF();

    private Paint paintBitmap = new Paint();
    private Paint paintText = new Paint();
    
    
    public GameView(Context context) {
        super(context);
        scale = getResources().getDisplayMetrics().density;
        paintBitmap.setAntiAlias(true);
        paintText.setAntiAlias(true);
        paintText.setColor(Color.rgb(255,255,255));
        paintText.setTextSize(scale*30);
        paintText.setStyle(Paint.Style.FILL);
    }

    public void setBackground(int backgroundid){
        background = (BitmapDrawable) getResources().getDrawable(backgroundid);
    }

    public void setCountdown(){
        countdown += 1;
    }

    public int getCountdown(){
        return countdown;
    }

    public void setData() {
        invalidate();       
    }

    public float getBaseDimension() {
        return scale*size;
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawText(Integer.toString(countdown), 10*scale, canvas.getHeight()-10*scale, paintText);
        for(int i = 0; i<Objekt.getListe().size(); i++){
            if(Objekt.getObjekt(i).getLife()>0){
                Vx = Objekt.getObjekt(i).getX();
                Vy = Objekt.getObjekt(i).getY();
                drawRect.set(Vx - (int) Objekt.getObjekt(i).getR() / 2, Vy - (int) Objekt.getObjekt(i).getR() / 2, Vx + (int) Objekt.getObjekt(i).getR() / 2, Vy + (int) Objekt.getObjekt(i).getR() / 2);
                color = (BitmapDrawable) getResources().getDrawable(Objekt.getObjekt(i).getColor());
                viechRect.set(0,0,color.getBitmap().getWidth(), color.getBitmap().getHeight());
                canvas.drawBitmap(color.getBitmap(), viechRect, drawRect, paintBitmap);
            }
        }
    }
}