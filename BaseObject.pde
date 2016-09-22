/**
 * BaseObject
 *
 * Copyright (c) 2014, Doi Yusuke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of the Yusuke Doi nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Yusuke Doi BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
