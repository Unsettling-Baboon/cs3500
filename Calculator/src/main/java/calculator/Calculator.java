package calculator;

/**
 * This interface represents the model of a calculator that has operations offered
 * by a {@code Calculator} object. There are operations to take a single character
 * as input and retrieve the current result of what has been inputted so far.
 */
public interface Calculator {
  /**
   * Create a new {@code Calculator} object including the new input.
   *
   * @param argument the character to add to the {@code Calculator}
   *                 object's display.
   * @return a new {@code Calculator} object with its previous inputs
   *         and the new input.
   */
  Calculator input(char argument) throws IllegalArgumentException;

  /**
   * Calculate the result of the existing inputs.
   *
   * @return the result of applying the desired operations on the current input.
   */
  String getResult();
}
