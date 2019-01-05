package com.example.fabian.gameofpoints;

import java.util.ArrayList;

public class Objekt {

    private int membership;
    private float life;
    private int lifeSafe;
    private int attack;
    private int speed;
    private int direction;
    private int breedTimer;
    private boolean breedState;
    private int partner;
    private boolean control;
    private int grow;
    private static int anzViech = 0;
    private int color;

    private float x, y, r;

    private static ArrayList<Objekt> liste = new ArrayList<>();

    public Objekt(int x, int y, int membership, int life, int attack, int speed, int color){ //Die Anfangswerte bei der Erschaffung vor dem Spielstart werden gesetzt
        this.x = x;
        this.y = y;
        this.membership = membership;
        this.life = life;
        lifeSafe = life;
        this.attack = attack;
        this.speed = speed;
        this.color = color;
        breedTimer = 0;
        breedState = false;
        control = false;
        grow = 0;
        setR();
        direction = (int)(Math.random()*360); //Zufällig wird eine Anfangsgradzahl berechnet
        liste.add(this);
        anzViech++;
    }

    public Objekt(int x, int y, int membership) { //Bei Erzeugung während des Spiels werden die nun noch nötigen Werte gesetzt, da die anderen Werte von den Eltern noch nachträglich eingetragen werden müssen
        this.x = x;
        this.y = y;
        this.membership = membership;
        breedTimer = 200;
        breedState = false;
        control = false;
        grow = 1;
        r = 10; //Der Radius bekommt einen Anfangswert, bevor er größer wird
        direction = (int)(Math.random()*360); //Zufällig wird eine Anfangsgradzahl berechnet
        liste.add(this);
        anzViech++;
    }

    public static ArrayList<Objekt> getListe() { return liste; } //Getter/Setter \/ + setLife(), setR(), setNewR()

    public static Objekt getObjekt(int i) {
        return liste.get(i);
    }

    public static int getAnzViech() {
        return anzViech;
    }

    public int getMembership() {
        return membership;
    }

    public float getLife() {
        return life;
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

    public int getBreedTimer() {
        return breedTimer;
    }

    public boolean getBreedState() {
        return breedState;
    }

    public int getDirection(){
        return direction;
    }

    public int getLifeSafe(){
        return lifeSafe;
    }

    public int getPartner(){
        return partner;
    }

    public boolean getControl() {
        return control;
    }

    public int getGrow() {
        return grow;
    }

    public void setLife(float life) {
        this.life = life;
        setNewR(); //Der Radius wird den Leben angepasst
        if(life<=0){ //Mit einem Tod wird auch die Anzahl der Viecher minimiert
            anzViech--;
        }
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

    public void setR() { //Radius wird anhand der Leben gesetzt, mit einem Anfangswert
        r = life*5+100;
    }

    public void setNewR() { //Der Radius wird gesetzt
        if(grow==0) { //Wenn es erwachsen ist, wird der normale Radius anhand der Leben gesetzt
            setR();
        }else {
            if(breedTimer==0) { //Wenn der breedTimer abgelaufen ist, ist das Objekt Erwachsen und bekommt die normale Größe anhand der Leben
                grow = 0;
                setR();
            }else {
                if(r<life*5+100) {
                    r = r + (life*5-r+100)/breedTimer; //Der Radius wird im Verhältniss zu den Leben und der Zeit zum erwachsenwerden erhöht
                }else {
                    setR();
                }
            }
        }

    }

    public void setDirection(int direction){
        this.direction = direction;
    }

    public void setBreedTimer(int breedTimer) {
        this.breedTimer = breedTimer;
    }

    public void setBreedState(boolean breedState) {
        this.breedState = breedState;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public void setGrow(int grow) {
        this.grow = grow;
    }

    public void setLifeSafe(int lifeSafe) {
        this.lifeSafe = lifeSafe;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }
}
