package net.automatalib.automata.oca;

import java.util.Set;

/**
 * Default implementation of location for OCAs.
 * 
 * OCAs using this kind of locations have two transition functions:
 * <ul>
 * <li>One when the counter value is equal to zero; and</li>
 * <li>One for all counter values strictly greater than zero</li>
 * </ul>
 * 
 * @author Gaëtan Staquet
 */
public class OCALocation extends AbstractOCALocation<Set<TransitionTarget<OCALocation>>> {
    private static final long serialVersionUID = 2599828083385204289L;

    public OCALocation(final int initialNumberOfInputs, final boolean accepting) {
        super(2, initialNumberOfInputs);
    }

    public void addSuccessor(final int counterValue, final int symbolId, TransitionTarget<OCALocation> target) {
        final int m = Math.min(counterValue, transitions.size() - 1);
        transitions.get(m).get(symbolId).add(target);
    }
}
