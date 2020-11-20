package net.automatalib.automata.oca;

/**
 * Interface for the VCA (visibly one-counter automaton), which is a ROCA using
 * a push-down alphabet.
 * 
 * That is, VCAs are deterministic automata that do not allow
 * epsilon-transitions. Moreover, the counter operations are dictated by the
 * input symbols. That is, each call in the push-down alphabet increments the
 * counter by one (+1), each return decrements by one (-1), while internal
 * symbols do not change the counter (0).
 * 
 * @param <L> Location type
 * @param <I> Input symbol type
 * 
 * @author Gaëtan Staquet
 */
public interface VCA<L, I> extends ROCA<L, I> {

    @Override
    default void setSuccessor(L start, int counterValue, I input, int counterOperation, L target) {
        setSuccessor(start, counterValue, input, target);
    }

    /**
     * Set the successor of the state (start, counterValue).
     * 
     * The counter operation is deduced from the input symbol.
     * 
     * @param start        The starting location
     * @param counterValue The counter value test
     * @param input        The input symbol
     * @param target       The target location
     */
    void setSuccessor(L start, int counterValue, I input, L target);

    /**
     * Set the successor of the state (start, counterValue).
     * 
     * The counter operation is +1, since the input is a call symbol.
     * 
     * @param start        The starting location
     * @param counterValue The counter value test
     * @param input        The input call symbol
     * @param target       The target location
     */
    void setCallSuccessor(L start, int counterValue, I input, L target);

    /**
     * Set the successor of the state (start, counterValue).
     * 
     * The counter operation is -1, since the input is a return symbol.
     * 
     * @param start        The starting location
     * @param counterValue The counter value test
     * @param input        The input return symbol
     * @param target       The target location
     */
    void setReturnSuccessor(L start, int counterValue, I input, L target);

    /**
     * Set the successor of the state (start, counterValue).
     * 
     * The counter operation is 0, since the input is an internal symbol.
     * 
     * @param start        The starting location
     * @param counterValue The counter value test
     * @param input        The input internal symbol
     * @param target       The target location
     */
    void setInternalSuccessor(L start, int counterValue, I input, L target);
}
