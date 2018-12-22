package com.example.fabian.gameofpoints;

import java.util.ArrayList;

public class Objekt {

    private int membership;
    private float life;
    private int lifeSafe;
    private int attack;
    private int speed;
    private int direction;
    private int breedTimer; //bitte noch hinzufügen
    private boolean breedState; // einfügen
    private int partner;
    private boolean control;
    private int grow;

    private int color;

    private float x, y, r;

    private static ArrayList<Objekt> liste = new ArrayList<>();

    public Objekt(int x, int y, int membership, int life, int attack, int speed, int color){ //radius entfernen und durch Live ersetzen
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
        direction = (int)(Math.random()*360); //random eine Anfangsgradzahl berechnen
        liste.add(this);
    }

    public Objekt(int x, int y, int membership) {
        this.x = x;
        this.y = y;
        this.membership = membership;
        breedTimer = 200;//auch unten bei setR() ändern
        breedState = false;
        control = false;
        grow = 1;
        r = 10;
        direction = (int)(Math.random()*360); //random eine Anfangsgradzahl berechnen
        liste.add(this);
    }

    public static ArrayList<Objekt> getListe() {
        return liste;
    }

    public static Objekt getObjekt(int i) {
        return liste.get(i);
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

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public void setLife(float life) {
        this.life = life;
        setNewR();
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

    public void setR() {
        r = life*2+10;
    }

    public void setNewR() {
        if(grow==0) {
            setR();
        }else {
            if(breedTimer==0) {
                grow = 0;
                life = lifeSafe;
                setR();
            }else {
                if(r<life*2+10) {
                    r = r + (life*2-r+10)/breedTimer;//oben im konstruktor auch mitändern
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
