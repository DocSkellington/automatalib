package net.automatalib.automata.oca;

import java.util.Set;

import net.automatalib.words.VPDAlphabet;

/**
 * Abstract class for VCAs.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractVCA<L, I> extends AbstractOCA<L, I> implements VCA<L, I> {
    protected final int m;
    protected final VPDAlphabet<I> alphabet;
    protected L initialLocation;

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

    @Override
    public Set<L> getInitialLocations() {
        return VCA.super.getInitialLocations();
    }

    @Override
    public L getInitialLocation() {
        return initialLocation;
    }

    @Override
    public L addInitialLocation(boolean accepting) {
        L location = addLocation(accepting);
        initialLocation = location;
        return initialLocation;
    }
}
