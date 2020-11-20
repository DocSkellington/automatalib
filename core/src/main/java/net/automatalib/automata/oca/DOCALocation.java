package net.automatalib.automata.oca;

/**
 * Default implementation of location for DOCAs.
 * 
 * DOCAs using this kind of locations have two transition functions:
 * <ul>
 * <li>One when the counter value is equal to zero; and</li>
 * <li>One for all counter values strictly greater than zero</li>
 * </ul>
 * 
 * @author Gaëtan Staquet
 */
public class DOCALocation extends AbstractOCALocation<TransitionTarget<DOCALocation>> {
    private static final long serialVersionUID = -7008076153791350517L;

    public DOCALocation(final int initialNumberOfInputs, final boolean accepting) {
        super(2, initialNumberOfInputs, accepting);
    }

    TransitionTarget<DOCALocation> getSuccessor(final int symbolId, final int counterValue) {
        return getSuccessors(symbolId, counterValue);
    }
}
