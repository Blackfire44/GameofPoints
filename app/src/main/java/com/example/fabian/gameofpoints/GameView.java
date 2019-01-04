package com.example.fabian.gameofpoints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class GameView extends View {
    private  final static float size = 32;
    private float scale, Vx, Vy;

    private BitmapDrawable color;
    private BitmapDrawable background;

    private Rect viechRect = new Rect();
    private RectF drawRect = new RectF();

    private Paint paintBitmap = new Paint();
    
    
    public GameView(Context context) {
        super(context);
        scale = getResources().getDisplayMetrics().density;
        paintBitmap.setAntiAlias(true);

    }

    public void setBackground(int backgroundid){
        background = (BitmapDrawable) getResources().getDrawable(backgroundid);
    }

    public void setData() {
        invalidate();       
    }

    public float getBaseDimension() {
        return scale*size;
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(background.getBitmap(), getMatrix(), paintBitmap);
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