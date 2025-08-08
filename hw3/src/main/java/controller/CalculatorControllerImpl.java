package controller;

import java.io.IOException;
import java.util.Scanner;

import calculator.Calculator;
import calculator.SimpleCalculator;

/**
 * This class represents an implementation of the calculator controller interface
 * that offers the ability to instantiate the controller and call the {@code execute()}
 * method from this object to start the calculator program.
 */
public class CalculatorControllerImpl implements CalculatorController {
  private final Appendable out;
  private Calculator model;

  /**
   * Constructs a new instance of a controller for the calculator program
   * that takes a model for the program as an argument, as well as an argument
   * for where to write outputs to.
   *
   * @param model the model needed for processing.
   * @param out   where output should be sent.
   */
  public CalculatorControllerImpl(Calculator model, Appendable out) {
    this.model = model;
    this.out = out;
  }

  @Override
  public void execute(Readable rd) throws IOException {
    if (rd == null) {
      throw new IOException("Input is not readable!");
    }
    redoNextInput((char) 0, rd);
  }

  private char redoNextInput(char nextInput, Readable in) {
    // initialize relevant variables
    Scanner scanner;
    String nextLine;
    // recursion quit condition
    if (nextInput == 'Q') {
      return nextInput;
    }
    // wrap actions and try-catch in do-while to keep calculator always running
    do {
      try {
        scanner = new Scanner(in);
        System.out.print("Enter an input (\"Q\" for exit): ");
        nextLine = scanner.nextLine();
        // ensure that an empty character is not attempted to be made
        if (!nextLine.isEmpty()) {
          nextInput = nextLine.toCharArray()[0];
          // make sure that quitting doesn't print anything new nor add a "Q" to the model
          if (nextInput != 'Q') {
            model = model.input(nextInput);
            out.append(String.format("%s\n", model.getResult()));
          }
        } else {
          nextInput = (char) 0;
        }
      } catch (Exception e) {
        // retrieve the appropriate error message
        System.out.println(e.getMessage());
        /* apply recursion to maintain infinite exception catching, and pass in
           the input so that previous results can all be retained in recursion */
        nextInput = redoNextInput(nextInput, in);
      }
    } while (nextInput != 'Q');
    return nextInput;
  }
}
