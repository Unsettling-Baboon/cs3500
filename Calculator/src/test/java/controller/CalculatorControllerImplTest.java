package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import calculator.MockCalculator;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorControllerImplTest {

  @Test
  void execute() {
    // controller.execute(); no way to test!!
    String input = "4\n3\nQ\n"; // user "input"; whitespace means that nextInt() reads next number
    Readable in = new InputStreamReader(new ByteArrayInputStream(input.getBytes())); // input the bytified String
    ByteArrayOutputStream outStream =  new ByteArrayOutputStream(); // define empty outstream
    Appendable out = new PrintStream(outStream); // object which can print to output stream!
    MockCalculator mockModel = new MockCalculator(); // test: only place where we use class type
    // making the mock model so input records can be found
    // pass in the input (the test string)
    CalculatorController controller = new CalculatorControllerImpl(
            mockModel, // making the mock model so input records can be found
            out // pass in the input (the test string)
    );
    try {
      controller.execute(in);
    } catch (IOException i) {
      System.out.println("Failed.");
    }
    String expectedOutput = "4"; // needed bc newline character added
    String actualOutput = mockModel.getLog(); // used class type to be able to call getLog()
    assertEquals(expectedOutput, actualOutput);
    String expectedPrint = "0";
    String actualPrint = outStream.toString();
    assertEquals(expectedPrint, actualPrint);
  }
}