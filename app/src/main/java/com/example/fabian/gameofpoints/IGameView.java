package com.example.fabian.gameofpoints;

import android.graphics.RectF;
import android.graphics.Typeface;

public interface IGameView {
    void setPosition(float x, float y);

   // void setTimer();

    void setPoints(int points);

   // void setTotalPoints(int totalPoints);


    float getBaseDimension();

    int getFps();
}
