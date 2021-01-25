package net.automatalib.automata.oca;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import net.automatalib.automata.concepts.SuffixOutput;
import net.automatalib.ts.acceptors.AcceptorTS;
import net.automatalib.words.Alphabet;

/**
 * Interface for the OCA (one-counter automaton), a finite-state automaton
 * augmented with a counter.
 * 
 * OCAs can also be seen as push-down automata with bottom-testing and a single
 * stack symbol.
 * <p>
 * Due to the counter value, we distinguish locations from states. A location is
 * a node in the automaton, while a state (sometimes called configuration) is a
 * pair (location, counter value).
 * <p>
 * OCAs can test their counter values up to a certain threshold, which is
 * determined by the semantics. All values strictly higher than that threshold
 * can not be distinguished. Typically, one can test if the counter value is
 * equal to zero or strictly greater than zero.
 * <p>
 * Transitions are in (Q x N) x S x (Q x N), with Q the set of states, N the
 * natural set, and S the alphabet. The first (Q x N) designates the starting
 * state, while the second designates the target location and the counter
 * operation to be applied. Usually, allowed counter operations are -1, 0, and
 * +1. OCAs allow epsilon-transitions.
 * <p>
 * The set of initial states is the set {(q, 0) : q is an initial location}.
 * <p>
 * Important: if the OCA has a loop that increases the counter and with only
 * epsilon-transitions, then checking whether a word is accepted will infinitely
 * run.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public interface OCA<L, I> extends AcceptorTS<State<L>, I>, SuffixOutput<I, Boolean> {
    /**
     * Returns the location with the given ID
     * 
     * @param id The ID of the location
     * @return The corresponding location
     */
    L getLocation(final int id);

    /**
     * Returns the ID of the given location
     * 
     * @param id The location
     * @return The corresponding ID
     */
    int getLocationId(final L loc);

    @Override
    default Boolean computeOutput(final Iterable<? extends I> input) {
        return accepts(input);
    }

    @Override
    default Boolean computeSuffixOutput(final Iterable<? extends I> prefix, final Iterable<? extends I> suffix) {
        Set<State<L>> states = getStates(Iterables.concat(prefix, suffix));
        return isAccepting(states);
    }

    @Override
    default Set<State<L>> getSuccessors(final Collection<? extends State<L>> states,
            final Iterable<? extends I> input) {
        Set<State<L>> current = new HashSet<State<L>>(states);
        Set<State<L>> successors = new HashSet<>();

        for (I sym : input) {
            // We follow sym-transitions, as usual
            Set<State<L>> newSuccessors = new HashSet<>();
            for (State<L> state : current) {
                Set<State<L>> succ = getSuccessors(state, sym);
                newSuccessors.addAll(succ);
                successors.addAll(succ);
            }

            // We follow as much epsilon-transitions as possible
            // Warning: this may loop infinitely if there is an epsilon-loop that modifies
            // the counter value
            // However, we can not prevent this for OCAs
            boolean change = true;
            while (change) {
                for (State<L> state : successors) {
                    Set<State<L>> epsSucc = getEpsilonSuccessors(state);
                    if (epsSucc != null) {
                        newSuccessors.addAll(epsSucc);
                    }
                }

                change = successors.addAll(newSuccessors);
                newSuccessors.clear();
            }

            // We update the current set of states
            Set<State<L>> tmp = current;
            current = successors;
            successors = tmp;
            successors.clear();
        }

        return current;
    }

    /**
     * Gets the epsilon-successors of a state.
     * 
     * @param state The starting state
     * @return The set of target states
     */
    default Set<State<L>> getEpsilonSuccessors(final State<L> state) {
        Collection<State<L>> transitions = getEpsilonTransitions(state);
        if (transitions.size() == 0) {
            return Collections.emptySet();
        }

        Set<State<L>> result = new HashSet<>(transitions.size());
        for (State<L> transition : transitions) {
            result.add(getSuccessor(transition));
        }
        return result;
    }

    /**
     * Gets the epsilon-transitions from a given state.
     * 
     * That is, these are the transitions that do not consume any input symbols
     * 
     * @param state The starting state
     * @return A collection of transitions
     */
    Collection<State<L>> getEpsilonTransitions(final State<L> state);

    @Override
    default boolean isAccepting(final State<L> state) {
        if (state == null) {
            return false;
        }
        final L location = state.getLocation();
        final int cv = state.getCounterValue();
        switch (getAcceptanceMode()) {
            case BOTH:
                return location != null && isAcceptingLocation(location) && cv == 0;
            case COUNTER_ZERO:
                return location != null && cv == 0;
            case ACCEPTING_LOCATION:
                return location != null && isAcceptingLocation(location);
            default:
                return false;
        }
    }

    @Override
    default boolean isAccepting(final Collection<? extends State<L>> states) {
        for (State<L> state : states) {
            if (isAccepting(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the given location is accepting
     * 
     * @param loc The location
     * @return Whether the given location is accepting
     */
    boolean isAcceptingLocation(final L loc);

    @Override
    default Set<State<L>> getInitialStates() {
        return getInitialLocations().stream().map(s -> new State<L>(s, 0)).collect(Collectors.toSet());
    }

    /**
     * Returns the set of initial locations
     * 
     * @return The set of initial locations
     */
    Set<L> getInitialLocations();

    /**
     * Creates a new location and adds it in the OCA.
     * 
     * @param accepting Whether the new location is accepting
     * @return The new location
     */
    public L addLocation(final boolean accepting);

    /**
     * Creates a new initial location and adds it in the OCA.
     * 
     * @param accepting Whether the new location is accepting
     * @return The new location
     */
    public L addInitialLocation(final boolean accepting);

    /**
     * Adds a successor to the state (start, counterValue) when reading input. The
     * successor is a pair (counterOperation, target).
     * 
     * @param start            The starting location
     * @param counterValue     The counter value test
     * @param input            The input symbol
     * @param counterOperation The counter operation
     * @param target           The target location
     */
    public void addSuccessor(final L start, final int counterValue, final I input, final int counterOperation,
            final L target);

    /**
     * Adds a successor to the state start when reading input. The successor is a
     * pair (counterOperation, target).
     * 
     * @param start            The starting state
     * @param input            The input symbol
     * @param counterOperation The counter operation
     * @param target           The target location
     */
    public default void addSuccessor(final State<L> start, final I input, final int counterOperation, final L target) {
        addSuccessor(start.getLocation(), start.getCounterValue(), input, counterOperation, target);
    }

    /**
     * Gives the acceptance mode of the OCA.
     * 
     * @return The acceptance mode
     */
    public AcceptanceMode getAcceptanceMode();

    /**
     * Gives the number of transition functions used by this OCA.
     * 
     * @return The number of transition functions
     */
    public int getNumberOfTransitionFunctions();

    /**
     * Gives the number of locations in the OCA.
     * 
     * @return The number of locations
     */
    public int size();

    public List<L> getLocations();

    public Alphabet<I> getAlphabet();
}
