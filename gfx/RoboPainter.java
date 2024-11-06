package systemprogramming.gfx;

import java.awt.Graphics2D;

public interface RoboPainter {
  void paintOn();
  void paintOff();
  void robocodePaintOn();
  void robocodePaintOff();
  boolean paintStatus();
  String paintLabel();
  void onPaint(Graphics2D g);
}
