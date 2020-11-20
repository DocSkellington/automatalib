package net.automatalib.automata.oca;

/**
 * Interface for the ROCA (realtime one-counter automaton), which is a DOCA
 * without epsilon-transitions.
 * 
 * @param <L> Location type
 * @param <I> Input symbol type
 * 
 * @author Gaëtan Staquet
 */
public interface ROCA<L, I> extends DOCA<L, I> {

}
