package calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A JUnit 5 testing class for the {@code SimpleCalculator} class.
 */
public abstract class AbstractCalculatorTest {
  protected Calculator calculator;

  /**
   * A setup method to initialize the {@code calculator} field for
   * every other testing method.
   */
  @BeforeEach
  public void setup() {
    calculator = of();
  }

  public abstract AbstractCalculator of();

  @Test
  public void inputThrowsForNumbersLongerThan32BitsAndRetainsResult() {
    setup();
    calculator = calculator
            .input('1')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0');
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('0')
    );
    assertEquals("1000000000", calculator.getResult());
    calculator = calculator
            .input('C')
            .input('1')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('+')
            .input('1')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0');
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('0')
    );
    assertEquals("1000000000+1000000000", calculator.getResult());
  }

  @Test
  public void inputThrowsForNonMathematicalCharacters() {
    setup();
    String initialResult = calculator.getResult();
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('f')
    );
    assertEquals(initialResult, calculator.getResult());
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('#')
    );
    assertEquals(initialResult, calculator.getResult());
    assertThrows(
            IllegalArgumentException.class,
            () -> calculator.input('l')
    );
    assertEquals(initialResult, calculator.getResult());
  }

  @Test
  public abstract void inputBehaviorForStartingWithOperator();

  @Test
  public abstract void inputBehaviorForTwoConsecutiveOperators();

  @Test
  public void getResultGetsEmptyStringBeforeAnyAction() {
    setup();
    assertEquals("", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringWhileEnteringFirstOperand() {
    setup();
    calculator = calculator
            .input('3')
            .input('2');
    assertEquals("32", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringAfterEnteringFirstOperator() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('+');
    assertEquals("32+", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringAfterEnteringSecondOperand() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('+')
            .input('2')
            .input('4');
    assertEquals("32+24", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringAfterEnteringOneOperandWithEqualsSign() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('=');
    assertEquals("32", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringAfterEnteringOneOperandWithManyEqualsSigns() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('=')
            .input('=')
            .input('=');
    assertEquals("32", calculator.getResult());
  }

  @Test
  public void getResultGetsCorrectStringForGoodBasicSequenceWithOneOperator() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('*')
            .input('2')
            .input('4')
            .input('=');
    assertEquals("768", calculator.getResult());
  }

  @Test
  public void getResultGetsCorrectStringAddingPreviousResultToCurrentOneIfOperatorAfterEquals() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('+')
            .input('2')
            .input('4')
            .input('=')
            .input('*');
    assertEquals("56*", calculator.getResult());
    calculator = calculator.input('2');
    assertEquals("56*2", calculator.getResult());
    calculator = calculator.input('=');
    assertEquals("112", calculator.getResult());
  }

  @Test
  public void getResultGetsCorrectStringForGoodBasicSequenceWithTwoOperators() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('+')
            .input('2')
            .input('4')
            .input('-')
            .input('1')
            .input('0')
            .input('=');
    assertEquals("46", calculator.getResult());
  }

  @Test
  public void getResultClearsCurrentStringAfterEnteringClearCharacterInCalculator() {
    setup();
    getResultGetsCorrectStringForGoodBasicSequenceWithTwoOperators();
    calculator = calculator.input('C');
    assertEquals("", calculator.getResult());
  }

  @Test
  public void getResultClearsCurrentStringAfterEnteringNumberRightAfterEqualsSign() {
    setup();
    getResultGetsCorrectStringForGoodBasicSequenceWithTwoOperators();
    calculator = calculator.input('1');
    assertEquals("1", calculator.getResult());
  }

  @Test
  public void getResultReturnsCorrectStringWithMultipleEqualsSigns() {
    setup();
    calculator = calculator
            .input('3')
            .input('2')
            .input('-')
            .input('1')
            .input('0')
            .input('=')
            .input('=');
    String result = "22";
    assertEquals(result, calculator.getResult());
  }

  @Test
  public void getResultGetsStringOfZeroForArithmeticOverflowOnResult() {
    setup();
    calculator = calculator
            .input('1')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('+')
            .input('2')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('=');
    assertEquals("0", calculator.getResult());
  }

  @Test
  public void getResultGetsStringOfZeroWithOperatorForArithmeticOverflowOnResult() {
    setup();
    calculator = calculator
            .input('1')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('+')
            .input('2')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('0')
            .input('=')
            .input('-');
    assertEquals("0-", calculator.getResult());
  }
}