package net.automatalib.automata.oca;

public class ROCALocation extends AbstractOCALocation<TransitionTarget<ROCALocation>> {
    private static final long serialVersionUID = 6716566634409087883L;

    public ROCALocation(final int initialNumberOfInputs, final int id, final boolean accepting) {
        super(2, initialNumberOfInputs, id, accepting);
    }

    TransitionTarget<ROCALocation> getSuccessor(final int symbolId, final int counterValue) {
        return getSuccessors(symbolId, counterValue);
    }

    public void setSuccessor(final int counterValue, final int symbolId, final TransitionTarget<ROCALocation> target) {
        setSuccessors(counterValue, symbolId, target);
    }
}
