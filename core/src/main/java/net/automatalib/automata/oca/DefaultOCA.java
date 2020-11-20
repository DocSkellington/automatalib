package net.automatalib.automata.oca;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
public class DefaultOCA<I> extends AbstractOCA<OCALocation, I> {

    DefaultOCA(final Alphabet<I> alphabet) {
        super(alphabet);
    }

    DefaultOCA(final Alphabet<I> alphabet, AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
    }

    @Override
    public boolean isAcceptingLocation(OCALocation loc) {
        return loc.isAccepting();
    }

    @Override
    public Collection<State<OCALocation>> getTransitions(State<OCALocation> state, I input) {
        int inputIdx = getAlphabet().getSymbolIndex(input);
        final Collection<TransitionTarget<OCALocation>> transitions = state.getLocation().getSuccessors(inputIdx,
                state.getCounterValue());
        Set<State<OCALocation>> states = new HashSet<>();
        for (TransitionTarget<OCALocation> transition : transitions) {
            final int newCounterValue = state.getCounterValue() + transition.counterOperation;
            if (newCounterValue >= 0) {
                states.add(new State<OCALocation>(transition.targetLocation, newCounterValue));
            }
        }
        return states;
    }

    @Override
    public OCALocation addLocation(boolean accepting) {
        OCALocation location = new OCALocation(alphabet.size(), accepting);
        locations.add(location);
        return location;
    }

    @Override
    public void addSuccessor(OCALocation start, int counterValue, I input, int counterOperation, OCALocation target) {
        if (counterValue + counterOperation < 0) {
            throw new InvalidParameterException("The counter value of an OCA must always stay at least 0");
        }
        start.addSuccessor(counterValue, alphabet.getSymbolIndex(input),
                new TransitionTarget<OCALocation>(target, counterOperation));
    }

}
