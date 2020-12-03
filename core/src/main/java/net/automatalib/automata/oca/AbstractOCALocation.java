package net.automatalib.automata.oca;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.automatalib.commons.smartcollections.ArrayStorage;
import net.automatalib.commons.util.nid.AbstractMutableNumericID;

/**
 * Abstract location for one-counter automata.
 * 
 * This implementation assumes the number of transitions and the size of the
 * alphabet do not change over time.
 * 
 * @param <T> The type of target of the transitions
 * 
 * @author Gaëtan Staquet
 */
public class AbstractOCALocation<T> extends AbstractMutableNumericID {
    private static final long serialVersionUID = 755795684504017747L;

    protected final ArrayStorage<ArrayStorage<@Nullable T>> transitions;

    protected boolean accepting;

    public AbstractOCALocation(final int numberFunctionsOfTransitions, final int initialNumberOfSymbols, final int id,
            boolean accepting) {
        this.transitions = new ArrayStorage<>(numberFunctionsOfTransitions);
        for (int i = 0; i < numberFunctionsOfTransitions; i++) {
            this.transitions.set(i, new ArrayStorage<>(initialNumberOfSymbols));
        }
        setId(id);
        this.accepting = accepting;
    }

    /**
     * Computes the successors of the location, given a counter value
     * 
     * @param symbolId     The symbol index
     * @param counterValue The counter value
     * @return Each pair of successor and the counter operation.
     */
    @Nullable
    public T getSuccessors(final int symbolId, final int counterValue) {
        int m = Math.min(counterValue, transitions.size() - 1);
        return transitions.get(m).get(symbolId);
    }

    public final boolean isAccepting() {
        return accepting;
    }

    public void setSuccessors(final int counterValue, final int symbolId, final T target) {
        int m = Math.min(counterValue, transitions.size() - 1);
        transitions.get(m).set(symbolId, target);
    }

    @Override
    public String toString() {
        return "q" + getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
