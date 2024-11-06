package systemprogramming.gun;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

import systemprogramming.TankBoy;
import systemprogramming.gfx.RoboGraphic;
import systemprogramming.test.RobocodeEventTestHelper;
import systemprogramming.utils.BattleField;
import systemprogramming.utils.MovementPredictor;
import systemprogramming.utils.RobotState;
import systemprogramming.utils.Wave;
import systemprogramming.utils.TimestampedFiringAngle;

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

public class TankBoyFistTest {
  @Mock
  private TankBoy TankBoy;
  @Mock
  private GunDataManager gunDataManager;
  @Mock
  private VirtualGunsManager<TimestampedFiringAngle> virtualGunsManager;

  private GunEnemy _shadow;
  private GunEnemy _portia;
  private OutputStream _out;
  private BattleField _battleField;
  private List<RoboGraphic> _renderables;
  private RobocodeEventTestHelper _eventHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(TankBoy.getX()).thenReturn(100.0);
    Mockito.when(TankBoy.getY()).thenReturn(100.0);
    Mockito.when(TankBoy.getEnergy()).thenReturn(100.0);

    _battleField = new BattleField(800, 600);
    _renderables = new ArrayList<RoboGraphic>();
    _eventHelper = new RobocodeEventTestHelper();
    MovementPredictor predictor = new MovementPredictor(_battleField);
    _shadow = new GunEnemy("Shadow", 400, 85, new Point2D.Double(400, 400), 1,
        50, 8.0, 0, 1, _battleField, predictor, _renderables);
    _portia = new GunEnemy("Portia", 700, 50, new Point2D.Double(400, 700), 1,
        50, -3.0, 0, 2, _battleField, predictor, _renderables);
    _out = new ByteArrayOutputStream();
  }

  @Test
  public void testConstruction() {
    TankBoyFist gun = newGun();
    assertFalse(gun.getVirtualGunsManager().getGuns().isEmpty());
  }

  @Test
  public void testConstructionNullTankBoy() {
    try {
      new TankBoyFist(null, true);
      fail();
    } catch (NullPointerException npe) {
      // expected
    }
  }

  // TODO: Make some of these tests a little more in depth, even at the risk
  //       of making them mock soup. There's not really any other reasonable
  //       way to test the activity.
  @Test
  public void testInitRound() {
    TankBoyFist gun = newGun(true, true);
    TankBoy newRobot = Mockito.mock(TankBoy.class);
    gun.getRenderables().add(
        RoboGraphic.drawPoint(new Point2D.Double(1, 1), Color.red));
    gun.initRound(newRobot);
    verify(gunDataManager).initRound();
    verify(virtualGunsManager).initRound();
    assertEquals(newRobot, gun.getRobot());
    assertTrue(gun.getRenderables().isEmpty());
  }

  @Test
  public void testExecute1v1() {
    TankBoyFist gun = newGun(true, false);
    mock1v1(gun, _shadow);
    when(gunDataManager.duelEnemy()).thenReturn(_shadow);
    gun.execute();
    verify(gunDataManager).execute(anyInt(), anyLong(), anyDouble(),
        anyDouble(), (Point2D.Double) any(), anyBoolean(), anyBoolean());
    verify(TankBoy).setTurnGunRightRadians(anyDouble());
  }

  @Test
  public void testExecuteMelee() {
    TankBoyFist gun = newGun(true, false);
    mockMelee(gun);
    gun.execute();
    verify(gunDataManager).execute(anyInt(), anyLong(), anyDouble(),
        anyDouble(), (Point2D.Double) any(), anyBoolean(), anyBoolean());
    verify(TankBoy).setTurnGunRightRadians(anyDouble());
  }

  @Test
  public void testCalculateBulletPower1v1() {
    TankBoyFist gun = newGun(true, false);
    mock1v1(gun, _shadow);
    double bulletPower = gun.calculateBulletPower();
    assertTrue(bulletPower >= 0.1);
    assertTrue(bulletPower <= 3.0);
  }

  @Test
  public void testCalculateBulletPower1v1LowEnergy() {
    TankBoyFist gun = newGun(true, false);
    _shadow.energy = 0.44;
    mock1v1(gun, _shadow);
    double bulletPower = gun.calculateBulletPower();
    assertTrue(bulletPower <= 0.11);
  }

  @Test
  public void testCalculateBulletPowerMelee() {
    TankBoyFist gun = newGun(true, false);
    mockMelee(gun);
    double bulletPower = gun.calculateBulletPower();
    assertTrue(bulletPower >= 0.1);
    assertTrue(bulletPower <= 3.0);
  }

  @Test
  public void testFireIfGunTurnedGunHot() {
    TankBoyFist gun = newGun();
    Mockito.when(TankBoy.getGunHeat()).thenReturn(1.0);
    Mockito.when(TankBoy.getTurnRemaining()).thenReturn(0.0);
    Mockito.when(TankBoy.getTurnRemainingRadians()).thenReturn(0.0);
    gun.fireIfGunTurned(3.0);
    verify(TankBoy, never()).setFire(anyDouble());
    verify(TankBoy, never()).setFireBullet(anyDouble());
  }

  @Test
  public void testFireIfGunTurnedGunNotTurned() {
    TankBoyFist gun = newGun();
    Mockito.when(TankBoy.getGunHeat()).thenReturn(0.0);
    Mockito.when(TankBoy.getGunTurnRemaining()).thenReturn(1.0);
    Mockito.when(TankBoy.getGunTurnRemainingRadians()).thenReturn(1.0);
    gun.fireIfGunTurned(3.0);
    verify(TankBoy, never()).setFire(anyDouble());
    verify(TankBoy, never()).setFireBullet(anyDouble());
  }

  @Test
  public void testFireIfGunTurned() {
    TankBoyFist gun = newGun();
    Mockito.when(TankBoy.getGunHeat()).thenReturn(0.0);
    Mockito.when(TankBoy.getTurnRemaining()).thenReturn(0.0);
    Mockito.when(TankBoy.getTurnRemainingRadians()).thenReturn(0.0);
    gun.fireIfGunTurned(3.0);
    verify(TankBoy).setFireBullet(3.0);
  }

  @Test
  public void testOnScannedRobotNewEnemy() {
    TankBoyFist gun = newGun(true, false);
    mock1v1NewEnemy(gun, _shadow);
    ScannedRobotEvent sre = _eventHelper.newScannedRobotEvent(_shadow);

    gun.onScannedRobot(sre);
    verify(gunDataManager).newEnemy(
        eq(sre), (Point2D.Double) any(), anyDouble(), anyInt(), eq(true));
  }

  @Test
  public void testOnScannedRobotUpdateEnemy() {
    TankBoyFist gun = newGun(true, false);
    mock1v1(gun, _shadow);
    ScannedRobotEvent sre = _eventHelper.newScannedRobotEvent(_shadow);

    gun.onScannedRobot(sre);
    verify(gunDataManager).updateEnemy(
        eq(sre), (Point2D.Double) any(), anyDouble(), anyInt(), eq(true));
  }

  @Test
  public void testOnRobotDeath() {
    TankBoyFist gun = newGun(true, false);
    RobotDeathEvent rde = _eventHelper.newRobotDeathEvent(_shadow.botName);
    gun.onRobotDeath(rde);
    verify(gunDataManager).onRobotDeath(rde);
  }

  @Test
  public void testOnWin1v1() {
    TankBoyFist gun = newGun();
    mock1v1(gun, _shadow);
    gun.onWin(_eventHelper.newWinEvent());
  }

  @Test
  public void testOnWinMelee() {
    TankBoyFist gun = newGun();
    mockMelee(gun);
    gun.onWin(_eventHelper.newWinEvent());
  }

  @Test
  public void testOnDeath1v1() {
    TankBoyFist gun = newGun();
    mock1v1(gun, _shadow);
    gun.onDeath(_eventHelper.newDeathEvent());
  }

  @Test
  public void testOnDeathMelee() {
    TankBoyFist gun = newGun();
    mockMelee(gun);
    gun.onDeath(_eventHelper.newDeathEvent());
  }

  @Test
  public void testRoundOver() {
    TankBoyFist gun = newGun();
    gun.roundOver();
  }

  @Test
  public void testRoundOverTc() {
    TankBoyFist gun = newGun(false, false, true);
    gun.roundOver();
    assertTrue(_out.toString().contains("TC score"));
  }

  @Test
  public void testOnBulletHit() {
    TankBoyFist gun = newGun(true, false);
    mock1v1NewEnemy(gun, _shadow);
    BulletHitEvent bhe =
        _eventHelper.newBulletHitEvent("TankBoy", _shadow.botName, 3.0);
    gun.onBulletHit(bhe);
    verify(gunDataManager).onBulletHit(eq(bhe), anyLong());
  }

  @Test
  public void testOnBulletHitBullet() {
    TankBoyFist gun = newGun(true, false);
    mock1v1(gun, _shadow);
    BulletHitBulletEvent bhbe = _eventHelper.newBulletHitBulletEvent(
        "TankBoy", 3.0, _shadow.botName, 3.0);
    gun.onBulletHitBullet(bhbe);
    verify(gunDataManager).onBulletHitBullet(eq(bhbe), anyLong());
  }

  @Test
  public void testOnPaintOn() {
    TankBoyFist gun = newGun();
    gun.robocodePaintOn();
    gun.onPaint(Mockito.mock(Graphics2D.class));
  }

  @Test
  public void testOnPaintOff() {
    TankBoyFist gun = newGun();
    gun.robocodePaintOff();
    gun.onPaint(Mockito.mock(Graphics2D.class));
  }

  private void mock1v1(TankBoyFist gun, GunEnemy gunData) {
    Mockito.when(gunDataManager.hasEnemy(gunData.botName)).thenReturn(true);
    gun.initGunViews(gunData);

    mock1v1Common(gunData);
  }

  private void mock1v1NewEnemy(TankBoyFist gun, GunEnemy gunData) {
    Mockito.when(gunDataManager.hasEnemy(anyString()))
        .thenReturn(false);
    Mockito.when(gunDataManager.newEnemy((ScannedRobotEvent) any(),
        (Point2D.Double) any(), anyDouble(), anyInt(), eq(true)))
            .thenReturn(gunData);

    mock1v1Common(gunData);
  }

  private void mock1v1Common(GunEnemy gunData) {
    Mockito.when(gunDataManager.getEnemyData(anyString()))
        .thenReturn(gunData);
    Mockito.when(gunDataManager.getAllEnemyData())
        .thenReturn(Arrays.asList(gunData));
    Mockito.when(gunDataManager.getClosestLivingBot(
        (Point2D.Double) any())).thenReturn(gunData);
    Mockito.when(TankBoy.getOthers()).thenReturn(1);
    gunData.lastWaveFired = Mockito.mock(Wave.class);
    gunData.setRobotState(RobotState.newBuilder()
        .setLocation(new Point2D.Double(100, 100))
        .setTime(1)
        .build());
  }

  private void mockMelee(TankBoyFist gun) {
    Mockito.when(gunDataManager.getEnemyData(_shadow.botName))
        .thenReturn(_shadow);
    Mockito.when(gunDataManager.getEnemyData(_portia.botName))
        .thenReturn(_portia);
    Mockito.when(gunDataManager.getAllEnemyData())
        .thenReturn(Arrays.asList(_shadow, _portia));
    Mockito.when(gunDataManager.hasEnemy(_shadow.botName)).thenReturn(true);
    Mockito.when(gunDataManager.hasEnemy(_portia.botName)).thenReturn(true);
    Mockito.when(gunDataManager.getClosestLivingBot(
        (Point2D.Double) any()))
            .thenReturn(_shadow);
    Mockito.when(TankBoy.getOthers()).thenReturn(2);

    gun.initGunViews(_shadow);
    _shadow.lastWaveFired = Mockito.mock(Wave.class);
    gun.initGunViews(_portia);
    _portia.lastWaveFired = Mockito.mock(Wave.class);
  }

  private TankBoyFist newGun() {
    return newGun(false, false, false);
  }

  private TankBoyFist newGun(
      boolean mockGunDataManager, boolean mockVirtualGunsManager) {
    return newGun(mockGunDataManager, mockVirtualGunsManager, false);
  }

  private TankBoyFist newGun(boolean mockGunDataManager,
      boolean mockVirtualGunsManager, boolean isTc) {
    return new TankBoyFist(TankBoy, isTc,
        mockGunDataManager ? gunDataManager : new GunDataManager(1,
            _battleField, new MovementPredictor(_battleField), _renderables,
            _out),
        mockVirtualGunsManager ? virtualGunsManager
            : new VirtualGunsManager<TimestampedFiringAngle>(_out),
        _battleField, _renderables, _out);
  }
}
