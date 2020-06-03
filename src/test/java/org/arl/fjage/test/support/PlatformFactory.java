package org.arl.fjage.test.support;

import org.arl.fjage.DiscreteEventSimulator;
import org.arl.fjage.Platform;
import org.arl.fjage.RealTimePlatform;

public class PlatformFactory {

  public static final PlatformFactory INSTANCE = new PlatformFactory();

  public Platform newPlatform(PlatformType type) {
    if (type == null) {
      throw new NullPointerException();
    }
    switch (type) {
      case RealTimePlatform:
        return new RealTimePlatform();
      case DiscreteEventSimulator:
        return new DiscreteEventSimulator();
      default:
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
  }
}
