/**
 * Card class
 *
 * Copyright (c) 2016, Doi Yusuke
 * All rights reserved.
 *
 * This software is released under the BSD 2-Clause License.
 * https://opensource.org/licenses/BSD-2-Clause
 */
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

