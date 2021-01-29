package net.automatalib.automata.oca;

import net.automatalib.automata.base.fast.AbstractFastState;

/**
 * States used in {@link DFAWithCounterValues}.
 *
 * A state has two properties: whether it is accepting, and the associated counter value.
 * @author Gaëtan Staquet
 */
public final class DFAWithCounterValuesState extends AbstractFastState<DFAWithCounterValuesState> {

    private boolean accepting;
    private int counterValue;

    public DFAWithCounterValuesState(int initialNumOfInputs, boolean accepting, int counterValue) {
        super(initialNumOfInputs);
        this.setAccepting(accepting);
        this.setCounterValue(counterValue);
    }

    public int getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(int counterValue) {
        this.counterValue = counterValue;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    @Override
    public String toString() {
        return "q" + getId() + " (" + accepting + ", " + counterValue + ")";
    }
    
}
