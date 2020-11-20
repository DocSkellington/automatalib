package net.automatalib.automata.oca;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import net.automatalib.automata.concepts.SuffixOutput;
import net.automatalib.ts.acceptors.AcceptorTS;

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
    L getLocation(int id);

    /**
     * Returns the ID of the given location
     * 
     * @param id The location
     * @return The corresponding ID
     */
    int getLocationId(L loc);

    @Override
    default Boolean computeOutput(Iterable<? extends I> input) {
        return accepts(input);
    }

    @Override
    default Boolean computeSuffixOutput(Iterable<? extends I> prefix, Iterable<? extends I> suffix) {
        Set<State<L>> states = getStates(Iterables.concat(prefix, suffix));
        return isAccepting(states);
    }

    @Override
    default boolean isAccepting(State<L> state) {
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
    default boolean isAccepting(Collection<? extends State<L>> states) {
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
    boolean isAcceptingLocation(L loc);

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
    public L addLocation(boolean accepting);

    /**
     * Creates a new initial location and adds it in the OCA.
     * 
     * @param accepting Whether the new location is accepting
     * @return The new location
     */
    public L addInitialLocation(boolean accepting);

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
    public void addSuccessor(L start, int counterValue, I input, int counterOperation, L target);

    /**
     * Adds a successor to the state start when reading input. The successor is a
     * pair (counterOperation, target).
     * 
     * @param start            The starting state
     * @param input            The input symbol
     * @param counterOperation The counter operation
     * @param target           The target location
     */
    public default void addSuccessor(State<L> start, I input, int counterOperation, L target) {
        addSuccessor(start.getLocation(), start.getCounterValue(), input, counterOperation, target);
    }

    /**
     * Gives the acceptance mode of the OCA.
     * 
     * @return The acceptance mode
     */
    public AcceptanceMode getAcceptanceMode();
}
