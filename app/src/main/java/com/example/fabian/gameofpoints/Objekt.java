package com.example.fabian.gameofpoints;

import android.graphics.Color;

public class Objekt {

    private int membership;
    private int live;
    private int attack;
    private int speed;
    private double direction;
    private Color color;

    public Objekt(int memb2, int live2, int attack2, int speed2, Color color2){
        membership = memb2;
        live = live2;
        attack = attack2;
        speed = speed2;
        color = color2;
        direction = Math.random()*4; //random: 0, 1, 2, 3
    }

    public void move(){

    }

    public int getMembership() {
        return membership;
    }

    public int getLive() {
        return live;
    }

    public int getAttack() {
        return attack;
    }

    public int getSpeed() {
        return speed;
    }

    public Color getColor() {
        return color;
    }

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
