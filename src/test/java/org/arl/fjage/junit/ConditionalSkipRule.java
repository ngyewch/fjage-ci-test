package org.arl.fjage.junit;

import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ConditionalSkipRule
    implements MethodRule {

  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    final Skippable skippableAnnotation = method.getAnnotation(Skippable.class);
    if (skippableAnnotation == null) {
      return base;
    }
    return new Statement() {

      @Override
      public void evaluate()
          throws Throwable {
        final ConditionalSkipSupport conditionalSkipSupport = (ConditionalSkipSupport) target;
        Assume.assumeFalse(conditionalSkipSupport.shouldSkip());
        base.evaluate();
      }
    };
  }
}
