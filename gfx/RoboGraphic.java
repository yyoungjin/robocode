package systemprogramming.gfx;

import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.Color;

import systemprogramming.utils.DiaUtils;


public abstract class RoboGraphic{
  public abstract void render(Graphics2D g);

  static class Circle extends RoboGraphic {
    Point2D.Double center;
    double radius;
    Color color;

    public Circle(Point2D.Double center, double radius, Color color) {
      this.center = center;
      this.radius = radius;
      this.color = color;
    }

    public void render(Graphics2D g) {
      g.setColor(color);
      g.drawOval((int) Math.round(center.x - radius),
                 (int) Math.round(center.y - radius),
                 (int) Math.round(2 * radius),
                 (int) Math.round(2 * radius));
    }
  }

  static class Dot extends RoboGraphic {
    Point2D.Double point;
    double radius;
    Color color;

    public Dot(Point2D.Double point, Color color, int r) {
      this.point = point;
      this.radius = r;
      this.color = color;
    }

    public void render(Graphics2D g) {
      g.setColor(color);
      g.fillOval((int) Math.round(point.x - radius),
                 (int) Math.round(point.y - radius),
                 (int) Math.round(2 * radius),
                 (int) Math.round(2 * radius));
    }
  }

  static class Line extends RoboGraphic {
    Point2D.Double p1, p2;
    Color color;

    double radius;
    public Line(Point2D.Double p1, Point2D.Double p2, Color color) {
      this.p1 = p1;
      this.p2 = p2;
      this.color = color;
    }

    public void render(Graphics2D g) {
      g.setColor(color);
      g.drawLine((int) Math.round(p1.x),
                 (int) Math.round(p1.y),
                 (int) Math.round(p2.x),
                 (int) Math.round(p2.y));
    }
  }

  static class Text extends RoboGraphic {
    String text;
    double x, y;
    Color color;
    double radius;

    public Text(String text, double x, double y, Color color) {
      this.text = text;
      this.x = x;
      this.y = y;
      this.color = color;
    }

    public void render(Graphics2D g) {
      g.setColor(color);
      g.drawString(text, (float)x, (float)y);
    }
  }

  public static RoboGraphic drawLine(
      Point2D.Double p1, Point2D.Double p2, Color color) {
    return new RoboGraphic.Line(p1, p2, color);
  }

  public static RoboGraphic drawCircle(
      Point2D.Double center, double radius, Color color) {
    return new RoboGraphic.Circle(center, radius, color);
  }

  public static RoboGraphic drawPoint(Point2D.Double p1, Color color) {
    return new RoboGraphic.Dot(p1, color, 3);
  }

  public static RoboGraphic drawCircleFilled(
      Point2D.Double p1, Color color, int radius) {
    return new RoboGraphic.Dot(p1, color, radius);
  }

  public static RoboGraphic drawText(
      String text, double x, double y, Color color) {
    return new RoboGraphic.Text(text, x, y, color);
  }

  public static RoboGraphic[] drawRectangle(
      Point2D.Double p1, double x,  double y, Color color) {
    RoboGraphic[] lines = new RoboGraphic[4];
    lines[0] = RoboGraphic.drawLine(
        new Point2D.Double(p1.x - (x / 2), p1.y - (y / 2)),
        new Point2D.Double(p1.x + (x / 2), p1.y - (y / 2)), color);
    lines[1] = RoboGraphic.drawLine(
        new Point2D.Double(p1.x - (x / 2), p1.y - (y / 2)),
        new Point2D.Double(p1.x - (x / 2), p1.y + (y / 2)), color);
    lines[2] = RoboGraphic.drawLine(
        new Point2D.Double(p1.x - (x / 2), p1.y + (y / 2)),
        new Point2D.Double(p1.x + (x / 2), p1.y + (y / 2)), color);
    lines[3] = RoboGraphic.drawLine(
        new Point2D.Double(p1.x + (x / 2), p1.y + (y / 2)),
        new Point2D.Double(p1.x + (x / 2), p1.y - (y / 2)), color);
    return lines;
  }

  public static RoboGraphic[] drawArrowHead(
      Point2D.Double p1, double length, double angle, Color color) {
    RoboGraphic[] lines = new RoboGraphic[2];
    lines[0] = RoboGraphic.drawLine(
        p1, DiaUtils.project(p1, angle - 2.7, length), color);
    lines[1] = RoboGraphic.drawLine(
        p1, DiaUtils.project(p1, angle + 2.7, length), color);
    return lines;
  }

  public static Color riskColor(double risk, double avg,
    double stDev, boolean safestYellow, double maxStDev) {
    if (risk < .0000001 && safestYellow) {
      return Color.yellow;
    }

    return new Color(
        (int) DiaUtils.limit(0, 255 * (risk - (avg - maxStDev*stDev))
            / (2*maxStDev*stDev), 255),
        0,
        (int) DiaUtils.limit(0, 255 * ((avg + maxStDev*stDev) - risk)
            / (2*maxStDev*stDev), 255));
  }
}
