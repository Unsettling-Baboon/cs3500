package calculator;

import java.util.List;

/**
 * A class representing a mock model of the {@code Calculator} implementation
 * so that tests of the controller can use this for testing purposes.
 */
public class MockCalculator implements Calculator {
  private final StringBuilder log;

  public MockCalculator() {
    log = new StringBuilder();
  }

  @Override
  public Calculator input(char argument) {
    log.append(argument);
    log.append("~");
    return this;
  }

  @Override
  public String getResult() {
    return "0";
  }

  public String getLog() {
    return log.toString();
  }
}
