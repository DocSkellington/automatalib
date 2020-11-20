package net.automatalib.automata.oca;

import java.security.InvalidParameterException;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.ts.simple.SimpleDTS;
import net.automatalib.words.VPDAlphabet;

/**
 * Default implementation for VCAs.
 * 
 * The number of transition functions is fixed at the creation of the automaton.
 * 
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public class DefaultVCA<I> extends AbstractVCA<VCALocation, I> {

    public DefaultVCA(final int m, final VPDAlphabet<I> alphabet) {
        super(m, alphabet);
    }

    public DefaultVCA(final int m, final VPDAlphabet<I> alphabet, AcceptanceMode acceptanceMode) {
        super(m, alphabet, acceptanceMode);
    }

    @Override
    public boolean isAcceptingLocation(VCALocation loc) {
        return loc.isAccepting();
    }

    @Override
    public Collection<State<VCALocation>> getTransitions(State<VCALocation> state, I input) {
        return SimpleDTS.stateToSet(getTransition(state, input));
    }

    @Override
    public @Nullable State<VCALocation> getTransition(State<VCALocation> state, I input) {
        int idx = getAlphabet().getSymbolIndex(input);
        TransitionTarget<VCALocation> transition = state.getLocation().getSuccessor(idx, state.getCounterValue());
        if (transition == null || state.getCounterValue() + transition.counterOperation < 0) {
            return null;
        }
        return new State<VCALocation>(transition.targetLocation, state.getCounterValue() + transition.counterOperation);
    }

    @Override
    public void setSuccessor(VCALocation start, int counterValue, I input, VCALocation target) {
        switch (alphabet.getSymbolType(input)) {
            case CALL:
                setCallSuccessor(start, counterValue, input, target);
                break;
            case INTERNAL:
                setInternalSuccessor(start, counterValue, input, target);
                break;
            case RETURN:
                setReturnSuccessor(start, counterValue, input, target);
                break;
            default:
                break;
        }
    }

    @Override
    public void setCallSuccessor(VCALocation start, int counterValue, I input, VCALocation target) {
        start.setSuccessor(counterValue, alphabet.getSymbolIndex(input), new TransitionTarget<VCALocation>(target, +1));
    }

    @Override
    public void setReturnSuccessor(VCALocation start, int counterValue, I input, VCALocation target) {
        // m == 0 means that we have only 1 transition function delta_>=0
        if (m != 0 && counterValue == 0) {
            throw new InvalidParameterException(
                    "Impossible to add a transition to the VCA as it may lead to a negative counter value");
        }

        start.setSuccessor(counterValue, alphabet.getSymbolIndex(input), new TransitionTarget<VCALocation>(target, -1));
    }

    @Override
    public void setInternalSuccessor(VCALocation start, int counterValue, I input, VCALocation target) {
        start.setSuccessor(counterValue, alphabet.getSymbolIndex(input), new TransitionTarget<VCALocation>(target, 0));
    }

    @Override
    public VCALocation addLocation(boolean accepting) {
        VCALocation location = new VCALocation(m + 1, alphabet.size(), accepting);
        locations.add(location);
        return location;
    }
}
