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
    if (enemy.getX() - enemy.getWidth()/2.0 < x + width/2.0 && x - width/2.0 < enemy.getX() + enemy.getWidth()/2.0 && enemy.getY() < y + height * 2/3 && y + height/3 < enemy.getY() - enemy.getHeight())
      return true;
    return false;
  }
  
  public void draw() {
    fill(charColor);
    triangle((float)x, (float)y, (float)x-width/2, (float)y+height, (float)x+width/2, (float)y+height);
  }
}

