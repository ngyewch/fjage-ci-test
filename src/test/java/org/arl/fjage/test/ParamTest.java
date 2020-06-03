package org.arl.fjage.test;

import org.arl.fjage.*;
import org.arl.fjage.param.Parameter;
import org.arl.fjage.param.ParameterMessageBehavior;
import org.arl.fjage.param.ParameterReq;
import org.arl.fjage.param.ParameterRsp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ParamTest
    extends AbstractConditionalSkipTest {

  private final Logger log = Logger.getLogger(getClass().getName());

  @org.junit.runners.Parameterized.Parameter(0)
  public String platformId;

  @org.junit.runners.Parameterized.Parameter(1)
  public Platform platform;

  @org.junit.runners.Parameterized.Parameter(2)
  public int requestTimeout;

  @Parameterized.Parameters(name = "platform={0}, requestTimeout={2}ms")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"RealTimePlatform", new RealTimePlatform(), 1000},
        {"DiscreteEventSimulator", new DiscreteEventSimulator(), 1000},
    });
  }

  @Override
  protected boolean shouldSkip() {
    return (isRunningInCI() && platformId.equals("RealTimePlatform"));
  }

  @Before
  public void beforeParamTest() {
    LogFormatter.install(null);
  }

  @Test
  @Skippable
  public void testParam1() {
    log.info("testParam1");
    Container container = new Container(platform);
    ParamServerAgent server = new ParamServerAgent(true);
    ParamClientAgent client1 = new ParamClientAgent(true, requestTimeout);
    ParamClientAgent client2 = new ParamClientAgent(true, requestTimeout);
    ParamClientAgent client3 = new ParamClientAgent(true, requestTimeout);
    container.add("S", server);
    container.add("C1", client1);
    container.add("C2", client2);
    container.add("C3", client3);
    platform.start();
    platform.delay(10000);
    shutdown(platform);
    log.info("Successful: " + (client1.count + client2.count + client3.count));
    log.info("Warnings: " + (client1.warnings + client2.warnings + client3.errors));
    log.info("Errors: " + (server.errors + client1.errors + client2.errors + client3.errors));
    assertTrue(server.errors == 0);
    assertTrue(client1.errors == 0);
    assertTrue(client2.errors == 0);
    assertTrue(client3.errors == 0);
    assertTrue(client1.warnings < 3);
    assertTrue(client2.warnings < 3);
    assertTrue(client3.warnings < 3);
    assertTrue(client1.count + client2.count + client3.count > 100);
  }

  @Test
  @Skippable
  public void testParam2() {
    log.info("testParam2");
    Container container = new Container(platform);
    ParamServerAgent server = new ParamServerAgent(false);
    ParamClientAgent client1 = new ParamClientAgent(false, requestTimeout);
    ParamClientAgent client2 = new ParamClientAgent(false, requestTimeout);
    ParamClientAgent client3 = new ParamClientAgent(false, requestTimeout);
    container.add("S", server);
    container.add("C1", client1);
    container.add("C2", client2);
    container.add("C3", client3);
    platform.start();
    platform.delay(10000);
    shutdown(platform);
    log.info("Successful: " + (client1.count + client2.count + client3.count));
    log.info("Warnings: " + (client1.warnings + client2.warnings + client3.errors));
    log.info("Errors: " + (server.errors + client1.errors + client2.errors + client3.errors));
    assertTrue(server.errors == 0);
    assertTrue(client1.errors == 0);
    assertTrue(client2.errors == 0);
    assertTrue(client3.errors == 0);
    assertTrue(client1.warnings < 3);
    assertTrue(client2.warnings < 3);
    assertTrue(client3.warnings < 3);
    assertTrue(client1.count + client2.count + client3.count > 100);
  }

  @Test
  @Skippable
  public void testAIDParam() {
    log.info("testAIDParam");
    Container container = new Container(platform);
    ParamServerAgent server = new ParamServerAgent(false);
    AIDParamClientAgent client1 = new AIDParamClientAgent();
    AIDParamClientAgent client2 = new AIDParamClientAgent();
    AIDParamClientAgent client3 = new AIDParamClientAgent();
    container.add("S", server);
    container.add("C1", client1);
    container.add("C2", client2);
    container.add("C3", client3);
    platform.start();
    platform.delay(10000);
    shutdown(platform);
    log.info("Successful: " + (client1.count + client2.count + client3.count));
    log.info("Warnings: " + (client1.warnings + client2.warnings + client3.errors));
    log.info("Errors: " + (server.errors + client1.errors + client2.errors + client3.errors));
    assertTrue(server.errors == 0);
    assertTrue(client1.errors == 0);
    assertTrue(client2.errors == 0);
    assertTrue(client3.errors == 0);
    assertTrue(client1.warnings < 3);
    assertTrue(client2.warnings < 3);
    assertTrue(client3.warnings < 3);
    assertTrue(client1.count + client2.count + client3.count > 100);
  }

  private void shutdown(Platform platform) {
    platform.shutdown();
    while (platform.isRunning()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  private static class RequestMessage extends Message {
    private static final long serialVersionUID = 1L;
    public int x;

    public RequestMessage(AgentID recipient) {
      super(recipient, Performative.REQUEST);
    }
  }

  private static class ResponseMessage extends Message {
    private static final long serialVersionUID = 1L;
    public int x, y;

    public ResponseMessage(Message request) {
      super(request, Performative.INFORM);
    }
  }

  public enum Params implements Parameter {
    x, y, s
  }

  public static class ParamServerAgent extends Agent {
    public int errors = 0;
    public int x = 1;

    public float getY() {
      return 2;
    }

    public String getS() {
      return "xxx";
    }

    public int setX(int x) {
      return x + 1;
    }

    public float getY(int ndx) {
      return ndx + 42;
    }

    public int setX(int ndx, int x) {
      return x + 42;
    }

    public ParamServerAgent(boolean yor) {
      super();
      setYieldDuringReceive(yor);
    }

    @Override
    public void init() {
      register("server");
      add(new ParameterMessageBehavior(Params.class));
      add(new MessageBehavior() {
        @Override
        public void onReceive(Message msg) {
          if (msg instanceof ParameterReq) {
            log.warning("Got unexpected req: " + msg);
            errors++;
          } else if (msg instanceof RequestMessage) send(new ResponseMessage(msg));
        }
      });
      if (getYieldDuringReceive()) {
        add(new TickerBehavior(1000) {
          @Override
          public void onTick() {
            Message m1 = new Message();
            Message m2 = receive(m1, 500);
            if (m2 != null) {
              log.warning("Unexpected message: " + m2.toString());
              errors++;
            }
          }
        });
      }
    }
  }

  private static class ParamClientAgent extends Agent {
    private final int requestTimeout;

    public int errors = 0;
    public int warnings = 0;
    public int count = 0;

    public ParamClientAgent(boolean yor, int requestTimeout) {
      super();
      this.requestTimeout = requestTimeout;
      setYieldDuringReceive(yor);
    }

    @Override
    public void init() {
      delay(200);
      add(new PoissonBehavior(100) {
        @Override
        public void onTick() {
          AgentID server = agent.agentForService("server");
          if (server == null) {
            log.warning("Unable to find server");
            errors++;
            return;
          }
          count++;
          Message req = new ParameterReq().get(Params.x);
          req.setRecipient(server);
          Message rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to get x");
            warnings++;
          } else {
            Integer x = (Integer) ((ParameterRsp) rsp).get(Params.x);
            if (x == null || x != 1) {
              log.warning("Bad value of x: " + x);
              errors++;
            }
          }
          req = new ParameterReq().set(Params.x, 3);
          req.setRecipient(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to set x");
            warnings++;
          } else {
            Integer x = (Integer) ((ParameterRsp) rsp).get(Params.x);
            if (x == null || x != 4) {
              log.warning("Bad value of set(x): " + x);
              errors++;
            }
          }
          req = new ParameterReq().get(Params.y).get(Params.s);
          req.setRecipient(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to get y and s");
            warnings++;
          } else {
            Float y = (Float) ((ParameterRsp) rsp).get(Params.y);
            if (y == null || y != 2) {
              log.warning("Bad value of y: " + y);
              errors++;
            }
            String s = (String) ((ParameterRsp) rsp).get(Params.s);
            if (!"xxx".equals(s)) {
              log.warning("Bad value of s: " + s);
              errors++;
            }
          }
          req = new ParameterReq().get(Params.y);
          ((ParameterReq) req).setIndex(7);
          req.setRecipient(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to get y[7]");
            warnings++;
          } else {
            Float y = (Float) ((ParameterRsp) rsp).get(Params.y);
            if (y == null || y != 49) {
              log.warning("Bad value of y[7]: " + y);
              errors++;
            }
          }
          req = new ParameterReq().set(Params.x, 2);
          ((ParameterReq) req).setIndex(7);
          req.setRecipient(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to set x[7]");
            warnings++;
          } else {
            Integer x = (Integer) ((ParameterRsp) rsp).get(Params.x);
            if (x == null || x != 44) {
              log.warning("Bad value of x[7]: " + x);
              errors++;
            }
          }
          req = new ParameterReq();
          req.setRecipient(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("Unable to get x, y and s");
            warnings++;
          } else {
            Integer x = (Integer) ((ParameterRsp) rsp).get(Params.x);
            if (x == null || x != 1) {
              log.warning("Bad value of x: " + x);
              errors++;
            }
            Float y = (Float) ((ParameterRsp) rsp).get(Params.y);
            if (y == null || y != 2) {
              log.warning("Bad value of y: " + y);
              errors++;
            }
            String s = (String) ((ParameterRsp) rsp).get(Params.s);
            if (!"xxx".equals(s)) {
              log.warning("Bad value of s: " + s);
              errors++;
            }
          }
          req = new RequestMessage(server);
          rsp = request(req, requestTimeout);
          if (rsp == null) {
            log.warning("No response from server");
            warnings++;
          } else if (!(rsp instanceof ResponseMessage)) {
            log.warning("Bad response: " + rsp);
            errors++;
          }
        }
      });
    }
  }

  private static class AIDParamClientAgent extends Agent {
    public int errors = 0;
    public int warnings = 0;
    public int count = 0;

    public AIDParamClientAgent() {
      super();
    }

    @Override
    public void init() {
      delay(1000);
      add(new PoissonBehavior(200) {
        @Override
        public void onTick() {
          AgentID server = agent.agentForService("server");
          if (server == null) {
            log.warning("Unable to find server");
            errors++;
            return;
          }
          count++;
          Integer x = (Integer) server.get(Params.x);
          if (x == null) {
            log.warning("Unable to get x");
            warnings++;
          } else if (x != 1) {
            log.warning("Bad value of x: " + x);
            errors++;
          }
          x = (Integer) server.set(Params.x, 3);
          if (x == null) {
            log.warning("Unable to set x");
            warnings++;
          } else if (x != 4) {
            log.warning("Bad value of set(x): " + x);
            errors++;
          }
          Float y = (Float) server.get(Params.y, 7);
          if (y == null) {
            log.warning("Unable to get y[7]");
            warnings++;
          } else if (y != 49) {
            log.warning("Bad value of y[7]: " + y);
            errors++;
          }
          x = (Integer) server.set(Params.x, 3, 7);
          if (x == null) {
            log.warning("Unable to set x[7]");
            warnings++;
          } else if (x != 45) {
            log.warning("Bad value of x[7]: " + x);
            errors++;
          }
        }
      });
    }
  }
}
