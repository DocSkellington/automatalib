package net.automatalib.automata.oca;

import net.automatalib.words.VPDAlphabet;

/**
 * Abstract class for VCAs.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractVCA<L, I> extends AbstractDOCA<L, I> implements VCA<L, I> {
    protected final int m;
    protected final VPDAlphabet<I> alphabet;

    public AbstractVCA(final int m, final VPDAlphabet<I> alphabet) {
        super(alphabet);
        this.alphabet = alphabet;
        this.m = m;
    }

    public AbstractVCA(final int m, final VPDAlphabet<I> alphabet, AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
        this.alphabet = alphabet;
        this.m = m;
    }

    public VPDAlphabet<I> getAlphabet() {
        return alphabet;
    }
}
