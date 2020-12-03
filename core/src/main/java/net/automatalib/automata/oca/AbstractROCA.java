package net.automatalib.automata.oca;

import net.automatalib.words.Alphabet;

/**
 * Abstract class for ROCAs.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractROCA<L, I> extends AbstractDOCA<L, I> implements ROCA<L, I> {

    public AbstractROCA(final Alphabet<I> alphabet) {
        super(alphabet);
    }

    public AbstractROCA(final Alphabet<I> alphabet, final AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
    }
}
