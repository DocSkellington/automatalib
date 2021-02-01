package net.automatalib.automata.oca.automatoncountervalues;

import net.automatalib.words.Alphabet;

/**
 * Abstract class for automata augmented with counter values.
 * 
 * See {@link DefaultAutomatonWithCounterValues} for a concrete type.
 * @author Gaëtan Staquet
 */
public abstract class AbstractAutomatonWithCounterValues<S, I> implements AutomatonWithCounterValues<S, I> {
    protected final Alphabet<I> alphabet;

    public AbstractAutomatonWithCounterValues(Alphabet<I> alphabet) {
        this.alphabet = alphabet;
    }

    @Override
    public Alphabet<I> getInputAlphabet() {
        return alphabet;
    }
}
