package net.automatalib.automata.oca;

/**
 * A state stores a location and a counter value.
 * 
 * @param <L> Location type
 * 
 * @author Gaëtan Staquet
 */
public final class State<L> {

    private final L loc;
    private final int counterValue;

    public State(L loc, int counterValue) {
        this.loc = loc;
        this.counterValue = counterValue;
    }

    public L getLocation() {
        return loc;
    }

    public int getCounterValue() {
        return counterValue;
    }
    
}
