package net.automatalib.util.automata.oca;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.automata.oca.DFAWithCounterValues;
import net.automatalib.automata.oca.DFAWithCounterValuesState;
import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.DefaultVCA;
import net.automatalib.automata.oca.ROCA;
import net.automatalib.automata.oca.State;
import net.automatalib.automata.oca.VCALocation;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 * Utility class revolving around OCAs.
 * 
 * @author Gaëtan Staquet
 */
public class OCAUtil {
    private OCAUtil() {}

    /**
     * Finds a word that distinguishes roca1 and roca2.
     * 
     * That is, it finds a word that is accepted by roca1 but not by roca2, or vice-versa.
     * If the ROCAs are equivalent, it returns null.
     * @param <L1> The location type of the first ROCA
     * @param <L2> The location type of the second ROCA
     * @param <I> The alphabet type
     * @param roca1 The first ROCA
     * @param roca2 The second ROCA
     * @param alphabet The alphabet
     * @return A word that separated both ROCAs, or null if the ROCAs are equivalent.
     */
    public static <L1, L2, I> @Nullable Word<I> findSeparatingWord(final ROCA<L1, I> roca1, final ROCA<L2, I> roca2, final Alphabet<I> alphabet) {
        // The algorithm comes from "Language equivalence of deterministic real-time one-counter automata is NL-complete", S. Böhm, Stefan Göller.

        // The idea is to explore the state space of both ROCAs at once with a parallel BFS
        // If, at some point, we see a pair of non-equivalent states, then we have a separating word
        // If we reach a pair of states where both counter values exceed (|roca1| + |roca2|)^2, we stop the exploration in that direction

        class InQueue {
            public Word<I> word;
            public State<L1> state1;
            public State<L2> state2;

            InQueue(Word<I> word, State<L1> state1, State<L2> state2) {
                this.word = word;
                this.state1 = state1;
                this.state2 = state2;
            }

            @Override
            public int hashCode() {
                return Objects.hash(state1, state2);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (obj == this) {
                    return true;
                }

                if (obj.getClass() != getClass()) {
                    return false;
                }
                InQueue o = (InQueue) obj;
                if ((state1 == null) != (o.state1 == null)) {
                    return false;
                }
                if ((state2 == null) != (o.state2 == null)) {
                    return false;
                }
                if (state1 == null) {
                    if (state2 == null) {
                        return true;
                    }
                    else {
                        return state2.equals(o.state2);
                    }
                }
                else {
                    if (state2 == null) {
                        return state1.equals(o.state1);
                    }
                    else {
                        return state1.equals(o.state1) && state2.equals(o.state2);
                    }
                }
            }
        }
        
        if (roca1.isAccepting(roca1.getInitialState()) != roca2.isAccepting(roca2.getInitialState())) {
            return Word.epsilon();
        }

        final long maxCounterValue = (long)Math.pow(roca1.size() + roca2.size(), 2);
        final List<InQueue> toExplore = new LinkedList<>();
        final Set<InQueue> explored = new HashSet<>();

        toExplore.add(new InQueue(Word.epsilon(), roca1.getInitialState(), roca2.getInitialState()));

        while (toExplore.size() != 0) {
            final InQueue current = toExplore.get(0);
            toExplore.remove(0);
            explored.add(current);

            for (final I a : alphabet) {
                State<L1> target1 = roca1.getTransition(current.state1, a);
                State<L2> target2 = roca2.getTransition(current.state2, a);
                Word<I> newWord = current.word.append(a);
                if (roca1.isAccepting(target1) != roca2.isAccepting(target2)) {
                    return newWord;
                }

                if (target1 == null || target2 == null || target1.getCounterValue() <= maxCounterValue || target2.getCounterValue() <= maxCounterValue) {
                    InQueue next = new InQueue(newWord, target1, target2);
                    if (!toExplore.contains(next) && !explored.contains(next)) {
                        toExplore.add(next);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Tests wether two ROCAs are equivalent.
     * @param <I> The alphabet type
     * @param roca1 The first ROCA
     * @param roca2 The second ROCA
     * @param alphabet The input alphabet
     * @return True iff the two ROCAs are equivalent
     */
    public static <I> boolean testEquivalence(final ROCA<?, I> roca1, final ROCA<?, I> roca2, final Alphabet<I> alphabet) {
        return findSeparatingWord(roca1, roca2, alphabet) == null;
    }

    public static <I> DFAWithCounterValues<I> constructRestrictedAutomaton(final DefaultVCA<I> vca, int maxCounterValue) {
        DFAWithCounterValues<I> dfa = new DFAWithCounterValues<>(vca.getAlphabet());
        Map<State<VCALocation>, DFAWithCounterValuesState> to_dfa_state = new HashMap<>();
        Queue<State<VCALocation>> queue = new LinkedList<>();
        queue.add(new State<>(vca.getInitialLocation(), 0));

        DFAWithCounterValuesState newState = dfa.addInitialState(vca.isAcceptingLocation(vca.getInitialLocation()), 0);
        to_dfa_state.put(new State<>(vca.getInitialLocation(), 0), newState);

        while (queue.size() != 0) {
            State<VCALocation> start = queue.poll();

            for (I a : vca.getAlphabet()) {
                State<VCALocation> target = vca.getTransition(start, a);
                if (target != null && target.getCounterValue() <= maxCounterValue) {
                    if (!to_dfa_state.containsKey(target)) {
                        VCALocation location = target.getLocation();
                        int counterValue = target.getCounterValue();
                        newState = dfa.addState(vca.isAccepting(target), counterValue);
                        to_dfa_state.put(new State<>(location, counterValue), newState);

                        queue.add(target);
                    }
                    DFAWithCounterValuesState start_in_dfa = to_dfa_state.get(start);
                    DFAWithCounterValuesState target_in_dfa = to_dfa_state.get(target);
                    dfa.setTransition(start_in_dfa, a, target_in_dfa);

                }
            }
        }

        return dfa;
    }

    /**
     * Constructs a DFA with states annotated with counter values, up to the given maximal counter value.
     * 
     * @param <I> Input alphabet type
     * @param roca The ROCA
     * @param maxCounterValue The maximum counter value
     * @return A DFA where the states are annotated with counter values.
     */
    public static <I> DFAWithCounterValues<I> constructRestrictedAutomaton(final DefaultROCA<I> roca, int maxCounterValue) {
        // We construct a VCA from this ROCA
        // We naturally need to transform the alphabet I into Pair<I, Integer>
        DefaultVCA<Pair<I, Integer>> vca = roca.toVCA();
        // We construct the behavior graph of the VCA, up to a counter value
        DFAWithCounterValues<Pair<I, Integer>> bg_b = constructRestrictedAutomaton(vca, maxCounterValue);

        // Finally, we construct a DFA from bg_bg by re-transforming the alphabet into I
        // We will obtain a DFA that is easy-to-learn as each state is annotated with a counter value
        DFAWithCounterValues<I> automaton = new DFAWithCounterValues<>(roca.getAlphabet());

        Map<DFAWithCounterValuesState, DFAWithCounterValuesState> bg_b_to_automaton_states = new HashMap<>();
        Queue<DFAWithCounterValuesState> queue = new LinkedList<>();
        queue.add(bg_b.getInitialState());
        
        DFAWithCounterValuesState initialInBG = bg_b.getInitialState();
        DFAWithCounterValuesState initialInAutomaton = automaton.addInitialState(bg_b.isAccepting(initialInBG), bg_b.getCounterValue(initialInBG));
        bg_b_to_automaton_states.put(initialInBG, initialInAutomaton);

        while (queue.size() != 0) {
            DFAWithCounterValuesState start = queue.poll();

            for (Pair<I, Integer> a : bg_b.getInputAlphabet()) {
                DFAWithCounterValuesState target = bg_b.getTransition(start, a);
                if (target != null) {
                    if (!bg_b_to_automaton_states.containsKey(target)) {
                        int counterValue = target.getCounterValue();
                        DFAWithCounterValuesState newState = automaton.addState(bg_b.isAccepting(target), counterValue);
                        bg_b_to_automaton_states.put(target, newState);

                        queue.add(target);
                    }
                    DFAWithCounterValuesState start_in_dfa = bg_b_to_automaton_states.get(start);
                    DFAWithCounterValuesState target_in_dfa = bg_b_to_automaton_states.get(target);
                    I symbol = a.getFirst();
                    automaton.setTransition(start_in_dfa, symbol, target_in_dfa);
                }
            }
        }

        return automaton;
    }
}
