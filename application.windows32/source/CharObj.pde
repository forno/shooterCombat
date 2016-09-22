class CharObj extends BaseObject {
  protected color charColor;
  
  public CharObj(int x, int y, int width, int height, color charColor) {
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
