package net.automatalib.automata.oca;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.ts.acceptors.DeterministicAcceptorTS;
import net.automatalib.ts.simple.SimpleDTS;

/**
 * Interface for the DOCA (deterministic one-counter automaton).
 * 
 * If, for some state and some counter value test, an epsilon-transition is
 * defined, it must be the only defined transition.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public interface DOCA<L, I> extends OCA<L, I>, DeterministicAcceptorTS<State<L>, I> {

    @Override
    default Boolean computeOutput(Iterable<? extends I> input) {
        return OCA.super.computeOutput(input);
    }

    @Override
    default Boolean computeSuffixOutput(Iterable<? extends I> prefix, Iterable<? extends I> suffix) {
        return DeterministicAcceptorTS.super.computeSuffixOutput(prefix, suffix);
    }

    @Override
    default boolean isAccepting(Collection<? extends State<L>> states) {
        return DeterministicAcceptorTS.super.isAccepting(states);
    }

    @Override
    default Set<L> getInitialLocations() {
        return SimpleDTS.stateToSet(getInitialLocation());
    }

    @Override
    default Set<State<L>> getInitialStates() {
        return OCA.super.getInitialStates();
    }

    @Override
    default @Nullable State<L> getInitialState() {
        Set<State<L>> initialStates = getInitialStates();
        if (initialStates.size() == 0) {
            return null;
        }

        Iterator<State<L>> itr = initialStates.iterator();
        State<L> initial = itr.next();
        if (itr.hasNext()) {
            throw new InvalidParameterException("A DOCA can not have multiple initial states");
        }
        return initial;
    }

    @Override
    default @Nullable State<L> getSuccessor(final State<L> state, final Iterable<? extends I> input) {
        Set<State<L>> successors = getSuccessors(Set.of(state), input);
        if (successors.size() == 0) {
            return null;
        }

        Iterator<State<L>> itr = successors.iterator();
        State<L> successor = itr.next();

        return successor;
    }

    @Override
    default @Nullable State<L> getTransition(final State<L> state, final I input) {
        Collection<State<L>> transitions = getTransitions(state, input);
        if (transitions.size() == 0) {
            return null;
        }
        Iterator<State<L>> itr = transitions.iterator();
        State<L> transition = itr.next();
        if (itr.hasNext()) {
            throw new InvalidParameterException("A DOCA can only define one transition per state and per input");
        }
        return transition;
    }

    /**
     * Gives the initial location
     * 
     * @return The initial location
     */
    L getInitialLocation();

    @Override
    public default void addSuccessor(final L start, final int counterValue, final I input, final int counterOperation,
            final L target) {
        setSuccessor(start, counterValue, input, counterOperation, target);
    }

    /**
     * Sets the successor of the state (start, counterValue)
     * 
     * @param start            The starting location
     * @param counterValue     The counter value test
     * @param input            The input symbol
     * @param counterOperation The counter operation to apply
     * @param target           The target location
     */
    public void setSuccessor(final L start, final int counterValue, final I input, final int counterOperation,
            final L target);

    /**
     * Sets the successor of the state
     * 
     * @param start            The starting location
     * @param counterValue     The counter value test
     * @param input            The input symbol
     * @param counterOperation The counter operation to apply
     * @param target           The target location
     */
    public default void setSuccessor(final State<L> start, final I input, final int counterOperation, final L target) {
        setSuccessor(start.getLocation(), start.getCounterValue(), input, counterOperation, target);
    }
}
