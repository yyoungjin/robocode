package systemprogramming;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;

import systemprogramming.gfx.TankBoyColors;
import systemprogramming.gun.TankBoyFist;
import systemprogramming.move.TankBoyWhoosh;
import systemprogramming.radar.TankBoyEyes;
import systemprogramming.utils.ErrorLogger;


public class TankBoy extends AdvancedRobot {
  private static final boolean _TC = false;
  private static final boolean _MC = false;
  private static final boolean _LOG_ERRORS = true;

  protected static TankBoyEyes _radar;
  protected static TankBoyWhoosh _move;
  private static TankBoyFist _gun;
  protected static TankBoyColors _gfx;
  private static double _randColors = Math.random();

  private double _maxVelocity;

  static {
    ErrorLogger.enabled = _LOG_ERRORS;
  }

  public void run() {
    try {
      ErrorLogger.init(this);
      initComponents();
      initColors();

      setAdjustGunForRobotTurn(true);
      setAdjustRadarForGunTurn(true);

      while (true) {
        _gfx.updatePaintProcessing();
        if (!_TC) {
          _move.execute();
        }
        if (!_MC) {
          _gun.execute();
        }
        _radar.execute();
        execute();
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  private void initComponents() {
    if (_radar == null) {
      _radar = new TankBoyEyes(this, System.out);
    }
    if (_move == null) {
      _move = new TankBoyWhoosh(this, System.out);
    }
    if (_gun == null) {
      _gun = new TankBoyFist(this, _TC);
      _gun.addFireListener(_move);
    }
    if (_gfx == null) {
      _gfx = new TankBoyColors(this, _radar, _move, _gun, _TC, _MC);
      _gfx.registerPainter("r", _radar);
      _gfx.registerPainter("g", _gun);
      _gfx.registerPainter("m", _move);
    }

    _radar.initRound(this);
    _move.initRound(this);
    _gun.initRound(this);
  }

  // private void initColors() {
  //   if (_randColors < .05) {
  //     setBlueStreakColors();
  //   } else if (_randColors < .1) {
  //     setMillenniumGuardColors();
  //   } else {
  //     setTankBoyColors();
  //   }
  // }
  private void initColors() {
    setTankBoyColors();
  }

  private void setTankBoyColors() {
    // Color TankBoyYellow = new Color(255, 255, 170);
    Color TankBoyRed = new Color(255, 0, 0);
    Color TankBoyYellow = new Color(255, 255, 0);
    Color TankBoyWhite = new Color(255, 255, 255);
    setColors(TankBoyYellow, TankBoyWhite, TankBoyRed);
  }

  private void setMillenniumGuardColors() {
    if (getRoundNum() == 0) {
      System.out.println("Activating Millennium Guard colors.");
    }

    Color bloodRed = new Color(120, 0, 0);
    Color gold = new Color(240, 235, 170);
    setColors(bloodRed, gold, bloodRed);
  }

  private void setBlueStreakColors() {
    if (getRoundNum() == 0) {
      System.out.println("Activating Blue Streak colors.");
    }

    Color denimGrey = new Color(101, 108, 128);
    Color gloveRed = new Color(150, 20, 30);
    setColors(denimGrey, Color.white, gloveRed);
  }

  public void onScannedRobot(ScannedRobotEvent e) {
    try {
      _radar.onScannedRobot(e);
      if (!_TC) {
        _move.onScannedRobot(e);
      }
      if (!_MC) {
        _gun.onScannedRobot(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onRobotDeath(RobotDeathEvent e) {
    try {
      _radar.onRobotDeath(e);
      if (!_TC) {
        _move.onRobotDeath(e);
      }
      if (!_MC) {
        _gun.onRobotDeath(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onHitByBullet(HitByBulletEvent e) {
    try {
      if (!_TC) {
        _move.onHitByBullet(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onBulletHit(BulletHitEvent e) {
    try {
      if (!_TC) {
        _move.onBulletHit(e);
      }
      if (!_MC) {
        _gun.onBulletHit(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onBulletHitBullet(BulletHitBulletEvent e) {
    try {
      if (!_TC) {
        _move.onBulletHitBullet(e);
      }
      if (!_MC) {
        _gun.onBulletHitBullet(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onHitWall(HitWallEvent e) {
    System.out.println("WARNING: I hit a wall (" + getTime() + ").");
  }

  public void onWin(WinEvent e) {
    try {
      if (!_MC) {
        _gun.onWin(e);
      }
      if (!_TC) {
        _move.onWin(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onDeath(DeathEvent e) {
    try {
      if (!_MC) {
        _gun.onDeath(e);
      }
      if (!_TC) {
        _move.onDeath(e);
      }
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onPaint(Graphics2D g) {
    try {
      _gfx.onPaint(g);
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onKeyPressed(KeyEvent e) {
    try {
      _gfx.onKeyPressed(e);
    } catch (RuntimeException re) {
      logAndRethrowException(re);
    }
  }

  public void onSkippedTurn(SkippedTurnEvent e) {
    System.out.println("WARNING: Turn skipped at: " + e.getTime());
  }

  protected void logAndRethrowException(RuntimeException e) {
    String moreInfo = "getOthers(): " + getOthers() + "\n"
        + "getEnemiesAlive(): " + _gun.getEnemiesAlive() + "\n"
        + "getRoundNum(): " + getRoundNum() + "\n" + "getTime(): " + getTime();
    ErrorLogger.getInstance().logException(e, moreInfo);

    throw e;
  }

  public void setMaxVelocity(double maxVelocity) {
    super.setMaxVelocity(maxVelocity);
    _maxVelocity = maxVelocity;
  }

  public double getMaxVelocity() {
    return _maxVelocity;
  }
}
