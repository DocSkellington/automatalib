package net.automatalib.automata.oca;

public class ROCALocation extends AbstractOCALocation<TransitionTarget<ROCALocation>> {
    private static final long serialVersionUID = 6716566634409087883L;
    
    public static final int NUMBER_OF_TRANSITION_FUNCTIONS = 2;

    public ROCALocation(final int initialNumberOfInputs, final int id, final boolean accepting) {
        super(NUMBER_OF_TRANSITION_FUNCTIONS, initialNumberOfInputs, id, accepting);
    }

    TransitionTarget<ROCALocation> getSuccessor(final int symbolId, final int counterValue) {
        return getSuccessors(symbolId, counterValue);
    }

    public void setSuccessor(final int counterValue, final int symbolId, final TransitionTarget<ROCALocation> target) {
        setSuccessors(counterValue, symbolId, target);
    }
}
