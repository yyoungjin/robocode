package systemprogramming.move;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

import systemprogramming.TankBoy;
import systemprogramming.gfx.RoboGraphic;
import systemprogramming.gun.FireListener.FiredBullet;
import systemprogramming.test.RobocodeEventTestHelper;
import systemprogramming.utils.BattleField;
import systemprogramming.utils.RobotState;
import systemprogramming.utils.RobotStateLog;

/**
 * Copyright (c) 2012 - systemprogramming
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software.
 *
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 *
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */

public class TankBoyWhooshTest {
  private static final int DEFAULT_ROUND = 3;
  private static final long DEFAULT_TIME = 301L;
  private static final double DEFAULT_HEADING = 0.392883;
  private static final double DEFAULT_VELOCITY = 7.29293848;
  private static final double DEFAULT_X = 83.01929;
  private static final double DEFAULT_Y = 80.939;
  private static final Point2D.Double DEFAULT_MYLOCATION =
      new Point2D.Double(DEFAULT_X, DEFAULT_Y);

  @Mock
  private TankBoy TankBoy;
  @Mock
  private MoveDataManager moveDataManager;
  @Mock
  private MeleeMover meleeMover;
  @Mock
  private SurfMover surfMover;

  private List<RoboGraphic> _renderables;
  private RobocodeEventTestHelper _eventHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    _renderables = new ArrayList<RoboGraphic>();
    _eventHelper = new RobocodeEventTestHelper();
  }

  @Test
  public void testTankBoyWhoosh() {
    newTankBoyWhoosh();
  }

  @Test
  public void testInitRound() {
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    TankBoy newRobot = mock(TankBoy.class);
    _renderables.add(
        RoboGraphic.drawPoint(new Point2D.Double(1, 1), Color.red));
    whoosh.initRound(newRobot);
    assertEquals(newRobot, whoosh.getRobot());
    assertTrue(_renderables.isEmpty());
    verify(moveDataManager).initRound();
    verify(meleeMover).initRound(eq(newRobot), (Point2D.Double) any());
  }

  @Test
  public void testExecute1v1() {
    when(TankBoy.getOthers()).thenReturn(1);
    mockRobotDefaults();

    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.execute();
    verify(moveDataManager).execute(DEFAULT_ROUND, DEFAULT_TIME,
        DEFAULT_MYLOCATION, DEFAULT_HEADING, DEFAULT_VELOCITY);
    verify(surfMover).move((RobotState) any(), (MoveEnemy) any(), anyInt(),
        anyBoolean());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testExecuteMelee() {
    when(TankBoy.getOthers()).thenReturn(3);
    mockRobotDefaults();

    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.execute();
    verify(moveDataManager).execute(DEFAULT_ROUND, DEFAULT_TIME,
        new Point2D.Double(DEFAULT_X, DEFAULT_Y), DEFAULT_HEADING,
        DEFAULT_VELOCITY);
    verify(meleeMover).move(eq(DEFAULT_MYLOCATION),
        (Collection<MoveEnemy>) any(), (MoveEnemy) any(), anyBoolean());
  }

  @Test
  public void testOnScannedRobotNewEnemy1v1() {
    testOnScannedRobotNewEnemy(1, true, true);
  }

  @Test
  public void testOnScannedRobotNewEnemyMelee() {
    testOnScannedRobotNewEnemy(3, false, false);
  }

  private void testOnScannedRobotNewEnemy(
      int numRobots, boolean mockDuelEnemy, boolean updateEnemyWaves) {
    mockRobotDefaults();
    when(moveDataManager.hasEnemy("Shadow")).thenReturn(false);
    MoveEnemy shadowData = mock(MoveEnemy.class);
    if (mockDuelEnemy) {
      when(moveDataManager.duelEnemy()).thenReturn(shadowData);
    }
    when(TankBoy.getOthers()).thenReturn(numRobots);

    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    ScannedRobotEvent shadowSre = _eventHelper.newScannedRobotEvent(
        "Shadow", 99, 1, 350, 0, 8.0);
    whoosh.onScannedRobot(shadowSre);

    verify(moveDataManager).newEnemy(eq(shadowSre), (Point2D.Double) any(),
        anyDouble(), eq(DEFAULT_ROUND), anyBoolean());
    if (updateEnemyWaves) {
      verify(moveDataManager).updateEnemyWaves(eq(DEFAULT_MYLOCATION), eq(99D),
          eq("Shadow"), eq(DEFAULT_ROUND), eq(DEFAULT_TIME), anyDouble(),
          eq(DEFAULT_HEADING), eq(DEFAULT_VELOCITY), anyInt());
    } else {
      verify(moveDataManager, never()).updateEnemyWaves((Point2D.Double) any(),
          anyDouble(), anyString(), anyInt(), anyLong(), anyDouble(),
          anyDouble(), anyDouble(), anyInt());
    }
  }

  @Test
  public void testOnScannedRobotUpdateEnemy1v1() {
    testOnScannedRobotUpdateEnemy(1, true, true);
  }

  @Test
  public void testOnScannedRobotUpdateEnemyMelee() {
    testOnScannedRobotUpdateEnemy(3, false, false);
  }

  private void testOnScannedRobotUpdateEnemy(
      int numRobots, boolean mockDuelEnemy, boolean updateEnemyWaves) {
    mockRobotDefaults();
    when(moveDataManager.hasEnemy("Shadow")).thenReturn(true);
    MoveEnemy shadowData = mock(MoveEnemy.class);
    shadowData.energy = 85;
    when(moveDataManager.getEnemyData("Shadow")).thenReturn(shadowData);
    when(moveDataManager.duelEnemy()).thenReturn(shadowData);
    if (mockDuelEnemy) {
      when(moveDataManager.duelEnemy()).thenReturn(shadowData);
    }
    when(TankBoy.getOthers()).thenReturn(numRobots);

    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    ScannedRobotEvent shadowSre = _eventHelper.newScannedRobotEvent(
        "Shadow", 51, 1, 350, 0, 8.0);
    whoosh.onScannedRobot(shadowSre);

    verify(moveDataManager).updateEnemy(eq(shadowSre), (Point2D.Double) any(),
        anyDouble(), eq(DEFAULT_ROUND), anyBoolean());
    if (updateEnemyWaves) {
      verify(moveDataManager).updateEnemyWaves(eq(DEFAULT_MYLOCATION), eq(85D),
        eq("Shadow"), eq(DEFAULT_ROUND), eq(DEFAULT_TIME), anyDouble(),
        eq(DEFAULT_HEADING), eq(DEFAULT_VELOCITY), anyInt());
    } else {
      verify(moveDataManager, never()).updateEnemyWaves((Point2D.Double) any(),
          anyDouble(), anyString(), anyInt(), anyLong(), anyDouble(),
          anyDouble(), anyDouble(), anyInt());
    }
  }

  @Test
  public void testOnRobotDeath() {
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    RobotDeathEvent rde = _eventHelper.newRobotDeathEvent("Shadow");
    whoosh.onRobotDeath(rde);
    verify(moveDataManager).onRobotDeath(rde);
  }

  @Test
  public void testOnHitByBullet() {
    mockRobotDefaults();
    HitByBulletEvent hbbe = _eventHelper.newHitByBulletEvent();
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.onHitByBullet(hbbe);
    verify(moveDataManager).onHitByBullet(
        hbbe, DEFAULT_ROUND, DEFAULT_TIME, false);
  }

  @Test
  public void testOnBulletHitBullet() {
    mockRobotDefaults();
    BulletHitBulletEvent bhbe = _eventHelper.newBulletHitBulletEvent();
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.onBulletHitBullet(bhbe);
    verify(moveDataManager).onBulletHitBullet(
        bhbe, DEFAULT_ROUND, DEFAULT_TIME, false);
  }

  @Test
  public void testOnBulletHit() {
    mockRobotDefaults();
    BulletHitEvent bhe = _eventHelper.newBulletHitEvent();
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.onBulletHit(bhe);
    verify(moveDataManager).onBulletHit(bhe);
  }

  @Test
  public void testOnWin() {
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.onWin(_eventHelper.newWinEvent());
  }

  @Test
  public void testOnDeath() {
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.onDeath(_eventHelper.newDeathEvent());
  }

  @Test
  public void testRoundOver1v1() {
    when(TankBoy.getOthers()).thenReturn(1);
    MoveEnemy duelEnemy = mock(MoveEnemy.class);
    when(moveDataManager.duelEnemy()).thenReturn(duelEnemy);
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.roundOver();
    verify(surfMover).roundOver(duelEnemy);
  }

  public void testRoundOverMelee() {
    when(TankBoy.getOthers()).thenReturn(2);
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    whoosh.roundOver();
    verify(surfMover, never()).roundOver((MoveEnemy) any());
  }

  @Test
  public void testBulletFired() {
    TankBoyWhoosh whoosh = newTankBoyWhoosh();
    FiredBullet diaBullet = mock(FiredBullet.class);
    whoosh.bulletFired(diaBullet);
    verify(moveDataManager).addFiredBullet(diaBullet);
  }

  private void mockRobotDefaults() {
    when(TankBoy.getRoundNum()).thenReturn(DEFAULT_ROUND);
    when(TankBoy.getTime()).thenReturn(DEFAULT_TIME);
    when(TankBoy.getX()).thenReturn(DEFAULT_X);
    when(TankBoy.getY()).thenReturn(DEFAULT_Y);
    when(TankBoy.getHeadingRadians()).thenReturn(DEFAULT_HEADING);
    when(TankBoy.getVelocity()).thenReturn(DEFAULT_VELOCITY);
    when(moveDataManager.myStateLog()).thenReturn(mock(RobotStateLog.class));
  }

  private TankBoyWhoosh newTankBoyWhoosh() {
    return new TankBoyWhoosh(TankBoy, moveDataManager, meleeMover, surfMover,
        mock(BattleField.class), _renderables, new ByteArrayOutputStream());
  }
}
