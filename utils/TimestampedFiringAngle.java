package systemprogramming.utils;

import java.awt.geom.Point2D;

/**
 * Copyright (c) 2011-2012 - systemprogramming
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

public class TimestampedFiringAngle extends Timestamped {
  public final double guessFactor;
  public final Point2D.Double displacementVector;
    
  public TimestampedFiringAngle(int roundNum, long time, double guessFactor,
      Point2D.Double displacementVector) {
    super(roundNum, time);
    this.guessFactor = guessFactor;
    this.displacementVector = displacementVector;
  }
}
