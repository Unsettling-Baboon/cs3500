package calculator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements an immutable, simple {@link Calculator} object. It has
 * a field {@code currentlyEnteredSymbols} that stores the history of entered
 * inputs, and has the ability to derive the result from that history in
 * {@code getResult()}, and the ability to pass the history on to future
 * instances, with a provided argument appended on, in {@code input()}.
 */
public class SimpleCalculator extends AbstractCalculator {
  /**
   * Construct a new {@link SimpleCalculator} object with an empty
   * {@code currentResult} String.
   */
  public SimpleCalculator() {
    super();
  }

  // used to copy over the current object to a new one in input(), maintaining immutability
  private SimpleCalculator(String lastResult) {
    super(lastResult);
  }

  @Override
  protected AbstractCalculator of() {
    return of("");
  }

  @Override
  protected AbstractCalculator of(String lastResult) {
    return new SimpleCalculator(lastResult);
  }

  @Override
  protected AbstractCalculator handleUnsupportedInputs(char argument, char lastChar) {
    // check conditions specific to when nothing has been inputted yet (an empty result)
    if (currentlyEnteredSymbols.isEmpty()) {
      // cannot start with operator
      if (OPERATORS.contains(argument)) {
        throw new IllegalArgumentException("Cannot start an expression with an operator.");
      }
    } else {
      // if both argument and last entered character are operators, entry should be rejected
      if (OPERATORS.contains(argument) && OPERATORS.contains(lastChar)) {
        throw new IllegalArgumentException("Cannot enter consecutive operators.");
      }
      if (OPERATORS.contains(lastChar) && argument == EQUALS_CHARACTER) {
        throw new IllegalArgumentException("Cannot enter equals sign after operator.");
      }
    }
    // return null to account for abstract class implementation of input()
    return null;
  }

  @Override
  public String getResult() throws IllegalArgumentException {
    // create a copy of the currentlyEnteredSymbols field
    String currentlyEnteredSymbols = new StringBuilder(this.currentlyEnteredSymbols).toString();
    // if the result string is literally empty, return it
    if (currentlyEnteredSymbols.isEmpty()) {
      return currentlyEnteredSymbols;
    }
    // if there's no numerical argument or the last character is an operator, return current
    char lastCharacter = getLastCharacterInResult();
    char[] symbolsAsArray = currentlyEnteredSymbols.toCharArray();
    int lastCharacterIndex = currentlyEnteredSymbols.length() - 1;
    // now, proceed to truncate the String if there are things after the last equals sign
    Result helperResult = handleSymbolsAfterEqualsSigns(
            lastCharacterIndex, symbolsAsArray,
            lastCharacter, currentlyEnteredSymbols
    );
    // reassign the calculated values
    int equalsCount = helperResult.equalsCount();
    String ending = helperResult.ending();
    currentlyEnteredSymbols = helperResult.currentlyEnteredSymbols();
    // ensure that calculations are only carried out when there's an ending equals sign
    if (lastCharacter == EQUALS_CHARACTER
            || currentlyEnteredSymbols.contains(String.valueOf(EQUALS_CHARACTER))
    ) {
      /* if there are no operators, then print the currently entered symbols, now guaranteed
         to only be numbers; works with >0 equals sign case with only one number case too */
      String potentialResult = handleManyEqualsSignsAfterOneNumber(
              lastCharacterIndex, symbolsAsArray
      );
      // only null in the case that there are indeed operators
      if (potentialResult != null) {
        currentlyEnteredSymbols = potentialResult;
        return currentlyEnteredSymbols;
      }
      // if there are indeed operators, split the symbols into numbers and operators
      List<String> numericalArgumentsAsList = getNumericalArgumentsAsList(currentlyEnteredSymbols);
      // however, if there's an operator after the equals sign, remove it, then add it at end
      List<String> operatorArgumentsAsList = getOperatorArgumentsAsList(currentlyEnteredSymbols);
      // define the result to be calculated as long—just in case it is longer than 32 bits
      long currentlyCalculatedResult;
      // initialize the result to be calculated as the first number from those entered
      currentlyCalculatedResult = Integer.parseInt(numericalArgumentsAsList.getFirst());
      // iterate over the numbers in the symbols and perform actions based on the operators
      currentlyCalculatedResult = performCalculations(
              operatorArgumentsAsList, numericalArgumentsAsList, currentlyCalculatedResult
      );
      // ensure that the result can fit in an int
      return overflowSafeResult(currentlyCalculatedResult, equalsCount, lastCharacter, ending);
    }
    /* case where only numbers are in the result string (already covered, statement is here
       only just to promise java that something is always returned) */
    return currentlyEnteredSymbols;
  }

