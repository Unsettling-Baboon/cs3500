package calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * A JUnit 5 testing class for the {@code SimpleCalculator} class.
 */
public class SimpleCalculatorTest extends AbstractCalculatorTest {
  /**
   * A setup method to initialize the {@code calculator} field for
   * every other testing method.
   */
  @Override
  public AbstractCalculator of() {
    calculator = new SimpleCalculator();
    return new SimpleCalculator();
  }

  @Test
  public void inputBehaviorForStartingWithOperator() {
    calculator = of();
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('+')
    );
  }

  @Test
  public void inputBehaviorForTwoConsecutiveOperators() {
    calculator = calculator
            .input('3')
            .input('2')
            .input('+')
            .input('2')
            .input('4')
            .input('+');
    String initialResult = calculator.getResult();
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('-')
    );
    assertEquals(initialResult, calculator.getResult());
  }

  @Test
  public void getResultThrowsForSecondOperandMissing() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('+');
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('=')
    );
  }
}