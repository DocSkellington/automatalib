package net.automatalib.automata.oca.automatoncountervalues;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.ROCA;
import net.automatalib.automata.oca.ROCALocation;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;

/**
 * Abstract class for automata where states are annotated with counter values.
 * 
 * See {@link DefaultAutomatonWithCounterValues} for a concrete type.
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractAutomatonWithCounterValues<S, I> implements AutomatonWithCounterValues<S, I> {
    protected final Alphabet<I> alphabet;

    protected final List<S> states;
    protected S initialState;
    protected final Map<Integer, List<S>> statesByCounterValue;

    public AbstractAutomatonWithCounterValues(Alphabet<I> alphabet) {
        this.alphabet = alphabet;
        this.states = new ArrayList<>();
        this.statesByCounterValue = new HashMap<>();
    }

    @Override
    public Alphabet<I> getInputAlphabet() {
        return alphabet;
    }

    @Override
    public ROCA<?, I> asROCA() {
        DefaultROCA<I> roca = new DefaultROCA<>(alphabet);

        Map<S, ROCALocation> dfaToRoca = new HashMap<>();
        for (S state : getStates()) {
            ROCALocation location;
            if (state.equals(getInitialState())) {
                location = roca.addInitialLocation(isAccepting(state));
            } else {
                location = roca.addLocation(isAccepting(state));
            }
            dfaToRoca.put(state, location);
        }

        for (S start : getStates()) {
            ROCALocation startLocation = dfaToRoca.get(start);
            for (I symbol : alphabet) {
                S target = getTransition(start, symbol);
                ROCALocation targetLocation = dfaToRoca.get(target);

                roca.setSuccessor(startLocation, 0, symbol, 0, targetLocation);
            }
        }

        return roca;
    }

    public S addState(AcceptingOrExit accepting, int counterValue) {
        S state = createState(accepting, counterValue);
        states.add(state);
        if (!statesByCounterValue.containsKey(counterValue)) {
            statesByCounterValue.put(counterValue, new ArrayList<>());
        }
        statesByCounterValue.get(counterValue).add(state);
        return state;
    }

    public S addInitialState(AcceptingOrExit accepting, int counterValue) {
        S state = addState(accepting, counterValue);
        initialState = state;
        return state;
    }

    @Override
    public int getWidth() {
        return statesByCounterValue.values().stream().map(list -> list.size()).max(Comparator.naturalOrder()).get();
    }

    /**
     * Gets the width of the automaton, if we restrict the counter value in [start,
     * end] (both endpoints are included)
     * 
     * @param start The first counter value
     * @param end   The last counter value
     * @return The width of the subautomaton.
     */
    protected int getWidth(int start, int end) {
        int width = 0;
        for (int i = start; i <= end; i++) {
            width = Math.max(width, statesByCounterValue.get(i).size());
        }
        return width;
    }

    protected abstract S createState(AcceptingOrExit accepting, int counterValue);

    @Override
    public List<ROCA<?, I>> toROCAs(int counterLimit) {
        List<ROCA<?, I>> rocas = new ArrayList<>();

        for (int offset = 0; offset <= counterLimit; offset++) {
            for (int period = 0; offset + 2 * period - 1 <= counterLimit; period++) {
                if (offset == 0 && period == 0) {
                    continue;
                }

                // We only construct the description if the subgraphs induced by the counter
                // values in [offset, offset + period - 1] and in [offset + period, offset + 2 *
                // period - 1] are isomorphic
                if (period > 0 && !areIsomorphic(offset, period)) {
                    continue;
                }

                DefaultPeriodicDescription<I> periodicDescription = new DefaultPeriodicDescription<>(
                        getWidth(0, offset + period - 1), offset, period, alphabet);

                S initialState = getInitialState();
                int initialCV = getCounterValue(initialState);
                int initialIndex = statesByCounterValue.get(initialCV).indexOf(initialState);
                periodicDescription.setInitialState(initialIndex, initialCV);

                for (int counterValue = 0; counterValue <= offset + period - 1; counterValue++) {
                    for (S state : statesByCounterValue.get(counterValue)) {
                        int startCV = getCounterValue(state);
                        int startIndex = statesByCounterValue.get(startCV).indexOf(state);

                        if (isAccepting(state)) {
                            periodicDescription.markAccepting(startIndex, startCV);
                        }

                        for (I symbol : alphabet) {
                            S target = getTransition(state, symbol);
                            if (target != null) {
                                int targetCV = getCounterValue(target);
                                if (0 <= targetCV && targetCV <= offset + period) { // We only copy interesting
                                                                                    // transitions
                                    int targetIndex = statesByCounterValue.get(targetCV).indexOf(target);
                                    periodicDescription.setTransition(startIndex, startCV, symbol, targetIndex,
                                            targetCV);
                                }
                            }
                        }
                    }
                }

                ROCA<?, I> roca = periodicDescription.toROCA();
                if (roca != null) {
                    rocas.add(roca);
                }
            }
        }

        return rocas;
    }

    /**
     * Tests whether the subautomata induced by the counter values [offset, offset +
     * period - 1] and [offset + period, offset + 2 * period - 1] are isomorphic.
     * 
     * @param offset The offset
     * @param period The period
     * @return True iff the subautomata are isomorphic
     */
    protected boolean areIsomorphic(int offset, int period) {
        int first = offset;
        int second = offset + period;
        if (statesByCounterValue.get(first).size() != statesByCounterValue.get(second).size()) {
            return false;
        }

        Map<Integer, Map<S, Integer>> traversalNumbers = new HashMap<>(2 * (second - first));
        for (int i = first; i < first + 2 * (second - first); i++) {
            traversalNumbers.put(i, new HashMap<>());
        }

        for (int i = 0; i < statesByCounterValue.get(first).size(); i++) {
            S stateFirst = statesByCounterValue.get(first).get(i);
            if (traversalNumbers.get(first).containsKey(stateFirst)) {
                continue;
            }

            boolean isomorphismFound = false;
            for (int j = 0; j < statesByCounterValue.get(second).size(); j++) {
                S stateSecond = statesByCounterValue.get(second).get(j);
                if (traversalNumbers.get(second).containsKey(stateSecond)) {
                    continue;
                }

                if (parallelBFS(offset, period, traversalNumbers, stateFirst, stateSecond)) {
                    isomorphismFound = true;
                    break;
                }
            }

            if (!isomorphismFound) {
                return false;
            }
        }

        return true;
    }

    /**
     * Executes two synchronized breadth-first searches from startFirst and
     * startSecond in order to test whether the explored subgraphs defined by the
     * periodic description we are trying to build are isomorphic.
     * 
     * The searches are synchronized in such a way that the same transitions are
     * seen at the same time. To do so, each state is marked with a traversal number
     * the first it is seen. The algorithm then checks whether both explorations see
     * the same traversal numbers at the same time.
     * 
     * See Learning Visibly One-Counter Automata with Polynomial Time, D. Neider and
     * Christof Löding, unpublished paper.
     * 
     * @param offset           The offset of the description
     * @param period           The period of the description
     * @param traversalNumbers The already assigned traversal numbers. This map is
     *                         modified.
     * @param startFirst       The starting state with counter value offset
     * @param startSecond      The starting state with counter value offset + period
     * @return Whether both BFS saw the same traversal numbers at the same time.
     */
    protected boolean parallelBFS(int offset, int period, Map<Integer, Map<S, Integer>> traversalNumbers, S startFirst,
            S startSecond) {
        int startFirstCV = getCounterValue(startFirst);
        int startSecondCV = getCounterValue(startSecond);
        int cvDifference = startSecondCV - startFirstCV;
        Queue<Pair<S, S>> queue = new LinkedList<>();
        queue.add(Pair.of(startFirst, startSecond));

        while (queue.size() != 0) {
            Pair<S, S> pair = queue.poll();
            S inFirst = pair.getFirst();
            S inSecond = pair.getSecond();
            int cvFirst = getCounterValue(inFirst);
            int cvSecond = getCounterValue(inSecond);

            traversalNumbers.get(cvFirst).put(inFirst, traversalNumbers.get(cvFirst).size());
            traversalNumbers.get(cvSecond).put(inSecond, traversalNumbers.get(cvSecond).size());

            for (I symbol : alphabet) {
                S targetFirst = getTransition(inFirst, symbol);
                S targetSecond = getTransition(inSecond, symbol);
                // One of the transition leads to a bin state but not the other
                if ((targetFirst == null) != (targetSecond == null)) {
                    return false;
                }
                // If we end up in a bin state, we stop the search in that direction
                if (targetFirst == null) {
                    continue;
                }
                // So, we are sure both states are not null

                int targetFirstCV = getCounterValue(targetFirst);
                int targetSecondCV = getCounterValue(targetSecond);
                // We should always keep cvDifference between the counter values
                if (targetSecondCV - targetFirstCV != cvDifference) {
                    return false;
                }
                // We ignore the states outside of the subgraphs
                if (!(startFirstCV <= targetFirstCV && targetFirstCV < startFirstCV + cvDifference)) {
                    continue;
                }

                boolean visitedFirst = traversalNumbers.get(targetFirstCV).containsKey(targetFirst);
                boolean visitedSecond = traversalNumbers.get(targetSecondCV).containsKey(targetSecond);
                // One of the targets has already been visited but not the other
                if (visitedFirst != visitedSecond) {
                    return false;
                }
                // If both have already been visited, they must have the same traversal number
                if (visitedFirst) {
                    int traversalFirst = traversalNumbers.get(targetFirstCV).get(targetFirst);
                    int traversalSecond = traversalNumbers.get(targetSecondCV).get(targetSecond);
                    if (traversalFirst != traversalSecond) {
                        return false;
                    }

                }
                // If none were visited, we store the traversal number and add the states in the
                // queue
                else {
                    int traversalFirst = traversalNumbers.get(targetFirstCV).size();
                    int traversalSecond = traversalNumbers.get(targetSecondCV).size();
                    if (traversalFirst != traversalSecond) {
                        return false;
                    }

                    traversalNumbers.get(targetFirstCV).put(targetFirst, traversalFirst);
                    traversalNumbers.get(targetSecondCV).put(targetSecond, traversalFirst);
                    queue.add(Pair.of(targetFirst, targetSecond));
                }
            }
        }

        return true;
    }

    @Override
    public DFA<?, I> toSimpleDFA() {
        CompactDFA<I> dfa = new CompactDFA<>(alphabet);

        Map<S, Integer> stateToDFA = new HashMap<>();
        for (S state : getStates()) {
            boolean accepting = isAccepting(state);
            boolean initial = (state == initialState);
            Integer newState;
            if (initial) {
                newState = dfa.addInitialState(accepting);
            }
            else {
                newState = dfa.addState(accepting);
            }
            stateToDFA.put(state, newState);
        }

        for (S state : getStates()) {
            Integer start = stateToDFA.get(state);

            for (I input : alphabet) {
                Integer target = stateToDFA.get(getTransition(state, input));
                dfa.setTransition(start, input, target);
            }
        }

        return dfa;
    }
}
