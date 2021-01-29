package net.automatalib.automata.oca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.words.Alphabet;

/**
 * A DFA where the states are annotated with a counter value.
 * @author Gaëtan Staquet
 */
public class DFAWithCounterValues<I> implements DFA<DFAWithCounterValuesState, I> {
    final Alphabet<I> alphabet;
    final List<DFAWithCounterValuesState> states;
    DFAWithCounterValuesState initialState;

    public DFAWithCounterValues(Alphabet<I> alphabet) {
        this.alphabet = alphabet;
        this.states = new ArrayList<>();
    }

    public DFAWithCounterValuesState addState(boolean accepting, int counterValue) {
        DFAWithCounterValuesState state = new DFAWithCounterValuesState(alphabet.size(), accepting, counterValue);
        states.add(state);
        return state;
    }

    public DFAWithCounterValuesState addInitialState(boolean accepting, int counterValue) {
        DFAWithCounterValuesState state = addState(accepting, counterValue);
        initialState = state;
        return state;
    }

    public void setSuccessor(DFAWithCounterValuesState start, I input, DFAWithCounterValuesState target) {
        start.setTransitionObject(alphabet.getSymbolIndex(input), target);
    }

    public void setTransition(DFAWithCounterValuesState start, I input, DFAWithCounterValuesState target) {
        setSuccessor(start, input, target);
    }

    @Override
    public Collection<DFAWithCounterValuesState> getTransitions(DFAWithCounterValuesState state, I input) {
        DFAWithCounterValuesState transition = getTransition(state, input);
        if (transition == null) {
            return Collections.emptySet();
        }
        else {
            return Collections.singleton(transition);
        }
    }

    @Override
    public @Nullable DFAWithCounterValuesState getTransition(DFAWithCounterValuesState state, I input) {
        return state.getTransitionObject(alphabet.getSymbolIndex(input));
    }

    @Override
    public DFAWithCounterValuesState getSuccessor(DFAWithCounterValuesState transition) {
        return transition;
    }

    @Override
    public Set<DFAWithCounterValuesState> getInitialStates() {
        if (initialState == null) {
            return Collections.emptySet();
        }
        else {
            return Collections.singleton(initialState);
        }
    }

    @Override
    public @Nullable DFAWithCounterValuesState getInitialState() {
        return initialState;
    }

    @Override
    public Collection<DFAWithCounterValuesState> getStates() {
        return Collections.unmodifiableList(states);
    }

    @Override
    public @Nullable DFAWithCounterValuesState getSuccessor(DFAWithCounterValuesState state, I input) {
        return getTransition(state, input);
    }

    @Override
    public Boolean getStateProperty(DFAWithCounterValuesState state) {
        return state.isAccepting();
    }

    @Override
    public Void getTransitionProperty(DFAWithCounterValuesState transition) {
        return null;
    }

    @Override
    public boolean isAccepting(DFAWithCounterValuesState state) {
        return state.isAccepting();
    }

    public int getCounterValue(DFAWithCounterValuesState state) {
        return state.getCounterValue();
    }

    public Alphabet<I> getInputAlphabet() {
        return alphabet;
    }
    
    public boolean isCoAccessible(DFAWithCounterValuesState state) {
        if (isAccepting(state)) {
            return true;
        }
        Queue<DFAWithCounterValuesState> queue = new LinkedList<>();
        queue.add(state);
        Set<DFAWithCounterValuesState> seenStates = new HashSet<>();

        while (queue.size() != 0) {
            DFAWithCounterValuesState current = queue.poll();
            seenStates.add(current);

            for (I a : getInputAlphabet()) {
                DFAWithCounterValuesState successor = getSuccessor(current, a);
                if (successor == null) {
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
}
