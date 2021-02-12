package net.automatalib.automata.oca.automatoncountervalues;

import net.automatalib.automata.base.fast.AbstractFastState;

/**
 * States used in {@link DefaultAutomatonWithCounterValues}.
 *
 * A state has two properties: its acceptance (see {@link AcceptingOrExit}), and
 * the associated counter value.
 * 
 * @author Gaëtan Staquet
 */
public final class DefaultAutomatonWithCounterValuesState
        extends AbstractFastState<DefaultAutomatonWithCounterValuesState> {

    private AcceptingOrExit acceptance;
    private int counterValue;

    public DefaultAutomatonWithCounterValuesState(int initialNumOfInputs, int id, AcceptingOrExit accepting, int counterValue) {
        super(initialNumOfInputs);
        setId(id);
        this.setAcceptance(accepting);
        this.setCounterValue(counterValue);
    }

    public int getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(int counterValue) {
        this.counterValue = counterValue;
    }

    public boolean isAccepting() {
        return acceptance == AcceptingOrExit.ACCEPTING;
    }

    public boolean isExit() {
        return acceptance == AcceptingOrExit.EXIT;
    }

    public boolean isRejecting() {
        return acceptance == AcceptingOrExit.REJECTING;
    }

    public AcceptingOrExit getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(AcceptingOrExit accepting) {
        this.acceptance = accepting;
    }

    @Override
    public String toString() {
        return "q" + getId() + " (" + acceptance + ", " + counterValue + ")";
    }

}
