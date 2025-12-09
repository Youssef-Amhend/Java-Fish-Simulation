package com.dtp5.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight event bus for decoupled communication between simulation
 * components.
 * Thread-safe and supports multiple listeners per event type.
 * 
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * EventBus bus = EventBus.getInstance();
 * bus.subscribe(FishDeathEvent.class, event -> {
 *     System.out.println("Fish died at: " + event.getPosition());
 * });
 * bus.publish(new FishDeathEvent(fish));
 * </pre>
 * 
 * @author Ocean Ecosystem Team
 * @version 2.0.0
 */
public class EventBus {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    /** Singleton instance */
    private static final EventBus INSTANCE = new EventBus();

    /** Map of event types to their listeners */
    private final Map<Class<? extends SimulationEvent>, List<Consumer<? extends SimulationEvent>>> listeners;

    /** Queue of events to process (for batched processing) */
    private final List<SimulationEvent> eventQueue;

    /** Whether to process events immediately or queue them */
    private boolean immediateMode = true;

    private EventBus() {
        this.listeners = new ConcurrentHashMap<>();
        this.eventQueue = new CopyOnWriteArrayList<>();
    }

    /**
     * Gets the singleton event bus instance.
     * 
     * @return The event bus
     */
    public static EventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Subscribes to events of a specific type.
     * 
     * @param <T>       Event type
     * @param eventType The class of events to subscribe to
     * @param handler   Handler to call when events occur
     */
    @SuppressWarnings("unchecked")
    public <T extends SimulationEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<SimulationEvent>) handler);
        logger.debug("Subscribed to {} events", eventType.getSimpleName());
    }

    /**
     * Unsubscribes a handler from events.
     * 
     * @param <T>       Event type
     * @param eventType The class of events to unsubscribe from
     * @param handler   Handler to remove
     */
    public <T extends SimulationEvent> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        List<Consumer<? extends SimulationEvent>> handlers = listeners.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    /**
     * Publishes an event to all subscribers.
     * 
     * @param event The event to publish
     */
    @SuppressWarnings("unchecked")
    public void publish(SimulationEvent event) {
        if (immediateMode) {
            dispatchEvent(event);
        } else {
            eventQueue.add(event);
        }
    }

    /**
     * Dispatches an event to all its listeners.
     */
    @SuppressWarnings("unchecked")
    private void dispatchEvent(SimulationEvent event) {
        List<Consumer<? extends SimulationEvent>> handlers = listeners.get(event.getClass());
        if (handlers != null && !handlers.isEmpty()) {
            for (Consumer handler : handlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    logger.error("Error handling event {}: {}", event.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Processes all queued events (when not in immediate mode).
     */
    public void processQueue() {
        if (immediateMode)
            return;

        List<SimulationEvent> toProcess = new ArrayList<>(eventQueue);
        eventQueue.clear();

        for (SimulationEvent event : toProcess) {
            dispatchEvent(event);
        }
    }

    /**
     * Sets whether events should be processed immediately or queued.
     * 
     * @param immediate true for immediate processing, false for batched
     */
    public void setImmediateMode(boolean immediate) {
        this.immediateMode = immediate;
    }

    /**
     * Clears all listeners (useful for testing).
     */
    public void clearAllListeners() {
        listeners.clear();
        eventQueue.clear();
    }

    /**
     * Gets the number of listeners for a specific event type.
     * 
     * @param eventType Event class
     * @return Number of listeners
     */
    public int getListenerCount(Class<? extends SimulationEvent> eventType) {
        List<Consumer<? extends SimulationEvent>> handlers = listeners.get(eventType);
        return handlers == null ? 0 : handlers.size();
    }
}
