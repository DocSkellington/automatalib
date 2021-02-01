package net.automatalib.automata.oca.automatoncountervalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.words.Alphabet;

/**
 * A default implementation of an {@link AutomatonWithCounterValues}.
 * 
 * @author Gaëtan Staquet
 */
public class DefaultAutomatonWithCounterValues<I>
        extends AbstractAutomatonWithCounterValues<DefaultAutomatonWithCounterValuesState, I> {

    final List<DefaultAutomatonWithCounterValuesState> states;
    DefaultAutomatonWithCounterValuesState initialState;

    public DefaultAutomatonWithCounterValues(Alphabet<I> alphabet) {
        super(alphabet);
        this.states = new ArrayList<>();
    }

    public DefaultAutomatonWithCounterValuesState addState(AcceptingOrExit accepting, int counterValue) {
        DefaultAutomatonWithCounterValuesState state = new DefaultAutomatonWithCounterValuesState(alphabet.size(),
                accepting, counterValue);
        states.add(state);
        return state;
    }

    public DefaultAutomatonWithCounterValuesState addInitialState(AcceptingOrExit accepting, int counterValue) {
        DefaultAutomatonWithCounterValuesState state = addState(accepting, counterValue);
        initialState = state;
        return state;
    }

    public void setSuccessor(DefaultAutomatonWithCounterValuesState start, I input,
            DefaultAutomatonWithCounterValuesState target) {
        start.setTransitionObject(alphabet.getSymbolIndex(input), target);
    }

    public void setTransition(DefaultAutomatonWithCounterValuesState start, I input,
            DefaultAutomatonWithCounterValuesState target) {
        setSuccessor(start, input, target);
    }

    @Override
    public @Nullable DefaultAutomatonWithCounterValuesState getTransition(DefaultAutomatonWithCounterValuesState state,
            I input) {
        return state.getTransitionObject(alphabet.getSymbolIndex(input));
    }

    @Override
    public @Nullable DefaultAutomatonWithCounterValuesState getInitialState() {
        return initialState;
    }

    @Override
    public Collection<DefaultAutomatonWithCounterValuesState> getStates() {
        return Collections.unmodifiableList(states);
    }

    @Override
    public boolean isAccepting(DefaultAutomatonWithCounterValuesState state) {
        return state.isAccepting();
    }

    @Override
    public boolean isExit(DefaultAutomatonWithCounterValuesState state) {
        return state.isExit();
    }

    @Override
    public boolean isRejecting(DefaultAutomatonWithCounterValuesState state) {
        return state.isRejecting();
    }

    @Override
    public int getCounterValue(DefaultAutomatonWithCounterValuesState state) {
        return state.getCounterValue();
    }

    @Override
    public AcceptingOrExit getStateProperty(DefaultAutomatonWithCounterValuesState state) {
        return state.getAcceptance();
    }

    @Override
    public AcceptingOrExit computeStateOutput(DefaultAutomatonWithCounterValuesState state,
            Iterable<? extends I> input) {
        DefaultAutomatonWithCounterValuesState target = getSuccessor(state, input);
        if (target != null) {
            return target.getAcceptance();
        } else {
            return AcceptingOrExit.REJECTING;
        }
    }
}
