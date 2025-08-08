package calculator;

import java.util.List;

/**
 * A class that factors out common elements of both the
 * {@link SimpleCalculator} and {@link SmartCalculator} classes.
 */
public abstract class AbstractCalculator implements Calculator {
  // define meaningfully named constants
  protected static final List<Character> POSSIBLE_CHARACTERS = List.of(
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '=', 'C', 'Q'
  );
  protected static final List<Character> NUMBERS = POSSIBLE_CHARACTERS.subList(0, 10);
  protected static final List<Character> OPERATORS = POSSIBLE_CHARACTERS.subList(10, 13);
  protected static final char EQUALS_CHARACTER = POSSIBLE_CHARACTERS.get(13);
  protected static final char CLEAR_CHARACTER = POSSIBLE_CHARACTERS.get(14);
  // define a field to store the currently entered numbers and operators
  protected final String currentlyEnteredSymbols;

  /**
   * Construct a new {@code Calculator} object with an empty {@code
   * currentlyEnteredSymbols} field.
   */
  protected AbstractCalculator() {
    this("");
  }

  // used to copy over the current object to a new one in input(), maintaining immutability
  protected AbstractCalculator(String lastResult) {
    currentlyEnteredSymbols = lastResult;
  }

  /**
   * An instance factory method for copying over the current object to maintain
   * immutability, left abstract for allowing refactoring of code in the
   * {@code input()} method.
   *
   * @return an empty instance of this class that extends {@link AbstractCalculator}
   */
  protected abstract AbstractCalculator of();

  /**
   * An instance factory method for copying over the current object to maintain
   * immutability, left abstract for allowing refactoring of code in the
   * {@code input()} method.
   *
   * @param result the result to copy over to the next object
   * @return       an instance of this class that extends {@link AbstractCalculator}
   *               with the {@code result}
   */
  protected abstract AbstractCalculator of(String result);

  @Override
  public Calculator input(char argument) {
    // before anything, if it's 'C', clear the result, as this would have no restrictions
    if (argument == CLEAR_CHARACTER) {
      return this.of();
    }
    // regardless of anything else, a non-arithmetic symbol cannot be entered
    if (!POSSIBLE_CHARACTERS.contains(argument)) {
      throw new IllegalArgumentException("Can only enter numbers or arithmetic operators.");
    }
    // find last character in currently entered symbols
    char lastCharacterInResult = getLastCharacterInResult();
    // perform checks specific to if the currently entered symbols aren't empty
    if (!currentlyEnteredSymbols.isEmpty()) {
      // protect from overflow
      protectFromOverflow(argument, lastCharacterInResult);
      // if the last character is an equals sign and argument is a number, move on
      if (lastCharacterInResult == EQUALS_CHARACTER && NUMBERS.contains(argument)) {
        return this.of(String.valueOf(argument));
      }
    }
    // check that inputs follow guidelines specific to this calculator
    AbstractCalculator potentialReturnValue = handleUnsupportedInputs(
            argument, lastCharacterInResult
    );
    // if everything goes well, return the input appended to this calculator
    return (potentialReturnValue != null) ?
            potentialReturnValue :
            this.of(String.format("%s%s", currentlyEnteredSymbols, argument));
  }

  // input() helper method 1
  protected char getLastCharacterInResult() {
    return (!currentlyEnteredSymbols.isEmpty())
            ? currentlyEnteredSymbols.toCharArray()[currentlyEnteredSymbols.length() - 1]
            : (char) 0;
  }

  // input() helper method 2
  protected void protectFromOverflow(char argument, char lastCharacterInResult) {
    // if it's a number and argument's a number, make sure no overflow occurs
    if (NUMBERS.contains(argument) && NUMBERS.contains(lastCharacterInResult)) {
      // find the last entered number, and find what it could be by parsing a long from it
      String[] numericalArguments = currentlyEnteredSymbols.split("[-+*=]+");
      String lastEnteredNumber = numericalArguments[numericalArguments.length - 1];
      String lastNumberWithInput = String.format("%s%s", lastEnteredNumber, argument);
      long potentiallyLong = Long.parseLong(lastNumberWithInput);
      // if it is indeed too large, throw an exception
      if (potentiallyLong > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("Input makes previously "
                + "inputted number too large.");
      }
    }
  }

  // input() helper method 3
  protected abstract AbstractCalculator handleUnsupportedInputs(char argument, char lastChar);
}
