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
            if ((o.loc == null) != (this.loc == null)) {
                return false;
            }
            if (o.loc == null) { // So, both are null
                return true;
            }
            else { // So, both are not null
                return this.loc.equals(o.loc);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(loc, counterValue);
    }
}
