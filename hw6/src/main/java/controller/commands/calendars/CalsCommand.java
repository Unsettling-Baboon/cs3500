package controller.commands.calendars;

import model.calendars.RobustCalendars;

/**
 * An abstract class representing a command for a multi-calendar program,
 * which can be applied on a certain multi-calendar model.
 */
public abstract class CalsCommand {
  protected final RobustCalendars m;
  protected String
          name,
          timeZone,
          property,
          newPropertyVal,
          fromDateStringTtimeString,
          calName,
          newFromDateStringTtimeString,
          onDateString,
          toDateString,
          fromDateString,
          newDateString;

  /**
   * Construct a new calendars command object pointing to a given model.
   *
   * @param m the target model to apply actions to.
   */
  protected CalsCommand(RobustCalendars m) {
    this.m = m;
  }

  /**
   * Carry out the action to be done on the model.
   */
  abstract void perform();
}
