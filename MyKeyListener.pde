/**
 * MyKeyListener class
 *
 * Copyright (c) 2016, Doi Yusuke
 * All rights reserved.
 *
 * This software is released under the BSD 2-Clause License.
 * https://opensource.org/licenses/BSD-2-Clause
 */
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
