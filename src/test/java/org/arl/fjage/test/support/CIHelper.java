package org.arl.fjage.test.support;

public class CIHelper {

  public static boolean isRunningInCI() {
    return "true".equals(System.getenv("CI"));
  }
}
