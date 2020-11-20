package net.automatalib.automata.oca;

/**
 * A pair storing the target location and the counter operation of a transition.
 * 
 * @param <L> Location type
 * 
 * @author Gaëtan Staquet
 */
public class TransitionTarget<L> {
    public final L targetLocation;
    public final int counterOperation;

    public TransitionTarget(final L targetLocation, final int counterOperation) {
        this.targetLocation = targetLocation;
        this.counterOperation = counterOperation;
    }

    @Override
    public String toString() {
        return "(" + targetLocation + ", " + counterOperation + ")";
    }
}
