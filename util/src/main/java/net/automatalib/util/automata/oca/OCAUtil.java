package net.automatalib.util.automata.oca;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.automata.oca.ROCA;
import net.automatalib.automata.oca.State;
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
}
