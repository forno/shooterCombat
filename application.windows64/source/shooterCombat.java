import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class shooterCombat extends PApplet {

static final int MODE_OP = 0;
static final int MODE_MAIN = 1;
static final int MODE_RESULT = 2;
static final int MODE_ED = 3;
static final int MODE_WAIT_RELESE = 4;

EnemyCharacter enemys[];
Shot shots[];
PlayerCharacter player;
Card card;
MyKeyListener myKey;
int gameMode;
int score;
long frame;

public void setup() {
  enemys = new EnemyCharacter[10];
  shots = new Shot[100];
  myKey = new MyKeyListener();
  size(500,500);
  myInit();
}

public void draw() {
  background(255);
  gameMode = gameUpdate(gameMode);
  gameRender(gameMode);
}

public void myInit() {
  gameMode = MODE_OP;
  
  for (int i = 0; i < enemys.length; i++)
    enemys[i] = new EnemyCharacter(-100, -100, 15, 15);
  for (int i = 0; i < shots.length; i++)
    shots[i] = new Shot(-100, -100, 20, color(200, 50, 0));
  card = new Card(-100, -100);
  player = new PlayerCharacter(width/2, height * 9/10 - 20, 10, 10);
  score = 0;
  frame = 0;
}

public int gameUpdate(int gameMode) {
  switch(gameMode) {
  case MODE_OP:
    myInit();
    if (myKey.isKeyPressing(MyKeyListener.SPACE)) return MODE_MAIN;
    return MODE_OP;
  case MODE_MAIN:
    frame++;
    player.move(myKey, width, height * 9/10);
    for (int i = 0; i < shots.length; i++) if (shots[i].getUseFlag()) shots[i].move();
    for (int i = 0; i < enemys.length; i++) if (enemys[i].getUseFlag()) enemys[i].move();
    if (card.getUseFlag()) {
      if (card.getBurstFlag()) card.reset();
      else if (card.getChargingFlag()) {
        card.charge(60*3);
        if (myKey.isKeyPressing(MyKeyListener.W)) {
          card.setX(player.getX() + player.getWidth()/2 - card.getWidth()/2);
          card.setY(player.getY() - card.getHeight());
          card.setWidthP(2);
          card.setHeightP(1);
        } else if (myKey.isKeyPressing(MyKeyListener.A)) {
          card.setX(player.getX() - card.getWidth());
          card.setY(player.getY() + player.getHeight()/2 - card.getHeight()/2);
          card.setWidthP(1);
          card.setHeightP(2);
        } else if (myKey.isKeyPressing(MyKeyListener.S)) {
          card.setX(player.getX() + player.getWidth()/2 - card.getWidth()/2);
          card.setY(player.getY() + player.getHeight());
          card.setWidthP(2);
          card.setHeightP(1);
        } else if (myKey.isKeyPressing(MyKeyListener.D)) {
          card.setX(player.getX() + player.getWidth());
          card.setY(player.getY() + player.getHeight()/2 - card.getHeight()/2);
          card.setWidthP(1);
          card.setHeightP(2);
        } else {
          card.setChargingFlag(false);
          card.update(60*1);
        }
      } else {
        card.update(60*1);
        if (myKey.isKeyPressing(MyKeyListener.SPACE) && card.isEndedSetup()) card.setBurstFlag(true);
      }
    } else {
      if (myKey.isKeyPressing(MyKeyListener.W)) {
        card.setUseFlag(true);
        card.setX(player.getX() + player.getWidth()/2 - card.getWidth()/2);
        card.setY(player.getY() - card.getHeight());
      } else if (myKey.isKeyPressing(MyKeyListener.A)) {
        card.setUseFlag(true);
        card.setX(player.getX() - card.getWidth());
        card.setY(player.getY() + player.getHeight()/2 - card.getHeight()/2);
      } else if (myKey.isKeyPressing(MyKeyListener.S)) {
        card.setUseFlag(true);
        card.setX(player.getX() + player.getWidth()/2 - card.getWidth()/2);
        card.setY(player.getY() + player.getHeight());
      } else if (myKey.isKeyPressing(MyKeyListener.D)) {
        card.setUseFlag(true);
        card.setX(player.getX() + player.getWidth());
        card.setY(player.getY() + player.getHeight()/2 - card.getHeight()/2);
      }
    }

    for (int i = 0; i < enemys.length; i++) {
      if (enemys[i].getUseFlag()) {
        // check delete
        if (!enemys[i].checkContinue(width, height * 9/10)) enemys[i].reset();
        // generate shot
        if (frame/(60*10) + 1 > random(0, 60*10))
          for (int j = 0; j < shots.length; j++)
            if (!shots[j].getUseFlag()) {
              shots[j].setX(enemys[i].getX());
              shots[j].setY(enemys[i].getY());
              shots[j].setMove(random(0.5f, 3), atan2(player.getY() - shots[j].getY(), player.getX() - shots[j].getX()) + random(-PI/6, PI/6));
              shots[j].setAcceleration(random(-0.1f, 0.1f));
              shots[i].setTrunRadian(random(-PI/12, PI/12));
              shots[j].setUseFlag(true);
              break;
            }
        if (player.isCollide(enemys[i])) return MODE_ED;
      } else {
        // generate enemy
        if (frame/(60*10) + 1 > random(0, 60*1)) {
          enemys[i].setX(random(0, width));
          enemys[i].setY(50);
          enemys[i].setMove(random(0.5f, 3), atan2(player.getY() - enemys[i].getY(), player.getX() - enemys[i].getX()) + random(-PI/6, PI/6));
          enemys[i].setAcceleration(random(-0.01f, 0.01f));
          enemys[i].setUseFlag(true);
        }
      }
    }

    if (card.getBurstFlag()) {
      // burst task
      for (int i = 0; i < enemys.length; i++)
        if (enemys[i].getUseFlag() && enemys[i].isCollide(card)) {
          score++;
          enemys[i].reset();
        }
      for (int i = 0; i < shots.length; i++)
        if (shots[i].getUseFlag() && shots[i].isCollide(card)) {
          score++;
          shots[i].reset();
        }
      if (player.isCollide(card)) return MODE_ED;
    }

    for (int i = 0; i < shots.length; i++) {
      if (shots[i].getUseFlag()) {
        if (!shots[i].checkContinue(width, height * 9/10)) shots[i].reset();
        if (player.isCollide(shots[i])) return MODE_ED;
      }
    }

    return MODE_MAIN;
  case MODE_ED:
    if (myKey.isKeyPressing(MyKeyListener.SPACE)) {
      return MODE_WAIT_RELESE;
    }
    return MODE_ED;
  case MODE_WAIT_RELESE:
    if (!myKey.isKeyPressing(MyKeyListener.SPACE)) {
      return MODE_OP;
    }
    return MODE_WAIT_RELESE;
  }
  return MODE_ED;
}

public void gameRender(int gameMode) {
  background(0);
  // line width changed
  stroke(255);
  line(0, height * 9/10, width, height * 9/10);
  fill(255);
  textSize(12);
  textAlign(LEFT);
  text("arrow keys: move.  w,a,s,d keys: set CARD(set attack area, and you can charge)", 15, height * 9/10 + 12);
  text("space key: burst CARD(turn active to attack area), and next reading.", 15, height * 9/10 + 2 * 12);
  switch (gameMode) {
  case MODE_OP:
    fill(255);
    textSize(15);
    textAlign(CENTER);
    text("Please push space key to battle start.", width/2, height/2);
    text("If you check key config, you look down.",width/2, height/2 + 20);
    break;
  case MODE_MAIN:
    card.draw();
    for (int i = 0; i < shots.length; i++) shots[i].draw();
    for (int i = 0; i < enemys.length; i++) enemys[i].draw();
    player.draw();
    if (card.getChargingFlag()) {
      fill(20,200,60);
      textSize(13);
      textAlign(CENTER);
      text("Pushing to charge.", player.getX(), player.getY() + player.getHeight() + 18);
    }
    if (card.getEndedSetupFlag()) {
      fill(255,0,0);
      textSize(16);
      textAlign(CENTER);
      text("Space to attack!", card.getX() + card.getWidth()/2, card.getY() + card.getHeight()/2);
    }
    break;
  case MODE_ED: case MODE_WAIT_RELESE:
    fill(255);
    textSize(20);
    textAlign(CENTER);
    text("Your result", width/2, height/2 - 15);
    text(score, width/2, height/2 + 15);
    text("Push space for continue battle!", width/2, height * 2/3);
  }
}

public void keyPressed() {
  if (key == CODED)
    myKey.myKeyPressed(keyCode);
  else
    myKey.myKeyPressed(key);
}

public void keyReleased() {
  if (key == CODED)
    myKey.myKeyReleased(keyCode);
  else
    myKey.myKeyReleased(key);
}
/*
 *Copyright (c) 2014, Yusuke Doi
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
* Neither the name of the Yusuke Doi nor the names of its contributors 
  may be used to endorse or promote products derived from this software 
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Doi BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Yusuke Doi
 */
public abstract class BaseObject {

  // position point
  protected double x, y;
  // rect size
  protected int width, height;
  // speed variable
  protected double vx, vy, speed, radian;
  protected double acceleration, turnRadian;
  // flag variable
  protected boolean useFlag;


  public BaseObject(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public BaseObject() {}

  public void setMove(double speed, double radian) {
    this.speed = speed;
    this.radian = radian;
    vx = speed * Math.cos(radian);
    vy = speed * Math.sin(radian);
  }

  public void setVector(double vx, double vy) {
    this.vx = vx;
    this.vy = vy;
    speed = Math.hypot(vx, vy);
    radian = Math.atan2(vy, vx);
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }
  
  public void setWidth(int width){
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setSpeed(double speed) {
    setMove(speed, radian);
  }

  public void setRadian(double radian) {
    setMove(speed, radian);
  }

  public void setVx(double vx) {
    setVector(vx, vy);
  }

  /**
   * y speed changed. and recalculation radian or speed<br>
   * if change vx and vy, you use setVector(vx, vy)
   *
   * @param vy y speed
   */
  public void setVy(double vy) {
    setVector(vx, vy);
  }
  
  /**
   * acceleration changed.
   *
   * @param acceleration speed acceleration
   */
  public void setAcceleration(double acceleration) {
    this.acceleration = acceleration;
  }
  
  /**
   * turn speed changed.
   *
   * @param turnRadian turn speed.
   */
  public void setTrunRadian(double turnRadian) {
    this.turnRadian = turnRadian;
  }
  
  public void setUseFlag(boolean useFlag) {
    this.useFlag = useFlag;
  }

  /**
   * return left point of instance.
   *
   * @return left point
   */
  public int getX() {
    return (int)x;
  }

  /**
   * return top point of instance.
   *
   * @return top point
   */
  public int getY() {
    return (int)y;
  }

  /**
   * return instance's width. you can use hit task.
   *
   * @return width
   */
  public int getWidth() {
    return width;
  }

  /**
   * return instance's height. you can use hit task.
   *
   * @return height
   */
  public int getHeight() {
    return height;
  }

  /**
   * return instance's direction(radian) of speed vector
   *
   * @return speed
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * return instance's direction(radian) of speed vector
   *
   * @return direction(radian) of speed vector
   */
  public double getRadian() {
    return radian;
  }
  
  public boolean getUseFlag() {
    return useFlag;
  }

  /**
   * add x or y and speed vector
   */
  public void move() {
    x += vx;
    y += vy;
    setMove(speed + acceleration, radian + turnRadian);
  }

  /**
   * x or y changed that add speed vector
   * after, if move out on rect(0, 0, limitWidth, limitHeight), force move on rect
   * and force move running reverce radian.
   *
   * @param limitWidth limit width on rect
   * @param limitHeight limit height on rect
   */
  public void move(int limitWidth, int limitHeight) {
    move();

    if (x < 0) {
      x = 0;
      reverseVectorX();
    } else if (x > limitWidth - width) {
      x = limitWidth - width;
      reverseVectorX();
    }

    if (y < 0) {
      y = 0;
      reverseVectorY();
    } else if (y > limitHeight - height) {
      y = limitHeight - height;
      reverseVectorY();
    }
  }

  /**
   * x speed reversed. change radian and vx.
   */
  public void reverseVectorX() {
    vx = -vx;
    if (vy >= 0)
      radian = Math.PI - radian;
    else
      radian = -Math.PI - radian;
  }

  /**
   * y speed reversed. change to radian and vy.
   */
  public void reverseVectorY() {
    vy = -vy;
    radian = -radian;
  }

  /**
   * you have set draw task.
   */
  public abstract void draw();
}
class Card extends BaseObject {
  private int waitSetupCount;
  private int chargeCount;
  private boolean endedSetupFlag;
  private boolean burstFlag;
  private boolean chargingFlag;
  private float widthP, heightP;
  
  public Card(int x, int y) {
    super(x, y, 0, 0);
  }
  
  public void setUseFlag(boolean useFlag) {
    this.useFlag = useFlag;
    chargeCount = 0;
    chargingFlag = true;
  }
  
  public void setWidthP(float widthP) {
    this.widthP = widthP;
  }
  
  public void setHeightP(float heightP) {
    this.heightP = heightP;
  }
  
  public void setChargingFlag(boolean chargingFlag) {
    this.chargingFlag = chargingFlag;
  }
  
  public float getWidthP() {
    return widthP; 
  }
  
  public float getheightP() {
    return heightP;
  }
  
  public void charge(int limitCharge) {
    if (chargeCount < limitCharge) {
      chargeCount++;
    }
    setWidth((int)widthP * chargeCount);
    setHeight((int)heightP * chargeCount);
  }

    public void update(int finishCount) {
    if (++waitSetupCount  > finishCount) {
      endedSetupFlag = true;
      waitSetupCount = finishCount;
    }
  }

  public boolean isEndedSetup() {
    return endedSetupFlag;
  }

  public void setBurstFlag(boolean burstFlag) {
    this.burstFlag = burstFlag;
  }

  public boolean getChargingFlag() {
    return chargingFlag;
  }

  public boolean getBurstFlag() {
    return burstFlag;
  }
  
  public boolean getEndedSetupFlag() {
    return endedSetupFlag;
  }
  
  
  public void reset() {
    x = -100;
    y = -100;
    width = 0;
    height = 0;
    useFlag = false;
    burstFlag = false;
    endedSetupFlag = false;
    waitSetupCount = 0;
    chargeCount = 0;
  }

  
  public void draw() {
    if (chargingFlag)
      fill(200, 50, 200);
    else if (endedSetupFlag)
      fill(100, 200, 50);
    else if (burstFlag)
      fill(255, 20, 20);
    else
      fill(200, 150, 50);
    rect((float)x, (float)y, width, height);
  }
}

class CharObj extends BaseObject {
  protected int charColor;
  
  public CharObj(int x, int y, int width, int height, int charColor) {
    super(x, y, width, height);
    this.charColor = charColor;
    useFlag = false;
  }
  public CharObj(int x, int y, int width, int height) {
    this(x, y, width, height, color(0, 0, 0));
  }
  
  public void draw() {
    fill(charColor);
    //triangle((float)x, (float)y, (float)x-width/2, (float)y+height, (float)x+width/2, (float)y+height);
  }
}
class EnemyCharacter extends CharObj {
  public EnemyCharacter(int x, int y, int width, int height) {
    super(x, y, width, height, color(170, 100, 50));
  }
  
  public void reset() {
    x = -100;
    y = -100;
    useFlag = false;
  }

  public boolean isCollide(Card card) {
    if (card.getX() < x + width && x < card.getX() + card.getWidth() && card.getY() < y + height && y < card.getY() + card.getHeight())
      return true;
    return false;
  }
  
  public boolean checkContinue(float limitWidth, float limitHeight) {
    if (vx > 0 && x - width/2 > limitWidth)
      return false;
    else if (vx < 0 && x + width/2 < 0)
      return false;
    if (vy > 0 && y - height > limitHeight)
      return false;
    else if (vy < 0 && y < 0)
      return false;

    return true;
  }
  
  public void draw() {
    fill(charColor);
    triangle((float)x, (float)y, (float)x-width/2, (float)y-height, (float)x+width/2, (float)y-height);
  }
}
public interface KeyListenerConstants {

  public static final int MAX_KEYS = 52;

  public static final int ZERO = 0;
  public static final int MY_ONE = 1;
  public static final int MY_TWO = 2;
  public static final int THREE = 3;
  public static final int FOUR = 4;
  public static final int FIVE = 5;
  public static final int SIX = 6;
  public static final int SEVEN = 7;
  public static final int EIGHT = 8;
  public static final int NINE = 9;
  public static final int MINUS = 10;
  public static final int yama = 11;
  public static final int KANE = 12;
  public static final int Q = 13;
  public static final int W = 14;
  public static final int E = 15;
  public static final int R = 16;
  public static final int T = 17;
  public static final int Y = 18;
  public static final int U = 19;
  public static final int I = 20;
  public static final int O = 21;
  public static final int P = 22;
  public static final int AT_MARK = 23;
  public static final int MEDIUM_KAKKO_START = 24;
  public static final int A = 25;
  public static final int S = 26;
  public static final int D = 27;
  public static final int F = 28;
  public static final int G = 29;
  public static final int H = 30;
  public static final int J = 31;
  public static final int K = 32;
  public static final int L = 33;
  public static final int SEMI_COLON = 34;
  public static final int COLON = 35;
  public static final int MEDIUMU_KAKKO_END = 36;
  public static final int Z = 37;
  public static final int X = 38;
  public static final int C = 39;
  public static final int V = 40;
  public static final int B = 41;
  public static final int N = 42;
  public static final int M = 43;
  public static final int COMMA = 44;
  public static final int PERIOD = 45;
  public static final int SLASH = 46;
  public static final int BACK_SLASH = 46;
  public static final int MY_LEFT = 47;
  public static final int MY_UP = 48;
  public static final int MY_RIGHT = 49;
  public static final int MY_DOWN = 50;
  public static final int SPACE = 51;
}
class MyKeyListener implements KeyListenerConstants {

  private boolean[] keys;

  public MyKeyListener() {
    keys = new boolean[MAX_KEYS];
  }

  /* (not Javadoc)
   * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
   */
  public void myKeyPressed(char keyNum) {
    switch (keyNum) {
    case 'w': case 'W':  keys[W] = true;  break;
    case 'a': case 'A':  keys[A] = true;  break;
    case 's': case 'S':  keys[S] = true;  break;
    case 'd': case 'D':  keys[D] = true;  break;
    case ' ': keys[SPACE] = true;  break;
    }
  }

  public void myKeyPressed(int keyCode) {
  switch (keyCode) {
    case LEFT:  keys[MY_LEFT] = true;  break;
    case UP:  keys[MY_UP] = true;  break;
    case RIGHT:  keys[MY_RIGHT] = true;  break;
    case DOWN:  keys[MY_DOWN] = true;  break;
    }
  }

  /* (not Javadoc)
   * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
   */
  public void myKeyReleased(char keyNum) {
    switch (keyNum) {
    case 'w': case 'W':  keys[W] = false;  break;
    case 'a': case 'A':  keys[A] = false;  break;
    case 's': case 'S':  keys[S] = false;  break;
    case 'd': case 'D':  keys[D] = false;  break;
    case ' ': keys[SPACE] = false;  break;
    }
  }
  
  public void myKeyReleased(int keyCode) {
  switch (keyCode) {
    case LEFT:  keys[MY_LEFT] = false;  break;
    case UP:  keys[MY_UP] = false;  break;
    case RIGHT:  keys[MY_RIGHT] = false;  break;
    case DOWN:  keys[MY_DOWN] = false;  break;
    }
  }

  public boolean isKeyPressing(int keyNum) {
    return keys[keyNum];
  }

  public boolean isKeyPressing2(char keyNum) {
    switch (keyNum) {
    case 'w': case 'W': return keys[W];
    case 'a': case 'A': return keys[A];
    case 's': case 'S': return keys[S];
    case 'd': case 'D': return keys[D];
    case ' ': return keys[SPACE];
    }

    return false;
  }
  
  public boolean isKeyPressing2(int keyCode) {
    switch (keyCode) {
    case LEFT:  return keys[MY_LEFT];
    case UP:  return keys[MY_UP];
    case RIGHT:  return keys[MY_RIGHT];
    case DOWN:  return keys[MY_DOWN];
    }

    return false;
  }

  public void reset() {
    for (int i = 0; i < keys.length; i++) keys[i] = false;
  }

}
class PlayerCharacter extends CharObj {
  public PlayerCharacter(int x, int y, int width, int height) {
    super(x, y, width, height, color(30, 100, 250));
  }
  
  public void move(MyKeyListener myKey, int limitWidth, int limitHeight) {
    vx = vy = 0;
    if (myKey.isKeyPressing(MyKeyListener.MY_LEFT))
      vx -= 3;
    if (myKey.isKeyPressing(MyKeyListener.MY_RIGHT))
      vx += 3;
    if (myKey.isKeyPressing(MyKeyListener.MY_UP))
      vy -= 3;
    if (myKey.isKeyPressing(MyKeyListener.MY_DOWN))
      vy += 3;
      
    x += vx;
    y += vy;
    
    if (x < width/2)
      x = width/2;
    else if (x > limitWidth - width/2)
      x = limitWidth - width/2;
    
    if (y < 0)
      y = height/2;
    else if (y > limitHeight - height)
      y = limitHeight - height;
  }

  public boolean isCollide(Card card) {
    if (card.getX() < x + width/6 && x - width/6 < card.getX() + card.getWidth() && card.getY() < y + height * 2/3 && y + height/3 < card.getY() + card.getHeight())
      return true;
    return false;
  }

  public boolean isCollide(Shot shot) {
    if (shot.getX() - shot.getSize()/2 < x + width/6 && x - width/6 < shot.getX() + shot.getSize()/2 && shot.getY() - shot.getSize() < y + height * 2/3 && y - height/3 < shot.getY() + shot.getSize()/2)
      return true;
    return false;
  }

  public boolean isCollide(EnemyCharacter enemy) {
    if (enemy.getX() - enemy.getWidth()/2.0f < x + width/2.0f && x - width/2.0f < enemy.getX() + enemy.getWidth()/2.0f && enemy.getY() < y + height * 2/3 && y + height/3 < enemy.getY() - enemy.getHeight())
      return true;
    return false;
  }
  
  public void draw() {
    fill(charColor);
    triangle((float)x, (float)y, (float)x-width/2, (float)y+height, (float)x+width/2, (float)y+height);
  }
}

public class Shot extends BaseObject {
  private int size;
  private int shotColor;
  
  public Shot(int x, int y, int size, int shotColor) {
    super(x, y, size, size);
    this.size = size;
    this.shotColor = shotColor;
  }

  public Shot(){
    this(0,0,0,color(0,0,0));
  }

  public void setColor(int shotColor) {
    this.shotColor = shotColor;
  }

  public void setSize(int size) {
    this.size = size;
    this.width = size;
    this.height = size;
  }
  
  public int getSize() {
    return size;
  }
  
  public void reset() {
    x = -100;
    y = -100;
    useFlag = false;
  }
  
  public boolean isCollide(Card card) {
    if (card.getX() < x + width && x < card.getX() + card.getWidth() && card.getY() < y + height && y < card.getY() + card.getHeight())
      return true;
    return false;
  }

  public boolean checkContinue(float width, float height) {
    boolean xOutFlag = false;
    boolean yOutFlag = false;
    while (turnRadian < 0) turnRadian += 2 * PI;
    while (2 * PI <= turnRadian) turnRadian -= 2 * PI;

    if (0 <= turnRadian && turnRadian < PI) {
      if (y < -size/2) return false;
    } else
      if (height + size/2 < y) return false;

    if ((0 <= turnRadian && turnRadian < PI/2) || (PI * 3/2 < turnRadian && turnRadian < 2 * PI)) {
      if (width + size/2 < x) return false;
    } else
      if (x < -size/2) return false;

    return true;
  }
  
   /**
   * x or y changed that add speed vector
   * after, if move out on rect(0, 0, limitWidth, limitHeight), force move on rect
   * and force move running reverce radian.
   *
   * @param limitWidth limit width on rect
   * @param limitHeight limit height on rect
   */
  public void move(int limitWidth, int limitHeight) {
    move();

    if (x < size/2) {
      x = size/2;
      reverseVectorX();
    } else if (x > limitWidth - size/2) {
      x = limitWidth - size/2;
      reverseVectorX();
    }

    if (y < size/2) {
      y = size/2;
      reverseVectorY();
    } else if (y > limitHeight - size/2) {
      y = limitHeight - size/2;
      reverseVectorY();
    }
  }

  public void draw() {
    fill(shotColor);
    ellipse((float)x, (float)y, size, size);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "shooterCombat" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
