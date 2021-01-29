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

    public DefaultVCA<Pair<I, Integer>> toVCA() {
        Alphabet<Pair<I, Integer>> internalAlphabet = Alphabets
                .fromCollection(alphabet.stream().map(symbol -> Pair.of(symbol, 0)).collect(Collectors.toList()));
        Alphabet<Pair<I, Integer>> callAlphabet = Alphabets
                .fromCollection(alphabet.stream().map(symbol -> Pair.of(symbol, +1)).collect(Collectors.toList()));
        Alphabet<Pair<I, Integer>> returnAlphabet = Alphabets
                .fromCollection(alphabet.stream().map(symbol -> Pair.of(symbol, -1)).collect(Collectors.toList()));
        VPDAlphabet<Pair<I, Integer>> vpdAlphabet = new DefaultVPDAlphabet<>(internalAlphabet, callAlphabet,
                returnAlphabet);

        DefaultVCA<Pair<I, Integer>> vca = new DefaultVCA<Pair<I, Integer>>(1, vpdAlphabet, getAcceptanceMode());

        Map<ROCALocation, VCALocation> roca_to_vca_states = new HashMap<>();
        for (ROCALocation rocaLocation : getLocations()) {
            if (!roca_to_vca_states.containsKey(rocaLocation)) {
                VCALocation vcaLocation;
                if (rocaLocation == getInitialLocation()) {
                    vcaLocation = vca.addInitialLocation(isAcceptingLocation(rocaLocation));
                } else {
                    vcaLocation = vca.addLocation(isAcceptingLocation(rocaLocation));
                }
                roca_to_vca_states.put(rocaLocation, vcaLocation);
            }

            for (int i = 0; i < getNumberOfTransitionFunctions(); i++) {
                State<ROCALocation> rocaStart = new State<>(rocaLocation, i);
                State<VCALocation> vcaStart = new State<>(roca_to_vca_states.get(rocaLocation), i);
                for (I symbol : getAlphabet()) {
                    State<ROCALocation> rocaTarget = getSuccessor(rocaStart, symbol);

                    if (rocaTarget == null) {
                        continue;
                    }

                    if (!roca_to_vca_states.containsKey(rocaTarget.getLocation())) {
                        VCALocation vcaLocation;
                        if (rocaTarget.getLocation() == getInitialLocation()) {
                            vcaLocation = vca.addInitialLocation(isAcceptingLocation(rocaTarget.getLocation()));
                        } else {
                            vcaLocation = vca.addLocation(isAcceptingLocation(rocaTarget.getLocation()));
                        }
                        roca_to_vca_states.put(rocaTarget.getLocation(), vcaLocation);
                    }

                    VCALocation vcaTarget = roca_to_vca_states.get(rocaTarget.getLocation());
                    int counterDifference = rocaTarget.getCounterValue() - rocaStart.getCounterValue();
                    Pair<I, Integer> visiblySymbol = Pair.of(symbol, counterDifference);
                    vca.setSuccessor(vcaStart, visiblySymbol, vcaTarget);
                }
            }
        }

        return vca;
    }
}
