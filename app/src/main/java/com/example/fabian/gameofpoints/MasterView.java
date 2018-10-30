package com.example.fabian.gameofpoints;

import android.content.Context;
import android.view.View;

public class MasterView extends View{
    private final static float size = 32;
    private float scale;

    public MasterView(Context context){
        super(context);
        scale = getResources().getDisplayMetrics().density;
    }


    public float getBaseDimension(){
        return(scale*size);
    }
}
