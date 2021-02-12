package net.automatalib.automata.oca.automatoncountervalues;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Iterables;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.automata.UniversalDeterministicAutomaton;
import net.automatalib.automata.UniversalFiniteAlphabetAutomaton;
import net.automatalib.automata.concepts.DetSuffixOutputAutomaton;
import net.automatalib.automata.oca.ROCA;
import net.automatalib.words.Alphabet;

/**
 * A deterministic finite automaton where the states are annotated with a
 * counter value, up to a given maximal counter value.
 * 
 * For any given word w, the automaton computes
 * <ul>
 * <li>Wether w is accepted;</li>
 * <li>Wether w is an exit point (i.e., the counter value associated with w is
 * strictly greater than the maximal counter value); or</li>
 * <li>w is rejected.</li>
 * </ul>
 * That is, this automaton produces a value taken in the enumeration
 * {@link AcceptingOrExit}.
 * 
 * @author Gaëtan Staquet
 */
public interface AutomatonWithCounterValues<S, I>
        extends UniversalDeterministicAutomaton<S, I, S, AcceptingOrExit, Void>,
        UniversalFiniteAlphabetAutomaton<S, I, S, AcceptingOrExit, Void>,
        DetSuffixOutputAutomaton<S, I, S, AcceptingOrExit> {

    @Override
    public default Collection<S> getTransitions(S state, I input) {
        S transition = getTransition(state, input);
        if (transition == null) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(transition);
        }
    }

    @Override
    public default S getSuccessor(S transition) {
        return transition;
    }

    @Override
    public default Set<S> getInitialStates() {
        if (getInitialState() == null) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(getInitialState());
        }
    }

    @Override
    public default @Nullable S getSuccessor(S state, I input) {
        return getTransition(state, input);
    }

    @Override
    public default Void getTransitionProperty(S transition) {
        return null;
    }

    @Override
    default AcceptingOrExit computeSuffixOutput(Iterable<? extends I> prefix, Iterable<? extends I> suffix) {
        Iterable<I> input = Iterables.concat(prefix, suffix);
        return computeOutput(input);
    }

    public boolean isAccepting(S state);

    public boolean isExit(S state);

    public boolean isRejecting(S state);

    public int getCounterValue(S state);

    public void setStateAcceptance(S state, AcceptingOrExit acceptance);

    public Alphabet<I> getInputAlphabet();

    public default boolean isCoAccessible(S state) {
        if (isAccepting(state)) {
            return true;
        }
        Queue<S> queue = new LinkedList<>();
        queue.add(state);
        Set<S> seenStates = new HashSet<>();
        final long maxCounterValue = (long) Math.pow(size(), 2);

        while (queue.size() != 0) {
            S current = queue.poll();
            seenStates.add(current);

            for (I a : getInputAlphabet()) {
                S successor = getSuccessor(current, a);
                if (successor == null || getCounterValue(successor) > maxCounterValue) {
                    continue;
                }
                if (isAccepting(successor)) {
                    return true;
                }
                if (!queue.contains(successor) && !seenStates.contains(successor)) {
                    queue.add(successor);
                }
            }
        }

        return false;
    }

    public default boolean accepts(Iterable<? extends I> input) {
        return computeOutput(input) == AcceptingOrExit.ACCEPTING;
    }

    /**
     * Gets the width of the automaton, defined as the maximal number of states
     * grouped by counter values.
     * 
     * That is, if the width is three, then there are at most three states of
     * counter value one, at most three of counter value one, and so on.
     * 
     * @return The width
     */
    public int getWidth();

    /**
     * Constructs a very simple {@link ROCA} by copying the automaton.
     * 
     * The built ROCA is just a copy of the automaton, as it uses only one
     * transition function and never modifies the counter value. That is, it does
     * not into account the knowledge about counter values.
     * 
     * @return An ROCA accepting the same language that the current automaton
     */
    public ROCA<?, I> asROCA();

    /**
     * Using the information about counter values, it constructs ROCAs matching
     * these counter values.
     * 
     * @return A list of ROCAs
     */
    public List<ROCA<?, I>> toROCAs(int counterLimit);
}
