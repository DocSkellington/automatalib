package net.automatalib.automata.oca.automatoncountervalues;

import java.util.Collection;
import java.util.Collections;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.words.Alphabet;

/**
 * A default implementation of an {@link AutomatonWithCounterValues}.
 * 
 * @author Gaëtan Staquet
 */
public class DefaultAutomatonWithCounterValues<I>
        extends AbstractAutomatonWithCounterValues<DefaultAutomatonWithCounterValuesState, I> {

    public DefaultAutomatonWithCounterValues(Alphabet<I> alphabet) {
        super(alphabet);
    }

    @Override
    protected DefaultAutomatonWithCounterValuesState createState(AcceptingOrExit accepting, int counterValue) {
        return new DefaultAutomatonWithCounterValuesState(alphabet.size(), states.size(), accepting, counterValue);
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
    public @Nullable DefaultAutomatonWithCounterValuesState getSuccessor(DefaultAutomatonWithCounterValuesState state,
            I input) {
        // If we are already in an exit point, we stay in the exit point
        if (state.isExit()) {
            return state;
        } else {
            return super.getSuccessor(state, input);
        }
    }

    @Override
    public AcceptingOrExit computeStateOutput(DefaultAutomatonWithCounterValuesState state,
            Iterable<? extends I> input) {
        // If we are already in an exit point, we stay in the exit point
        if (state.isExit()) {
            return AcceptingOrExit.EXIT;
        }

        DefaultAutomatonWithCounterValuesState target = getSuccessor(state, input);
        if (target != null) {
            return target.getAcceptance();
        } else {
            return AcceptingOrExit.REJECTING;
        }
    }

    @Override
    public void setStateAcceptance(DefaultAutomatonWithCounterValuesState state, AcceptingOrExit acceptance) {
        state.setAcceptance(acceptance);
    }
}
