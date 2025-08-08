package calculator;

/**
 * This class implements a smarter immutable {@link Calculator} object. It has
 * a field {@code currentlyEnteredSymbols} that stores the history of entered
 * inputs, and has the ability to derive the result from that history in
 * {@code getResult()}, and the ability to pass the history on to future
 * instances, with a provided argument appended on, in {@code input()}. It can
 * do more than the {@link SimpleCalculator} class, with more supported wrong
 * input patterns.
 */
public class SmartCalculator extends AbstractCalculator {
  /**
   * Construct a new {@link SmartCalculator} object with an empty
   * {@code currentResult} String.
   */
  public SmartCalculator() {
    super();
  }

  // used to copy over the current object to a new one in input(), maintaining immutability
  private SmartCalculator(String lastResult) {
    super(lastResult);
  }

  @Override
  protected AbstractCalculator of() {
    return of("");
  }

  @Override
  protected AbstractCalculator of(String lastResult) {
    return new SmartCalculator(lastResult);
  }

  @Override
  protected AbstractCalculator handleUnsupportedInputs(char argument, char lastChar) {
    // check conditions specific to when nothing has been inputted yet (an empty result)
    if (currentlyEnteredSymbols.isEmpty()) {
      // now, CAN start with just one operator
      if (argument == '+') {
        System.out.println("The plus-sign operator is ignored at the start of an expression.");
        return this.of();
      } else if (OPERATORS.contains(argument)) {
        throw new IllegalArgumentException("Cannot start an expression with an operator.");
      }
    } else {
      String intermediateString = currentlyEnteredSymbols
              .substring(0, currentlyEnteredSymbols.length() - 1);
      // if both argument and last entered character are operators, ignore the previous
      if (OPERATORS.contains(argument) && OPERATORS.contains(lastChar)) {
        System.out.println("With consecutive operators, only the latest one is counted.");
        return this.of(String.format("%s%s", intermediateString, argument));
      }
    }
    // if no condition is satisfied, ignore the return value with a null
    return null;
  }

  private String getActualCurrentSymbols(String currentlyEnteredSymbols) {
     /* numerical arguments' size now same as operator arguments size WITHOUT EQUALS SIGNS,
       because currently calculated result takes care of the first number, and the ending
       equals sign is taken care of during splitting. to take care of equals signs, though,
       an approach must be developed to allow for us to properly process multiple equals
       signs. here are the cases we must address:

       [

       0. a sole equals sign followed by an operator, which should be allowed, and the
          lone equals sign case is never encountered thanks to the input() method
          EDIT: this will automatically be accounted for in handleSymbolsAfterEqualsSigns()

                                                                                           ]

       1. many equals signs following a number, in which case they should be ignored,
          i.e. "123====" should become "123" -> DONE
       2. many equals signs following an operator right after a number, in which case
          that operator should be continuously applied to that number on itself, if alone,
          i.e. "23 + ===" should become "23 + 23 + 23 + 23"
       3. many equals signs following an operator right after a number that is the last
          operand in a series of other numbers, in which case the cumulative result should
          have the operator repeatedly applied on that last operand to the cumulative
          result, i.e. "24 + 32 ===" should be "24 + (32 + 32 + 32)"

       the question arises, which case to prioritize amongst 2 and 3 if ambiguity between
       the two is presented? the choice that we make here is simple, that 2 dominates 3,
       i.e. "24 + 32 - 44 * ===" becomes "(24 + 32 - 44) * ... * ... * (24 + 32 - 44)"

       next, we must also account for the discrepancy between the sizes of the numerical
       and operator argument lists' sizes now; we must pause when reaching an equals sign
       and deal with the above cases before then continuing to increment the argument */
    StringBuilder newSymbols = new StringBuilder();
    String intermediateSymbols = new StringBuilder(currentlyEnteredSymbols).toString();
    char[] interSymbols = intermediateSymbols.toCharArray();
    for (int i = 0; i < interSymbols.length; i++) {
      char currentSymbol = interSymbols[i];
      if (NUMBERS.contains(currentSymbol)) {
        newSymbols.append(currentSymbol);
      }
      char nextSymbol = (i + 1 < interSymbols.length) ? interSymbols[i + 1] : (char) 0;
      if (OPERATORS.contains(currentSymbol)) {
        StringBuilder prevNumberReverse = new StringBuilder();
        String prevNumber;
        if (nextSymbol == EQUALS_CHARACTER) {
          for (int j = i - 1; j >= 0; j--) {
            char possiblePrevNumber = interSymbols[j];
            if (NUMBERS.contains(possiblePrevNumber)) {
              prevNumberReverse.append(possiblePrevNumber);
            } else {
              break;
            }
          }
          prevNumber = prevNumberReverse.reverse().toString();
          for (int k = i + 1; k < interSymbols.length; k++) {
            if (interSymbols[k] == EQUALS_CHARACTER) {
              newSymbols.append(currentSymbol);
              newSymbols.append(prevNumber);
            } else {
              break;
            }
          }
        }
        if (NUMBERS.contains(nextSymbol)) {
          StringBuilder nextNum = new StringBuilder();
          for (int l = i + 1; l < interSymbols.length; l++) {
            char nextNumberOrEquals = interSymbols[l];
            if (NUMBERS.contains(nextNumberOrEquals)) {
              nextNum.append(nextNumberOrEquals);
            } else {
              if (nextNumberOrEquals == EQUALS_CHARACTER) {
                if (l + 1 < interSymbols.length
                        && interSymbols[l + 1] == EQUALS_CHARACTER) {
                  for (int m = i + 1; m < interSymbols.length; m++) {
                    if (interSymbols[m] == EQUALS_CHARACTER) {
                      newSymbols.append(currentSymbol);
                      newSymbols.append(nextNum);
                    } else {
                      break;
                    }
                  }
                }
              }
              break;
            }
          }
        }
        newSymbols.append(currentSymbol);
      }
      if (currentSymbol == EQUALS_CHARACTER) {
        if (i == interSymbols.length - 1 || i == currentlyEnteredSymbols.lastIndexOf('=')) {
          newSymbols.append(currentSymbol);
        }
      }
    }
    return newSymbols.toString();
  }

  @Override
  public String getResult() {
    return new SimpleCalculator().of(getActualCurrentSymbols(currentlyEnteredSymbols)).getResult();
  }
}
