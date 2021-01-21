package net.automatalib.automata.oca;

import java.util.Collection;
import java.util.Collections;

import net.automatalib.ts.simple.SimpleDTS;
import net.automatalib.words.Alphabet;

/**
 * Default implementation for OCAs with two transitions functions.
 * 
 * One function is used when the counter value is equal to zero. The other
 * function is used for all values strictly greater than zero.
 * 
 * @param <I> Input symbol type
 * 
 * @author Gaëtan Staquet
 */
public class DefaultDOCA<I> extends AbstractDOCA<DOCALocation, I> {

    public DefaultDOCA(final Alphabet<I> alphabet) {
        super(alphabet);
    }

    public DefaultDOCA(final Alphabet<I> alphabet, final AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
    }

    @Override
    public void setSuccessor(final DOCALocation start, final int counterValue, final I input,
            final int counterOperation, final DOCALocation target) {
        start.setSuccessor(counterValue, getAlphabet().getSymbolIndex(input),
                new TransitionTarget<DOCALocation>(target, counterOperation));
    }

    @Override
    public Collection<State<DOCALocation>> getEpsilonTransitions(final State<DOCALocation> state) {
        final TransitionTarget<DOCALocation> transition = state.getLocation()
                .getEpsilonSuccessor(state.getCounterValue());
        if (transition == null) {
            return Collections.emptySet();
        }
        final int counterValue = state.getCounterValue() + transition.counterOperation;
        if (counterValue < 0) {
            return Collections.emptySet();
        }
        return SimpleDTS.stateToSet(new State<DOCALocation>(transition.targetLocation, counterValue));
    }

    @Override
    public boolean isAcceptingLocation(DOCALocation loc) {
        return loc.isAccepting();
    }

    @Override
    public DOCALocation addLocation(boolean accepting) {
        final DOCALocation location = new DOCALocation(getAlphabet().size(), locations.size(), accepting);
        locations.add(location);
        return location;
    }

    @Override
    public Collection<State<DOCALocation>> getTransitions(State<DOCALocation> state, I input) {
        final int symbolId = getAlphabet().getSymbolIndex(input);
        final TransitionTarget<DOCALocation> transition = state.getLocation().getSuccessor(symbolId,
                state.getCounterValue());
        if (transition == null) {
            return Collections.emptySet();
        }
        final int counterValue = state.getCounterValue() + transition.counterOperation;
        if (counterValue < 0) {
            return Collections.emptySet();
        }
        return SimpleDTS.stateToSet(new State<DOCALocation>(transition.targetLocation, counterValue));
    }

    public void setEpsilonSuccessor(DOCALocation start, int counterValue, int counterOperation, DOCALocation target) {
        start.setEpsilonSuccessor(counterValue, new TransitionTarget<DOCALocation>(target, counterOperation));
    }

    @Override
    public int getNumberOfTransitionFunctions() {
        return DOCALocation.NUMBER_OF_TRANSITION_FUNCTIONS;
    }
}
