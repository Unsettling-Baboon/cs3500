package calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmartCalculatorTest extends AbstractCalculatorTest {

  @Override
  public AbstractCalculator of() {
    calculator = new SmartCalculator();
    return new SmartCalculator();
  }

  @Override
  public void inputBehaviorForStartingWithOperator() {
    calculator = calculator
            .input('+')
            .input('3');
    assertEquals("3", calculator.getResult());
  }

  @Override
  public void inputBehaviorForTwoConsecutiveOperators() {
    calculator = calculator
            .input('+')
            .input('3')
            .input('+')
            .input('-');
    assertEquals("3-", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectlyForSecondOperandMissing() {
    calculator = calculator
            .input('2')
            .input('4')
            .input('+')
            .input('3')
            .input('2')
            .input('=')
            .input('=');
    assertEquals("88", calculator.getResult());
  }
}