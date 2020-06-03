package org.arl.fjage.test;

import org.arl.fjage.DiscreteEventSimulator;
import org.arl.fjage.Platform;
import org.junit.rules.TestName;

public class DiscreteEventParamTest
    extends AbstractParamTest {

  @Override
  protected Platform newPlatform() {
    return new DiscreteEventSimulator();
  }

  @Override
  protected int getRequestTimeout() {
    return 1000;
  }

  @Override
  protected boolean shouldSkip(TestName testName) {
    return false;
  }
}
