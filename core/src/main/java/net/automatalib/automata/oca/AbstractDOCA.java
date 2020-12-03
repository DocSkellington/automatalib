package net.automatalib.automata.oca;

import java.util.Set;

import net.automatalib.words.Alphabet;

/**
 * Abstract class for DOCAs.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractDOCA<L, I> extends AbstractOCA<L, I> implements DOCA<L, I> {
    protected L initialLocation;

    public AbstractDOCA(final Alphabet<I> alphabet) {
        super(alphabet);
    }

    public AbstractDOCA(final Alphabet<I> alphabet, final AcceptanceMode acceptanceMode) {
        super(alphabet, acceptanceMode);
    }

    @Override
    public Set<L> getInitialLocations() {
        return DOCA.super.getInitialLocations();
    }
    
    @Override
    public L getInitialLocation() {
        return initialLocation;
    }

    @Override
    public L addInitialLocation(final boolean accepting) {
        L location = addLocation(accepting);
        initialLocation = location;
        return initialLocation;
    }
}
