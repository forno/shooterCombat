/**
 * Shot class
 *
 * Copyright (c) 2016, Doi Yusuke
 * All rights reserved.
 *
 * This software is released under the BSD 2-Clause License.
 * https://opensource.org/licenses/BSD-2-Clause
 */
public class Shot extends BaseObject {
  private int size;
  private color shotColor;
  
  public Shot(int x, int y, int size, color shotColor) {
    super(x, y, size, size);
    this.size = size;
    this.shotColor = shotColor;
  }

  public Shot(){
    this(0,0,0,color(0,0,0));
  }

  public void setColor(color shotColor) {
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
