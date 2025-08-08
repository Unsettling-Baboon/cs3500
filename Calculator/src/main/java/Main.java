import java.io.IOException;
import java.io.InputStreamReader;

import calculator.SimpleCalculator;
import calculator.SmartCalculator;
import controller.CalculatorControllerImpl;

public class Main {
  public static void main(String[] args) {
    try {
      new CalculatorControllerImpl(new SmartCalculator(), System.out)
              .execute(new InputStreamReader(System.in));
    } catch (IOException i) {
      System.out.println(i.getMessage());
    }
  }
}
