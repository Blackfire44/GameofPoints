package com.example.fabian.gameofpoints;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Engine implements SensorEventListener {
    private float impactX, impactY;
    private float minX, maxX, minY, maxY;
    private int directionChange = 44;
    private float scaleA = 100f;
    private int msPerFrame = 30;
    private int timer;
    private int touched;

    private Objekt a;

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor sensor;
    private GameActivity gameActivity;
    private ScheduledExecutorService service;


    public Engine(SensorManager sensorManager, GameView gameView, GameActivity gameActivity){ //Konstruktor, die anderen Objekte werden übernommen
        this.gameView = gameView;
        this.sensorManager = sensorManager;
        this.gameActivity = gameActivity;
    }

    public void start(){ //Das Spiel wird erstellt
        service = Executors.newSingleThreadScheduledExecutor();

        final Runnable runnable = new Runnable(){ //Das Runnable geht jeden Tick des Spiels die benötigten Funktionen durch
            @Override
            public void run() {
                moveObjects(); //Die Objekte werden bewegt
                timer++;  //Der Timer wird auf Sekunden umgerechnet und gibt sobald eine Sekunde abgelaufen ist, dies an den gameView weiter
                if(timer>1000/msPerFrame){
                    timer=0;
                    gameView.setCountdown();
                }
                gameView.setData(); //Der GameView wird aufgefordert, so bald wie möglich neu zu zeichnen
            }
        };
        service.scheduleAtFixedRate(runnable, msPerFrame, msPerFrame, TimeUnit.MILLISECONDS); //Der Service lässt alle "msPerFrame" das Runnable einmal durchlaufen

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //Der Sensor wird auf den Accelerometer gesetzt
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void testFinish(){
        boolean stammgut = false;       //geht alle Viecher durch, solange ein Viech aus beiden Stämmen lebt, werde beide Variablen wieder auf true gesetzt und Süiel geht weiter
        boolean stammböse = false;
        for(int i = 0; i<Objekt.getListe().size();i++){
            if(Objekt.getObjekt(i).getLife()>0){
                if(Objekt.getObjekt(i).getMembership()==1){
                    stammgut=true;
                }else{
                    stammböse=true;
                }
            }
        }
        if(stammgut==false||stammböse==false){ //Wenn ein Stamm ausgelöscht ist
            if(stammböse==false){
                gameActivity.endGame(true, gameView.getCountdown()); //Gibt den Countdown weiter, um ihn in gameover.xml anzuzeigen, das Spiel ist gewonnen
            }else{
                gameActivity.endGame(false, gameView.getCountdown()); //Das Spiel ist verloren
            }
        }else {
            for (int i = 0; i < Objekt.getListe().size(); i++) {
                if (Objekt.getObjekt(i).getLife() > 0 && Objekt.getObjekt(i).getControl()) { //Wenn gute Viecher gewonnen haben
                    setData(i); //Die Werte werden aktualisiert, sollte das Objekt Leben verloren haben, da sihc dann die Anzeige ändern muss
                }
            }
        }
    }

    public void createObjekt(int x, int y, int membership, int life, int attack, int speed, int color){     //erstellt neues Viech
        a = new Objekt(x, y, membership, life, attack, speed, color);
    }

    public void setSelect(int objekt){      //wenn angetipptes Viech lieb, dann wird es ausgewählt
        for(int i = 0; i<Objekt.getListe().size(); i++){ //Die Kontrolle über das letzte Objekt wird entfernt
            Objekt.getObjekt(i).setControl(false);
        }
        Objekt.getObjekt(objekt).setControl(true); //Die Kontrolle wird auf das neue Objekt gesetzt
        setData(objekt); //Die Werte des neuen Objekts werden übernommen
    }

    private void setData(int objekt){
        gameActivity.setData(Objekt.getObjekt(objekt).getLife(), Objekt.getObjekt(objekt).getAttack(), Objekt.getObjekt(objekt).getSpeed()); //gibt Daten von Viech für die Anzeige der Daten in activity_game.xml
    }

    public void setRegion(float minX, float minY, float maxX, float maxY){  //Setzt Randpositionen
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }

    public void prüfeTouch(int touchX, int touchY){
        double highest = 1000000;
        int objekt = -1;
        for(int i = 0; i<Objekt.getListe().size(); i++){  //geht alle Viecehr durch
            if(Objekt.getObjekt(i).getMembership()==1&&Objekt.getObjekt(i).getLife()>0) { //Alle Viecher die noch leben und dem eigenen Team angehören
                double länge = Math.sqrt((Objekt.getObjekt(i).getX() - touchX) * (Objekt.getObjekt(i).getX() - touchX) + (Objekt.getObjekt(i).getY() - touchY) * (Objekt.getObjekt(i).getY() - touchY)); //Sucht das Objekt, das den gegebenen Koordinaten am nächsten ist (Satz des Pythagoras)
                if (highest > länge) { //es wird getestet, welches Objekt die kürzeste Entfernung hat, dieser Wert wird immer wieder gespeichert, solange er unterboten wird, so bleibt die kürzeste Entfernung übrig
                    highest = länge;
                    objekt = i; //Das Objet mit der bisher kürzesten Entfernung wird gespeichert
                }
            }
        }
        if(objekt!=-1) { //Wenn ein Objekt ausgewählt wirde, wird diesem Objekt der Select mitgeteilt
            setSelect(objekt);
        }
    }

    public void stop(){ //stoppt Spiel
        service.shutdown();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { //fragt Werte von Sensor ab, dabei ist der X-Wert an der 0ten Stelle im Array gespeichert und der Y-Wert an der 1ten Stelle
        impactX = -sensorEvent.values[0]*scaleA;
        impactY = sensorEvent.values[1]*scaleA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void moveObjects(){ //berechent, wie sich die Objekte bewegen sollen
        for(int i = 0; i<Objekt.getListe().size(); i++){ //geht alle Objekte durch
            if(Objekt.getObjekt(i).getLife()>0) { //wenndas Viech noch lebt
                float x = Objekt.getObjekt(i).getX(); //setzt X- und Y-Koordinaten
                float y = Objekt.getObjekt(i).getY();

                if(Objekt.getObjekt(i).getBreedTimer()<=200) { //Objekt nicht Paarungsfäig
                    boolean rand = false;
                    if(Objekt.getObjekt(i).getX()<minX+Objekt.getObjekt(i).getR()/2) {
                        Objekt.getObjekt(i).setX(minX+Objekt.getObjekt(i).getR()/2); //Das Objekt wird an den Rand gesetzt
                        setObjektToBorder(i); //Die Richtung wird umgekehrt
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getX()>maxX-Objekt.getObjekt(i).getR()/2) {
                        Objekt.getObjekt(i).setX(maxX-Objekt.getObjekt(i).getR()/2); //Das Objekt wird an den Rand gesetzt
                        setObjektToBorder(i); //Die Richtung wird umgekehrt
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getY()<minY+Objekt.getObjekt(i).getR()/2) {
                        Objekt.getObjekt(i).setY(minY + Objekt.getObjekt(i).getR() / 2); //Das Objekt wird an den Rand gesetzt
                        setObjektToBorder(i); //Die Richtung wird umgekehrt
                        rand=true;
                    }
                    if(Objekt.getObjekt(i).getY()>maxY-Objekt.getObjekt(i).getR()/2) {
                            Objekt.getObjekt(i).setY(maxY - Objekt.getObjekt(i).getR() / 2); //Das Objekt wird an den Rand gesetzt
                        setObjektToBorder(i); //Die Richtung wird umgekehrt
                        rand=true;
                    }
                    if(!rand) { //Objekt bewegt sich nicht, wenn es an einen Rand gestoßen ist
                        for(int o = 0; o<Objekt.getListe().size(); o++) {
                            if(i!=o && Objekt.getObjekt(o).getLife()>0 && Math.sqrt((Objekt.getObjekt(i).getX()-Objekt.getObjekt(o).getX())*(Objekt.getObjekt(i).getX()-Objekt.getObjekt(o).getX())+(Objekt.getObjekt(i).getY()-Objekt.getObjekt(o).getY())*(Objekt.getObjekt(i).getY()-Objekt.getObjekt(o).getY()))<Objekt.getObjekt(i).getR()/2+Objekt.getObjekt(o).getR()/2) { //Prüfung, ob zwei Objekte sich berühren (Satz des Pythagoras)
                                if(Objekt.getObjekt(i).getMembership()==Objekt.getObjekt(o).getMembership()) { //Wenn Viecher aus selben Stamm
                                    if(Objekt.getObjekt(i).getBreedState()==false) { //Wenn das Objekt, das gerade die Schleife durchgegangen ist das Andere als erstes trifft, ist das Attribut noch nicht gesetzt, welches definiert, ob man shon bei einer Paarung beteiligt ist. So wird nur ein Objekt erzeugt, da das erste Objekt dann den Brutvorgang leitet
                                        if(Objekt.getObjekt(i).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedTimer()==0&&Objekt.getObjekt(o).getBreedState()==false) {
                                            Objekt.getObjekt(i).setBreedTimer(400); //Cooldown für die Paarung wird hoch gesetzt
                                            Objekt.getObjekt(i).setPartner(o); //Viechpartner werden gegenseitig gesetzt
                                            Objekt.getObjekt(o).setPartner(i);
                                            Objekt.getObjekt(o).setBreedState(true); //Dem anderen Objekt wird klar gemacht, dass es schon an einer Paarung beteiligt ist
                                        }
                                    }else {
                                        Objekt.getObjekt(i).setBreedTimer(400); //Wenn dieses Objekt von einem Anderen getroffen wurde, das Andere aber zu erst da war, wird nur der BreedTimer hoch gesetzt
                                    }
                                }else {
                                    while(Objekt.getObjekt(i).getLife()>0&&Objekt.getObjekt(o).getLife()>0) { //Wenn Viecher aus unterschiedlichen Stämmen sich treffen, werden so lange Leben nach der Attacke des gegners abgezogen, bis ein Viech von beiden keinen positiven Lebenswert mehr hat
                                        Objekt.getObjekt(o).setLife(Objekt.getObjekt(o).getLife()-Objekt.getObjekt(i).getAttack()); //Dem Anderen Objekt werden so viele Leben abgezogen, wie das eigene Viech Angriffspunkte hat
                                        if(Objekt.getObjekt(o).getBreedTimer()<200) { //Wenn das Andere Objekt noch in der Paarung ist, wird nur ein Drittel der Attacke genutzt (Die Paarung läuft nur bis 200, das danach zählt dann als Cooldown)
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-Objekt.getObjekt(o).getAttack());//Dem Eigenen Objekt werden so viele Leben abgezogen, wie das Andere Viech Angriffspunkte hat
                                        }else {
                                            Objekt.getObjekt(i).setLife(Objekt.getObjekt(i).getLife()-(float)Objekt.getObjekt(o).getAttack()/3); //sonst wird 1/3 der Attackpunkte des Anderen Viechs genutzt
                                            if(Objekt.getObjekt(o).getLife()<=0) {
                                                Objekt.getObjekt(Objekt.getObjekt(o).getPartner()).setBreedTimer(0); //Wenn Objekt tot, wir Vortpflanzungscool-down von früherer Partenr  = 0 und kein neues Viech wird erzeugt
                                            }
                                        }
                                    }
                                    testFinish(); //testet, ob gestorbenes Viech das Letzte von einem Stamm war
                                }
                                Objekt.getObjekt(i).setDirection((int)(Math.acos((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())/(Math.sqrt((Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())*(Objekt.getObjekt(o).getX()-Objekt.getObjekt(i).getX())+(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())*(Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY()))))*180/Math.PI));
                                if((Objekt.getObjekt(o).getY()-Objekt.getObjekt(i).getY())<0){
                                    Objekt.getObjekt(i).setDirection(-Objekt.getObjekt(i).getDirection());
                                }
                                Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection()-180);
                                touched = 2;
                                Objekt.getObjekt(i).setX((float)(Objekt.getObjekt(o).getX() + Math.cos(Objekt.getObjekt(i).getDirection()*Math.PI/180) * (Objekt.getObjekt(i).getR()/2+Objekt.getObjekt(o).getR()/2))); //Fehler in der direction berechnung
                                Objekt.getObjekt(i).setY((float)(Objekt.getObjekt(o).getY() + Math.sin(Objekt.getObjekt(i).getDirection()*Math.PI/180) * (Objekt.getObjekt(i).getR()/2+Objekt.getObjekt(o).getR()/2)));
                            }
                        }
                    }
                    if(Objekt.getObjekt(i).getGrow()==2) {
                        Objekt.getObjekt(i).setGrow(1);
                    }
                    if(touched==0) {    //Wenn das Objekt nichts berührt hat
                        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection() + (int)(Math.random() * directionChange - directionChange / 2));    //zufällige Richtung in eiem bestimmten Winkelbereich
                        if(Objekt.getObjekt(i).getGrow()!=0) {  //Während Viech wächst
                            Objekt.getObjekt(i).setNewR(); //setzt Wert von Radius neu
                        }
                    }else {
                        if(Objekt.getObjekt(i).getGrow()!=0&&touched==2) {
                            Objekt.getObjekt(i).setGrow(2); //Wenn das Objekt nicht ausgewachsen ist, aber ein Objekt berührt, wird das Wachsen unterbunden, bis der Platz vorhanden ist
                        }
                        touched = 0;
                    }
                }
                if(Objekt.getObjekt(i).getBreedTimer()>0&&Objekt.getObjekt(i).getGrow()!=2) { //Wenn nicht ausgewachsen oder noch im Paarungs-Cooldown
                    Objekt.getObjekt(i).setBreedTimer(Objekt.getObjekt(i).getBreedTimer()-1); //Das Objekt wächst etwas weiter
                }
                if(Objekt.getObjekt(i).getBreedTimer()==200&&Objekt.getObjekt(i).getGrow()==0) { //Wenn der BreedTimer am richtigen Punkt angekommen ist, wird ein neues Objekt erzeugt, dabei wird noch getestet, ob das nicht der Prozess des erwachsenwerdens ist, der auch über den BreedTimer läuft
                    if(Objekt.getObjekt(i).getBreedState()==false&&Objekt.getAnzViech()<=20) { //Nur bei einem der Objekte, die durch die Schleife laufen, darf ein Kind erzeugt werden
                        //new Viech
                        Objekt b = new Objekt((int)(Objekt.getObjekt(i).getX() + Math.cos((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), (int)(Objekt.getObjekt(i).getY() + Math.sin((Objekt.getObjekt(i).getDirection()-180)*Math.PI/180)*Objekt.getObjekt(i).getR()/2), Objekt.getObjekt(i).getMembership()); //Ein neues Objekt wird mit den schon sicheren Werten erzeugt
                        if((int)(Math.random()*2)==0) {
                            b.setLife(Objekt.getObjekt(i).getLifeSafe()); //Es bekommt zufällig die Werte von einem der Elternteile, pro Wert, zugewiesen
                            b.setLifeSafe(Objekt.getObjekt(i).getLifeSafe());
                        }else {
                            b.setLife(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getLifeSafe());
                            b.setLifeSafe(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getLifeSafe());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setAttack(Objekt.getObjekt(i).getAttack());
                        }else {
                            b.setAttack(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getAttack());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setSpeed(Objekt.getObjekt(i).getSpeed());
                        }else {
                            b.setSpeed(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getSpeed());
                        }
                        if((int)(Math.random()*2)==0) {
                            b.setColor(Objekt.getObjekt(i).getColor());
                        }else {
                            b.setColor(Objekt.getObjekt(Objekt.getObjekt(i).getPartner()).getColor());
                        }
                        //new Viech
                    }
                    Objekt.getObjekt(i).setBreedState(false); //
                }
            }
        }
        for(int i = 0; i<Objekt.getListe().size(); i++){ //Alle objekte werden bewegt, nachdem sie ihre richtungsänderungen etc. bekommen haben
            if(Objekt.getObjekt(i).getBreedTimer()<=201&&Objekt.getObjekt(i).getLife()>0&&Objekt.getObjekt(i).getControl()==false) { //Objekte bewegen sich nur dann automatisch, wenn sie noch leben, nicht von dem Spieler gesteuert werden und sich nicht gerade paaren
                Objekt.getObjekt(i).setX((float)(Objekt.getObjekt(i).getX() + Math.cos(Objekt.getObjekt(i).getDirection()*Math.PI/180) * Objekt.getObjekt(i).getSpeed()));//float statt int
                Objekt.getObjekt(i).setY((float)(Objekt.getObjekt(i).getY() + Math.sin(Objekt.getObjekt(i).getDirection()*Math.PI/180) * Objekt.getObjekt(i).getSpeed()));//float statt int
            }
            if(Objekt.getObjekt(i).getControl()==true&&Objekt.getObjekt(i).getBreedTimer()<=201&&Objekt.getObjekt(i).getLife()>0) { //Das Objekt, das von dem Spieler gesteuert wird, bewegt sich auch, wenn es lebt und sich nicht paart
                if(impactX>40){ //Hier habe ich, extra ab einem bestimmtn Wert des Sensors, damit das Objekt auch an einer Stelle stehen kann, dieses nur mit ihrem Speed bewegt, sodass es immer gleich schnell ist
                    Objekt.getObjekt(i).setX(Objekt.getObjekt(i).getX()+Objekt.getObjekt(i).getSpeed());
                }
                if(impactX<-40){
                    Objekt.getObjekt(i).setX(Objekt.getObjekt(i).getX()-Objekt.getObjekt(i).getSpeed());
                }
                if(impactY>40){
                    Objekt.getObjekt(i).setY(Objekt.getObjekt(i).getY()+Objekt.getObjekt(i).getSpeed());
                }
                if(impactY<-40){
                    Objekt.getObjekt(i).setY(Objekt.getObjekt(i).getY()-Objekt.getObjekt(i).getSpeed());
                }
            }
        }
    }

    private void setObjektToBorder(int i){ //180Grad Wend von einem Rand weg
        Objekt.getObjekt(i).setDirection(Objekt.getObjekt(i).getDirection()-180);
        touched = 1; //touched zeigt an, dass es einen Rand berührt hat
    }
}
