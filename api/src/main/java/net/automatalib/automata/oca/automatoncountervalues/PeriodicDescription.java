package net.automatalib.automata.oca.automatoncountervalues;

import net.automatalib.automata.oca.ROCA;
import net.automatalib.automata.oca.State;

/**
 * Interface for an ultimately periodic description of an
 * {@link AutomatonWithCounterValues}
 * 
 * To build a periodic description, the states are grouped and enumerated by
 * counter values. The transitions are then defined naturally by copying the
 * transitions from the automaton while applying the enumeration. That is, a
 * periodic description is an abstraction of an
 * {@link AutomatonWithCounterValues} from which it is easy to construct an ROCA
 * accepting the same language than the automaton.
 * 
 * @param I Input alphabet type
 * @author Gaëtan Staquet
 */
interface PeriodicDescription<I> {
    /**
     * Marks a pair (state's index, counter value) as accepting
     * 
     * @param state        The state's index
     * @param counterValue The counter value
     */
    public void markAccepting(int state, int counterValue);

    /**
     * Defines the transition from the pair (start, start counter value) to the pair
     * (target, target counter value) if symbol is read.
     * 
     * @param start              The starting state's index
     * @param startCounterValue  The starting counter value
     * @param symbol             The symbol
     * @param target             The target state's index
     * @param targetCounterValue The target counter value
     */
    public void setTransition(int start, int startCounterValue, I symbol, int target, int targetCounterValue);

    /**
     * Gets the transition from the pair (state, counter value) for the given
     * symbol.
     * 
     * @param state        The state's index
     * @param counterValue The counter value
     * @param symbol       The symbol
     * @return The pair (target, target counter value)
     */
    public State<Integer> getTransition(int state, int counterValue, I symbol);

    /**
     * Construct an ROCA (accepting by final state and counter equal to zero) from
     * the periodic description.
     * 
     * If L is the language encoded by the periodic description, then the language
     * of the ROCA is also L.
     * 
     * @return An ROCA accepting the language encoded by the periodic description.
     */
    public ROCA<?, I> toROCA();
}
