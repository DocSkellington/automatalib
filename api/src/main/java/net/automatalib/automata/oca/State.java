package net.automatalib.automata.oca;

import java.util.Objects;

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
    
    @Override
    public String toString() {
        return "(" + loc + ", " + counterValue + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if(!(obj instanceof State<?>)) {
            return false;
        }
        final State<?> o = (State<?>)obj;
        if (counterValue == o.counterValue) {
            return (o.loc == null && this.loc == null) || (o.loc.equals(loc));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(loc, counterValue);
    }
}
