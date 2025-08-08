package model.events;

import java.util.Collection;
import java.util.TreeSet;

/**
 * An interface representing a robust calendar event that now has both editable
 * and queriable properties, with the new addition of being able to be added
 * to a series of calendar events.
 */
public interface RobustCalEvent extends EditableCalEvent, QueriableCalEvent {
  /**
   * A class representing a series of calendar events, useful for keeping track
   * of a series of recurring events.
   */
  class Series {
    private final TreeSet<RobustCalEvent> events;

    /**
     * Construct a new, empty series of calendar events.
     */
    public Series() {
      events = new TreeSet<>();
    }

    /**
     * Retrieve all the events in this series.
     *
     * @return the set of all events contained in this series.
     */
    public TreeSet<RobustCalEvent> getEvents() {
      return events;
    }

    /**
     * Add an event to the series instance variable of this calendar event
     * series.
     *
     * @param event the event to be added.
     */
    public void add(RobustCalEvent event) {
      events.add(event);
    }

    /**
     * Add all events from a collection of events to the series instance
     * variable of this calendar event series.
     *
     * @param eventCollection the source collection of all the events.
     */
    public void addAll(Collection<? extends RobustCalEvent> eventCollection) {
      events.addAll(eventCollection);
    }

    /**
     * Remove an event from the series instance variable of this calendar event
     * series.
     *
     * @param event the event to be removed.
     */
    public void remove(RobustCalEvent event) {
      events.remove(event);
    }
  }
}
