package net.automatalib.automata.oca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import net.automatalib.visualization.DefaultVisualizationHelper;
import net.automatalib.visualization.VisualizationHelper;
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

    public AbstractOCA(final Alphabet<I> alphabet, final AcceptanceMode acceptanceMode) {
        this.alphabet = alphabet;
        locations = new ArrayList<>();
        initialLocations = new HashSet<>();
        this.acceptanceMode = acceptanceMode;
    }

    @Override
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
        return Collections.unmodifiableSet(initialLocations);
    }

    @Override
    public List<L> getLocations() {
        return Collections.unmodifiableList(locations);
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

    @Override
    public Collection<L> getNodes() {
        return Collections.unmodifiableCollection(getLocations());
    }

    @Override
    public L getTarget(OCAViewEdge<L, I> edge) {
        return edge.target;
    }

    @Override
    public Collection<OCAViewEdge<L, I>> getOutgoingEdges(final L location) {
        final List<OCAViewEdge<L, I>> edges = new ArrayList<>();

        for (final I a : getAlphabet()) {
            for (int i = 0; i < getNumberOfTransitionFunctions(); i++) {
                State<L> state = new State<>(location, i);
                Set<State<L>> targets = getSuccessors(state, a);
                for (State<L> target : targets) {
                    int counterOperation = 0;
                    L targetLocation = null;
                    if (target != null) {
                        counterOperation = target.getCounterValue() - i;
                        targetLocation = target.getLocation();
                    }
                    OCAViewEdge<L, I> edge = new OCAViewEdge<>(a, i, counterOperation, targetLocation);
                    edges.add(edge);
                }
            }
        }

        return edges;
    }

    @Override
    public VisualizationHelper<L, OCAViewEdge<L, I>> getVisualizationHelper() {
        return new DefaultVisualizationHelper<L, OCAViewEdge<L, I>>() {
            @Override
            protected Collection<L> initialNodes() {
                return getInitialLocations();
            }

            @Override
            public boolean getNodeProperties(L node, Map<String, String> properties) {
                super.getNodeProperties(node, properties);

                properties.put(NodeAttrs.SHAPE,
                        isAcceptingLocation(node) ? NodeShapes.DOUBLECIRCLE : NodeShapes.CIRCLE);
                properties.put(NodeAttrs.LABEL, "L" + getLocationId(node));

                return true;
            }

            @Override
            public boolean getEdgeProperties(L src, OCAViewEdge<L, I> edge, L tgt, Map<String, String> properties) {
                final I input = edge.input;
                final int counterValue = edge.counterValue;
                final int counterOperation = edge.counterOperation;

                String guard;
                if (counterValue >= getNumberOfTransitionFunctions() - 1) {
                    guard = ">=" + (getNumberOfTransitionFunctions() - 1);
                } else {
                    guard = "=" + counterValue;
                }
                properties.put(EdgeAttrs.LABEL, input + ", " + guard + ", " + counterOperation);

                if (counterValue == 0) {
                    properties.put(EdgeAttrs.COLOR, "blue");
                } else if (counterValue == 1) {
                    properties.put(EdgeAttrs.COLOR, "green");
                } else if (counterValue == 2) {
                    properties.put(EdgeAttrs.COLOR, "red");
                }

                return true;
            }
        };
    }

    @Override
    public int size() {
        return locations.size();
    }
}
