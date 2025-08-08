package controller;

import java.io.IOException;

/**
 * This interface represents the controller of a calculator that takes user input,
 * applies calculations on it, and communicates the results to where the user can
 * see them.
 */
public interface CalculatorController {
  /**
   * Initialize the model (and "view") and allow numbers to be inputted
   * for calculation.
   *
   * @param rd a readable input.
   * @throws IOException if the input isn't readable.
   */
  void execute(Readable rd) throws IOException;
}