package net.automatalib.automata.oca;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.m;

import net.automatalib.commons.smartcollections.ArrayStorage;

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

    public static final int NUMBER_OF_TRANSITION_FUNCTIONS = 2;

    protected final ArrayStorage<@Nullable Set<TransitionTarget<OCALocation>>> epsilonTransitions;

    public OCALocation(final int initialNumberOfInputs, final int id, final boolean accepting) {
        super(2, initialNumberOfInputs, id, accepting);
        this.epsilonTransitions = new ArrayStorage<>(NUMBER_OF_TRANSITION_FUNCTIONS, HashSet::new);
        // We create empty sets for the transitions
        for (int i = 0 ; i < NUMBER_OF_TRANSITION_FUNCTIONS ; i++) {
            for (int j = 0 ; j < initialNumberOfInputs ; j++) {
                this.transitions.get(i).set(j, new HashSet<>());
            }
        }
    }

    public void addSuccessor(final int counterValue, final int symbolId, final TransitionTarget<OCALocation> target) {
        final int m = Math.min(counterValue, transitions.size() - 1);
        transitions.get(m).get(symbolId).add(target);
    }

    public void setEpsilonSuccessors(final int counterValue, final Set<TransitionTarget<OCALocation>> successors) {
        final int m = Math.min(counterValue, epsilonTransitions.size() - 1);
        epsilonTransitions.set(m, successors);
    }

    public void addEpsilonSuccessor(final int counterValue, final TransitionTarget<OCALocation> successor) {
        final int m = Math.min(counterValue, epsilonTransitions.size() - 1);
        epsilonTransitions.get(m).add(successor);
    }

    public @Nullable Set<TransitionTarget<OCALocation>> getEpsilonSuccessors(final int counterValue) {
        final int m = Math.min(counterValue, epsilonTransitions.size() - 1);
        return epsilonTransitions.get(m);
    }
}
