package net.automatalib.automata.oca.automatoncountervalues;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.ROCA;
import net.automatalib.automata.oca.ROCALocation;
import net.automatalib.automata.oca.State;
import net.automatalib.words.Alphabet;

/**
 * A concrete implementation of a {@link PeriodicDescription}.
 * 
 * See the interface's documentation for more information.
 * 
 * @author Gaëtan Staquet
 */
final class DefaultPeriodicDescription<I> implements PeriodicDescription<I> {

    private final int width;
    private final int offset;
    private final int period;
    private final Alphabet<I> alphabet;
    private State<Integer> initialState;
    private final List<BitSet> accepting;
    private final List<List<Map<I, State<Integer>>>> transitions;

    public DefaultPeriodicDescription(int width, int offset, int period, Alphabet<I> alphabet) {
        this.width = width;
        this.offset = offset;
        this.period = period;
        this.alphabet = alphabet;

        this.transitions = new ArrayList<>(offset + period);
        this.accepting = new ArrayList<>(offset + period);
        for (int i = 0; i < offset + period; i++) {
            this.accepting.add(new BitSet(width));

            this.transitions.add(new ArrayList<>(width));
            for (int j = 0; j < width; j++) {
                this.transitions.get(i).add(new HashMap<>());
            }
        }
    }

    @Override
    public void markAccepting(int state, int counterValue) {
        accepting.get(counterValue).set(state, true);
    }

    @Override
    public void setTransition(int start, int startCounterValue, I symbol, int target, int targetCounterValue) {
        getMapping(start, startCounterValue).put(symbol, new State<>(target, targetCounterValue));
    }

    @Override
    public State<Integer> getTransition(int state, int counterValue, I symbol) {
        if (0 <= counterValue && counterValue < offset + period) {
            return getMapping(state, counterValue).get(symbol);
        } else {
            return null;
        }
    }

    @Override
    public ROCA<?, I> toROCA() {
        DefaultROCA<I> roca = new DefaultROCA<>(alphabet);

        // First, we create the set of states
        // The set of states is defined as {1, ...., K} x {0, 1, ..., m + k - 1}, with K
        // the width of the description, m the offset, and k the period
        List<List<ROCALocation>> locations = new ArrayList<>(width);
        for (int state = 0; state < width; state++) {
            locations.add(new ArrayList<>(offset + width));
            for (int counterValue = 0; counterValue < offset + period; counterValue++) {
                ROCALocation loc;
                if (counterValue == initialState.getCounterValue() && state == initialState.getLocation()) {
                    loc = roca.addInitialLocation(isAccepting(state, counterValue));
                } else {
                    loc = roca.addLocation(isAccepting(state, counterValue));
                }
                locations.get(state).add(loc);
            }
        }

        // We copy the "easy" transitions from the description.
        // That is, as long as we are not at a boundary of the periodic part (i.e.,
        // offset or offset + period - 1), we simply copy the transitions.
        for (int counterValue = 0; counterValue < offset + period - 1; counterValue++) {
            for (int state = 0; state < width; state++) {
                ROCALocation startLocation = locations.get(state).get(counterValue);
                for (I symbol : alphabet) {
                    State<Integer> target = getTransition(state, counterValue, symbol);
                    if (target != null) {
                        ROCALocation targetLocation = locations.get(target.getLocation()).get(target.getCounterValue());
                        roca.addSuccessor(startLocation, 0, symbol, 0, targetLocation);

                        if (offset < counterValue && counterValue < offset + period - 1) {
                            roca.addSuccessor(startLocation, 1, symbol, 0, targetLocation);
                        }
                    }
                }
            }
        }

        if (period != 0) { // If the period is zero, see below
            for (int state = 0; state < width; state++) {
                // We define the transitions when we are at states with counter value "offset".
                // In this case, we must pay attention to decrement the counter value when going
                // back to "offset + period - 1".
                // We must also pay attention to the corner cases induced by having a period
                // equal to one. In that case, we need to define more transitions and make sure
                // the automaton remains deterministic.
                ROCALocation startLocation = locations.get(state).get(offset);
                for (I symbol : alphabet) {
                    State<Integer> target = getTransition(state, offset, symbol);
                    if (target != null && 0 <= target.getCounterValue() && target.getCounterValue() < offset + period) {
                        if (target.getCounterValue() == offset - 1) {
                            ROCALocation targetLocation = locations.get(target.getLocation()).get(offset + period - 1);
                            roca.addSuccessor(startLocation, 1, symbol, -1, targetLocation);
                            if (period == 1) {
                                targetLocation = locations.get(target.getLocation()).get(offset - 1);
                                roca.addSuccessor(startLocation, 0, symbol, 0, targetLocation);
                            }
                        } else if (period != 1 || target.getCounterValue() != offset) {
                            ROCALocation targetLocation = locations.get(target.getLocation())
                                    .get(target.getCounterValue());
                            roca.addSuccessor(startLocation, 1, symbol, 0, targetLocation);
                        }
                    }
                }

                // Finally, we do something similar to the previous part but when the counter
                // value is "offset + period - 1".
                // We must then pay attention to increment the counter when going back to
                // "offset".
                // Again, if the period is one, we only define transitions if needed (i.e., such
                // that the ROCA is deterministic).
                startLocation = locations.get(state).get(offset + period - 1);
                for (I symbol : alphabet) {
                    State<Integer> target = getTransition(state, offset + period - 1, symbol);
                    if (target != null && 0 <= target.getCounterValue()
                            && target.getCounterValue() <= offset + period) {
                        if (target.getCounterValue() == offset + period) {
                            ROCALocation targetLocation = locations.get(target.getLocation()).get(offset);
                            roca.addSuccessor(startLocation, 0, symbol, +1, targetLocation);
                            roca.addSuccessor(startLocation, 1, symbol, +1, targetLocation);
                        } else if (period != 1 || target.getCounterValue() != offset - 1) {
                            ROCALocation targetLocation = locations.get(target.getLocation())
                                    .get(target.getCounterValue());
                            roca.addSuccessor(startLocation, 0, symbol, 0, targetLocation);
                            roca.addSuccessor(startLocation, 1, symbol, 0, targetLocation);
                        }
                    }
                }
            }
        } else { // period = 0
                 // In this case, we never change the counter value.
                 // But we still have to defined the transitions in offset + period - 1 = offset
                 // - 1
            for (int state = 0; state < width; state++) {
                ROCALocation startLocation = locations.get(state).get(offset - 1);
                for (I symbol : alphabet) {
                    State<Integer> target = getTransition(state, offset - 1, symbol);
                    if (target != null && 0 <= target.getCounterValue() && target.getCounterValue() <= offset - 1) {
                        ROCALocation targetLocation = locations.get(target.getLocation()).get(target.getCounterValue());
                        roca.addSuccessor(startLocation, 0, symbol, 0, targetLocation);
                    }
                }
            }
        }

        return roca;
    }

    public void setInitialState(int initialState, int counterValue) {
        this.initialState = new State<>(initialState, counterValue);
    }

    private Map<I, State<Integer>> getMapping(int state, int counterValue) {
        return transitions.get(counterValue).get(state);
    }

    private boolean isAccepting(int state, int counterValue) {
        if (0 <= counterValue && counterValue < offset + period) {
            return accepting.get(counterValue).get(state);
        } else {
            return false;
        }
    }
}
