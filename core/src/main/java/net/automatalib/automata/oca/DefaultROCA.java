package net.automatalib.automata.oca;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.automatalib.commons.util.Pair;
import net.automatalib.ts.simple.SimpleDTS;
import net.automatalib.words.Alphabet;
import net.automatalib.words.VPDAlphabet;
import net.automatalib.words.impl.Alphabets;
import net.automatalib.words.impl.DefaultVPDAlphabet;

/**
 * Default implementation for ROCAs with two transitions functions, and without
 * resets.
 * 
 * One function is used when the counter value is equal to zero. The other
 * function is used for all values strictly greater than zero.
 * 
 * @param <I> Input symbol type
 * 
 * @author Gaëtan Staquet
 */
public class DefaultROCA<I> extends AbstractROCA<ROCALocation, I> {

    public DefaultROCA(final Alphabet<I> alphabet) {
        super(alphabet);
    }

    public DefaultROCA(final Alphabet<I> alphabet, final AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
    }

    @Override
    public void setSuccessor(ROCALocation start, int counterValue, I input, int counterOperation, ROCALocation target) {
        int inputIdx = getAlphabet().getSymbolIndex(input);
        TransitionTarget<ROCALocation> transition = new TransitionTarget<>(target, counterOperation);
        start.setSuccessor(counterValue, inputIdx, transition);
    }

    @Override
    public boolean isAcceptingLocation(ROCALocation loc) {
        return loc.isAccepting();
    }

    @Override
    public ROCALocation addLocation(boolean accepting) {
        ROCALocation location = new ROCALocation(getAlphabet().size(), locations.size(), accepting);
        locations.add(location);
        return location;
    }

    @Override
    public Collection<State<ROCALocation>> getTransitions(State<ROCALocation> state, I input) {
        final int symbolId = getAlphabet().getSymbolIndex(input);
        if (state == null || state.getLocation() == null) {
            return Collections.emptySet();
        }
        final TransitionTarget<ROCALocation> transition = state.getLocation().getSuccessor(symbolId,
                state.getCounterValue());
        if (transition == null) {
            return Collections.emptySet();
        }
        final int counterValue = state.getCounterValue() + transition.counterOperation;
        if (counterValue < 0) {
            return Collections.emptySet();
        }
        return SimpleDTS.stateToSet(new State<ROCALocation>(transition.targetLocation, counterValue));
    }

    @Override
    public int getNumberOfTransitionFunctions() {
        return ROCALocation.NUMBER_OF_TRANSITION_FUNCTIONS;
    }
}
