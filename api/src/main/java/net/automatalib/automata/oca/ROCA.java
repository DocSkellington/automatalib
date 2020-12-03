package net.automatalib.automata.oca;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.automatalib.ts.simple.SimpleDTS;

/**
 * Interface for the ROCA (realtime one-counter automaton), which is a DOCA
 * without epsilon-transitions.
 * 
 * @param <L> Location type
 * @param <I> Input symbol type
 * 
 * @author Gaëtan Staquet
 */
public interface ROCA<L, I> extends DOCA<L, I> {

    @Override
    default Set<State<L>> getEpsilonSuccessors(State<L> state) {
        return Collections.emptySet();
    }

    @Override
    default Collection<State<L>> getEpsilonTransitions(State<L> state) {
        return Collections.emptySet();
    }

    @Override
    default State<L> getSuccessor(State<L> state, Iterable<? extends I> input) {
        State<L> curr = state;
        Iterator<? extends I> it = input.iterator();

        while (curr != null && it.hasNext()) {
            I sym = it.next();
            curr = getSuccessor(curr, sym);
        }

        return curr;
    }

    @Override
    default Set<State<L>> getSuccessors(State<L> state, Iterable<? extends I> input) {
        return SimpleDTS.stateToSet(getSuccessor(state, input));
    }
}
