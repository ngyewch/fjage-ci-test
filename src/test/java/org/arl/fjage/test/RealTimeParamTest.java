package org.arl.fjage.test;

import org.arl.fjage.Platform;
import org.arl.fjage.RealTimePlatform;
import org.junit.rules.TestName;

public class RealTimeParamTest
    extends AbstractParamTest {

  @Override
  protected Platform newPlatform() {
    return new RealTimePlatform();
  }

  @Override
  protected int getRequestTimeout() {
    return 1000;
  }

  @Override
  protected boolean shouldSkip(TestName testName) {
    final boolean ci = "true".equals(System.getenv("CI"));
    return ci;
  }
}
