package com.example.fabian.gameofpoints;

import java.util.ArrayList;

public class Objekt {

    private int membership;
    private int live;
    private int attack;
    private int speed;
    private double direction;
    private int color;

    private float x, y, r;

    public static ArrayList<Objekt> liste = new ArrayList<>();   //bitte zugriffsrecht ändern, falls es nicht gebraucht wird!!

    public Objekt(int x, int y, int r, int membership, int live, int attack, int speed, int color){
        this.x = x;
        this.y = y;
        this.r = r;
        this.membership = membership;
        this.live = live;
        this.attack = attack;
        this.speed = speed;
        this.color = color;
        direction = Math.random()*4; //random: 0, 1, 2, 3
        liste.add(this);
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

    public int getColor() {
        return color;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getR() {
        return r;
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

    public void setColor(int color) {
        this.color = color;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }

    public void setR(float r) {
        this.r = r;
    }
}
