package systemprogramming.gfx;

import java.awt.Color;
import java.util.ArrayList;

import systemprogramming.utils.DiaUtils;

public class ColoredValueSet {
  protected ArrayList<ColoredValue> _values;
  protected double _avg;
  protected double _min;
  protected double _stDev;

  public ColoredValueSet() {
    _values = new ArrayList<ColoredValue>();
    _avg = 0;
    _min = Double.POSITIVE_INFINITY;
    _stDev = 0;
  }

  public void addValue(double value, double firingAngle) {
    ColoredValue cv = new ColoredValue(value, firingAngle);
    _avg = ((_avg * _values.size()) + cv.value) / (_values.size() + 1);
    _min = Math.min(_min, cv.value);
    _values.add(cv);

    double[] values = new double[_values.size()];
    for (int x = 0; x < _values.size(); x++) {
      values[x] = _values.get(x).value;
    }

    _stDev = DiaUtils.standardDeviation(values);
  }

  public int colorValue(double value, double avg,
    double stDev, double maxStDev) {

    return (int) DiaUtils.limit(0, 255 * (value - (avg - maxStDev*stDev))
        / (2*maxStDev*stDev), 255);
  }

  public ArrayList<ColoredValue> getColoredValues() {
    return _values;
  }

  public class ColoredValue {
    public double value;
    public double firingAngle;

    public ColoredValue(double value, double firingAngle) {
      this.value = value;
      this.firingAngle = firingAngle;
    }

    public Color grayToWhiteColor() {
      int cval = Math.max(20, colorValue(value, _avg, _stDev, 3));
      return new Color(cval, cval, cval);
    }
  }
}
