package controller.gui;

import java.awt.event.ActionListener;

/**
 * Defines the interface for the Controller in the MVC pattern for the calendar application.
 * The controller is responsible for handling user input from the view, interacting with the model,
 * and directing the view to update its display.
 *
 * It extends ActionListener to directly handle events from the Swing view components.
 */
public interface CalendarsControllerGUI extends ActionListener {
  /**
   * Starts the application.
   * This method should handle the initial setup, such as making the view visible
   * and loading the initial state from the model into the view.
   */
  void start();
}