package net.automatalib.automata.oca;

import java.security.InvalidParameterException;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.commons.smartcollections.ArrayStorage;

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

    protected final ArrayStorage<@Nullable TransitionTarget<DOCALocation>> epsilonTransitions;

    public DOCALocation(final int initialNumberOfInputs, final int id, final boolean accepting) {
        super(2, initialNumberOfInputs, id, accepting);
        this.epsilonTransitions = new ArrayStorage<>(2);
    }

    TransitionTarget<DOCALocation> getSuccessor(final int symbolId, final int counterValue) {
        return getSuccessors(symbolId, counterValue);
    }

    @Override
    public void setSuccessors(final int counterValue, final int symbolId, final TransitionTarget<DOCALocation> target) {
        setSuccessor(counterValue, symbolId, target);
    }

    public void setSuccessor(final int counterValue, final int symbolId, final TransitionTarget<DOCALocation> target) {
        final int m = Math.min(counterValue, transitions.size() - 1);
        // If there is already an epsilon-transition, we can not add a new transition
        if (epsilonTransitions.get(m) != null) {
            throw new InvalidParameterException(
                    "Impossible to add a transition from a state in a DOCA as there is already an epsilon-transition from that state.");
        }
        transitions.get(m).set(symbolId, target);
    }

    public void setEpsilonSuccessor(final int counterValue, final TransitionTarget<DOCALocation> successor) {
        final int m = Math.min(counterValue, epsilonTransitions.size() - 1);
        // If there is already a transition, we can not add an epsilon-transition
        for (int i = 0; i < transitions.get(m).size(); i++) {
            if (transitions.get(m).get(i) != null) {
                throw new InvalidParameterException(
                        "Impossible to add a epsilon-transition from a state in a DOCA as there is already a transition from that state.");
            }
        }
        epsilonTransitions.set(m, successor);
    }

    public @Nullable TransitionTarget<DOCALocation> getEpsilonSuccessor(final int counterValue) {
        final int m = Math.min(counterValue, epsilonTransitions.size() - 1);
        return epsilonTransitions.get(m);
    }
}
