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

void setup() {
  enemys = new EnemyCharacter[10];
  shots = new Shot[100];
  myKey = new MyKeyListener();
  size(500,500);
  myInit();
}

void draw() {
  background(255);
  gameMode = gameUpdate(gameMode);
  gameRender(gameMode);
}

void myInit() {
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

int gameUpdate(int gameMode) {
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
              shots[j].setMove(random(0.5, 3), atan2(player.getY() - shots[j].getY(), player.getX() - shots[j].getX()) + random(-PI/6, PI/6));
              shots[j].setAcceleration(random(-0.1, 0.1));
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
          enemys[i].setMove(random(0.5, 3), atan2(player.getY() - enemys[i].getY(), player.getX() - enemys[i].getX()) + random(-PI/6, PI/6));
          enemys[i].setAcceleration(random(-0.01, 0.01));
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

void gameRender(int gameMode) {
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

void keyPressed() {
  if (key == CODED)
    myKey.myKeyPressed(keyCode);
  else
    myKey.myKeyPressed(key);
}

void keyReleased() {
  if (key == CODED)
    myKey.myKeyReleased(keyCode);
  else
    myKey.myKeyReleased(key);
}
