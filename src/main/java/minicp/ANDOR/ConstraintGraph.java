package minicp.ANDOR;

import java.util.*;


// TO DO
// STATE
// REMOVE

public class ConstraintGraph {

    private Map<String, Set<String>> adjacencyList;

    public ConstraintGraph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(String node) {
        this.adjacencyList.putIfAbsent(node, new HashSet<>());
    }

    public void addEdge(String node1, String node2) {
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
    public List<Set<String>> findIndependentSubgraphs() {
        List<Set<String>> subgraphs = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String node : adjacencyList.keySet()) {
            if (!visited.contains(node)) {
                Set<String> subgraph = new HashSet<>();
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
    private void dfs(String node, Set<String> visited, Set<String> component) {
        visited.add(node);
        component.add(node);

        for (String neighbor : adjacencyList.get(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, component);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
