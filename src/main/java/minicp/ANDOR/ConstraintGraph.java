package minicp.ANDOR;

import minicp.engine.core.IntDomain;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.engine.core.SparseSetDomain;
import minicp.state.StateManager;
import minicp.state.StateSparseSet;

import java.util.*;
import java.util.stream.Collectors;


// TO DO
// STATE => state manger => specific statestack
// REMOVE
// CUT


public class ConstraintGraph {

    private Map<IntVar, Set<IntVar>> adjacencyList;
    private final Solver cp;

    public ConstraintGraph(Solver cp) {
        this.cp = cp;
        this.adjacencyList = new HashMap<>();
    }

    public List<IntVar> getUnfixedNeighbors (IntVar key){
        return adjacencyList.get(key).stream()
                .filter(var -> !var.isFixed())
                .collect(Collectors.toList());
    }

    public void addNode(IntVar node) {
        this.adjacencyList.putIfAbsent(node, new HashSet<IntVar>());
    }

    public void addNode(IntVar[] node) {
        for (IntVar n : node) {
            this.adjacencyList.putIfAbsent(n, new HashSet<IntVar>());
        }
    }

    public void addEdge(IntVar node1, IntVar node2) {
        if (node1.equals(node2)) {
            throw new IllegalArgumentException("Self-edge are not allowed");
        }
        addNode(node1);
        addNode(node2);
        adjacencyList.get(node1).add(node2);
        adjacencyList.get(node2).add(node1);
    }
    
     

    /**
     * Finds all independent subgraphs in the constraint graph.
     *
     * @return a list of sets, where each set contains the nodes of an independent subgraph
     */
    public List<Set<IntVar>> findIndependentSubgraphs() {
        List<Set<IntVar>> subgraphs = new ArrayList<>();
        Set<IntVar> visited = new HashSet<>();

        for (IntVar node : adjacencyList.keySet()) {
            if (!visited.contains(node)) {
                Set<IntVar> subgraph = new HashSet<>();
                dfs(node, visited, subgraph);
                subgraphs.add(subgraph);
            }
        }

        return subgraphs;
    }

    /**
     * Performs depth-first search to find connected components.
     *
     * @param node      the current node being visited
     * @param visited   set of nodes that have been visited
     * @param component the current component being built
     */
    private void dfs(IntVar node, Set<IntVar> visited, Set<IntVar> component) {
        visited.add(node);
        component.add(node);

        for (IntVar neighbor : this.getUnfixedNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, component);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IntVar, Set<IntVar>> entry : adjacencyList.entrySet()) {
            sb.append(System.identityHashCode(entry.getKey()));
            sb.append(" : ");
            sb.append(entry.getKey());
            sb.append(" -> ");
            if (entry.getValue().isEmpty()) {
                sb.append(" / ");
            } else {
                sb.append(entry.getValue().stream()
                        .map(System::identityHashCode)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