  // getResult() helper method 1
  private Result handleSymbolsAfterEqualsSigns(
          int lastCharacterIndex, char[] symbolsAsArray,
          char lastCharacter, String currentlyEnteredSymbols
  ) {
    String ending = "";
    StringBuilder endingBuilder = new StringBuilder();
    int equalsCount = 0;
    // calculate amount of equals signs amongst these symbols
    for (int i = 0; i <= lastCharacterIndex; i++) {
      if (symbolsAsArray[i] == EQUALS_CHARACTER) {
        equalsCount++;
      }
    }
    // if there are indeed more than one equals sign...
    if (equalsCount > 0 && lastCharacter != EQUALS_CHARACTER) {
      // calculate everything before the last equals sign...
      StringBuilder result = new StringBuilder();
      int lastEqualsIndex = currentlyEnteredSymbols.lastIndexOf(EQUALS_CHARACTER);
      // this ensures JIT calculation of all inputs BEFORE the last equals sign...
      for (int j = 0; j <= lastEqualsIndex; j++) {
        result.append(symbolsAsArray[j]);
      }
      // and maintains immutability of both currentlyEnteredSymbols field AND this object
      for (int k = lastEqualsIndex + 1; k <= lastCharacterIndex; k++) {
        endingBuilder.append(symbolsAsArray[k]);
      }
      // store calculated values in relevant objects
      currentlyEnteredSymbols = result.toString();
      ending = endingBuilder.toString();
    }
    return new Result(currentlyEnteredSymbols, ending, equalsCount);
  }

  // getResult() helper method 2
  private String handleManyEqualsSignsAfterOneNumber(
          int lastCharacterIndex, char[] symbolsAsArray
  ) {
    for (int l = 0; l <= lastCharacterIndex; l++) {
      if (OPERATORS.contains(symbolsAsArray[l])) {
        break;
      } else {
        if (l == lastCharacterIndex) {
          // now guaranteed that everything in the input is only numbers...
          StringBuilder result = new StringBuilder();
          for (char symbol : symbolsAsArray) {
            // so now, remove all the equals signs, if there's multiple
            if (symbol != EQUALS_CHARACTER) {
              result.append(symbol);
            }
          }
          return result.toString();
        }
      }
    }
    return null;
  }

  // getResult() helper method 3
  private List<String> getNumericalArgumentsAsList(String currentlyEnteredSymbols) {
    // if there are indeed operators, split the symbols into numbers and operators
    String[] numericalArgumentsInitial = currentlyEnteredSymbols.split("[-+*=]+");
    // ensure there are no empty Strings from split() method adjacent symbol behavior
    List<String> numericalArgumentsAsList = new LinkedList<>();
    for (String numericalArgument : numericalArgumentsInitial) {
      if (!numericalArgument.isEmpty()) {
        numericalArgumentsAsList.add(numericalArgument);
      }
    }
    return numericalArgumentsAsList;
  }

  // getResult() helper method 4
  private List<String> getOperatorArgumentsAsList(String currentlyEnteredSymbols) {
    // however, if there's an operator after the equals sign, remove it, then add it at end
    String[] operatorArgumentsInitial = Arrays
            .stream(currentlyEnteredSymbols.split("[0123456789=]+"))
            .toList()
            .subList(1, currentlyEnteredSymbols.split("[0123456789=]+").length)
            .toArray(String[]::new);
    // ensure there are no empty Strings from split() method adjacent symbol behavior
    return placeArguments(operatorArgumentsInitial);
  }

  // getOperatorArgumentsAsList() and getNumericalArgumentsAsList() common code refactored
  private List<String> placeArguments(String[] initialArguments) {
    List<String> argumentsAsList = new LinkedList<>();
    for (String initialArgument : initialArguments) {
      if (!initialArgument.isEmpty()) {
        argumentsAsList.add(initialArgument);
      }
    }
    return argumentsAsList;
  }

  // getResult() helper method 5
  private long performCalculations(
          List<String> operatorArgumentsAsList,
          List<String> numericalArgumentsAsList,
          long currentlyCalculatedResult
  ) {
    for (int m = 0; m < operatorArgumentsAsList.size(); m++) {
      // define the relevant variables
      String currentOperator = operatorArgumentsAsList.get(m);
      /* numerical arguments' size now same as operator arguments size because currently
         calculated result takes care of the first number, and the ending equals sign is
         taken care of during splitting */
      String nextOperand = numericalArgumentsAsList.get(m + 1);
      long nextOperandAsInt = Integer.parseInt(nextOperand);
      // actually perform the operations, referring to the relevant symbols
      switch (currentOperator) {
        case "+" -> currentlyCalculatedResult += nextOperandAsInt;
        case "-" -> currentlyCalculatedResult -= nextOperandAsInt;
        case "*" -> currentlyCalculatedResult *= nextOperandAsInt;
        default -> System.out.println("Impossible!");
      }
    }
    return currentlyCalculatedResult;
  }

  // getResult() helper method 6
  private String overflowSafeResult(
          long currentlyCalculatedResult, int equalsCount,
          char lastCharacter, String ending
  ) {
    if (Math.abs(currentlyCalculatedResult) <= Integer.MAX_VALUE) {
      // append ending characters again if there were some after the last equals sign
      if (equalsCount > 0 && lastCharacter != '=') {
        return String.format("%d%s", currentlyCalculatedResult, ending);
      } else {
        // otherwise, print the result
        return String.format("%d", currentlyCalculatedResult);
      }
    } else {
      // same as previous branch's first sub-branch
      if (equalsCount > 0 && lastCharacter != '=') {
        return String.format("%d%s", 0, ending);
      } else {
        // same as previous branch's second sub-branch
        return String.format("%d", 0);
      }
    }
  }

  // getResult() helper record class
  private record Result(String currentlyEnteredSymbols, String ending, int equalsCount) { }
}
