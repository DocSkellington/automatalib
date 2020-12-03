package net.automatalib.automata.oca;

/**
 * Default implementation of location for VCAs.
 * 
 * The number of transition functions is fixed at the creation of the location.
 * 
 * @author Gaëtan Staquet
 */
public class VCALocation extends AbstractOCALocation<TransitionTarget<VCALocation>> {
    private static final long serialVersionUID = 4568777401124027184L;

    public VCALocation(final int numberOfTransitionFunctions, final int initialNumberOfInputs, final int id,
            final boolean accepting) {
        super(numberOfTransitionFunctions, initialNumberOfInputs, id, accepting);
    }

    TransitionTarget<VCALocation> getSuccessor(final int symbolId, final int counterValue) {
        return getSuccessors(symbolId, counterValue);
    }

    @Override
    public void setSuccessors(final int counterValue, final int symbolId, final TransitionTarget<VCALocation> target) {
        setSuccessor(counterValue, symbolId, target);
    }

    public void setSuccessor(final int counterValue, final int symbolId, final TransitionTarget<VCALocation> target) {
        final int m = Math.min(counterValue, transitions.size() - 1);
        transitions.get(m).set(symbolId, target);
    }
}
