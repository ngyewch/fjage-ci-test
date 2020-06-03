package org.arl.fjage.test;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class AbstractConditionalSkipTest {

  @Rule
  public final ConditionalSkipRule conditionalSkipRule = new ConditionalSkipRule();

  protected abstract boolean shouldSkip();

  protected static boolean isRunningInCI() {
    return "true".equals(System.getenv("CI"));
  }

  public static class ConditionalSkipRule
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
          final AbstractConditionalSkipTest conditionalSkipTest = (AbstractConditionalSkipTest) target;
          Assume.assumeFalse(conditionalSkipTest.shouldSkip());
          base.evaluate();
        }
      };
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Skippable {
  }
}
