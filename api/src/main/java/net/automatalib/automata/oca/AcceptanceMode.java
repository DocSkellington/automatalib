package net.automatalib.automata.oca;

/**
 * The acceptance mode of an OCA.
 * 
 * @author Gaëtan Staquet
 */
public enum AcceptanceMode {
    /**
     * A run ending in an accepting location is accepting
     */
    ACCEPTING_LOCATION,
    /**
     * A run ending in a state with a counter value of zero is accepting.
     */
    COUNTER_ZERO,
    /**
     * A run ending in a state with an accepting location and a counter value equal
     * to zero is accepting.
     */
    BOTH
}
