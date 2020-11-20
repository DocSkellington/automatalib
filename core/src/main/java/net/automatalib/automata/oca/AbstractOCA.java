package net.automatalib.automata.oca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.automatalib.words.Alphabet;

/**
 * Abstract class for OCAs.
 * 
 * @param <L> Location type
 * @param <I> Input alphabet type
 * 
 * @author Gaëtan Staquet
 */
public abstract class AbstractOCA<L, I> implements OCA<L, I> {
    protected final AcceptanceMode acceptanceMode;

    protected final Alphabet<I> alphabet;

    protected final List<L> locations;
    protected final Set<L> initialLocations;

    public AbstractOCA(final Alphabet<I> alphabet) {
        this.alphabet = alphabet;
        locations = new ArrayList<>();
        initialLocations = new HashSet<>();
        acceptanceMode = AcceptanceMode.BOTH;
    }

    public AbstractOCA(final Alphabet<I> alphabet, AcceptanceMode acceptanceMode) {
        this.alphabet = alphabet;
        locations = new ArrayList<>();
        initialLocations = new HashSet<>();
        this.acceptanceMode = acceptanceMode;
    }

    public Alphabet<I> getAlphabet() {
        return alphabet;
    }

    @Override
    public int getLocationId(L loc) {
        return locations.indexOf(loc);
    }

    @Override
    public L getLocation(int id) {
        return locations.get(id);
    }

    @Override
    public Set<L> getInitialLocations() {
        return initialLocations;
    }

    public List<L> getLocations() {
        return locations;
    }

    @Override
    public L addInitialLocation(boolean accepting) {
        L location = addLocation(accepting);
        initialLocations.add(location);
        return location;
    }

    @Override
    public AcceptanceMode getAcceptanceMode() {
        return acceptanceMode;
    }
}
