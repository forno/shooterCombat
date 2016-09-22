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
