package minicp.ANDOR;

import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.state.StateStack;

import java.util.*;
import java.util.stream.Collectors;


// TO DO
// REMOVE
// CUT


public class ConstraintGraph {

    private Map<IntVar, Set<IntVar>> adjacencyList;
    private final Solver cp;
    private StateStack<Set<IntVar>> stateVars;

    public ConstraintGraph(Solver cp) {
        this.cp = cp;
        this.adjacencyList = new HashMap<>();
        this.stateVars = new StateStack<Set<IntVar>>(cp.getStateManager());
    }

    /**
     * Retrieves the list of unfixed neighbors for the given variable.
     *
     * @param key the variable whose unfixed neighbors are to be retrieved
     * @return a list of variables representing the unfixed neighbors of the given variable
     */
    public List<IntVar> getUnfixedNeighbors (IntVar key){
        return adjacencyList.get(key).stream()
                .filter(var -> !var.isFixed())
                .collect(Collectors.toList());
    }

    public ArrayList<IntVar> getVariables() {
        return adjacencyList.keySet().stream()
                .filter(var -> !var.isFixed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<IntVar> getStateVariables(){return this.stateVars.getLastElement();}

    /**
     * Adds a node to the constraint graph if the node does not already exist.
     * It initializes an empty set of neighbors in the adjacency list.
     *
     * @param node the variable to be added as a node in the graph
     */
    public void addNode(IntVar node) {
        this.adjacencyList.putIfAbsent(node, new HashSet<IntVar>());
    }


    /**
     * Adds multiple nodes to the constraint graph if the node does not already exist.
     * For each node, it initializes an empty set of neighbors in the adjacency list.
     *
     * @param nodes an array of variables to be added as nodes in the graph
     */
    public void addNode(IntVar[] nodes) {
        for (IntVar n : nodes) {
            this.adjacencyList.putIfAbsent(n, new HashSet<IntVar>());
        }
    }


    /**
     * Adds nodes and adds edges between them.
     *
     * @param node1 node to be connected
     * @param node2 node to be connected
     */
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
     * Adds nodes and adds edges between all pairs of nodes in the given array.
     *
     * @param nodes array of nodes to be connected
     */
    public void addEdge(IntVar[] nodes) {
        addNode(nodes);
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                addEdge(nodes[i], nodes[j]);
            }
        }
    }

    public void computeStateVariables(){
        this.stateVars.push(new HashSet<IntVar>(getVariables()));
    }

    public void computeStateVariables(IntVar variable){
        Set<IntVar> newStateVars = this.stateVars.getLastElement();
        newStateVars.add(variable);
        this.stateVars.push((Set<IntVar>)newStateVars);
    }

    public void computeStateVariables(IntVar[] variables){
        Set<IntVar> newStateVars = this.stateVars.getLastElement();
        newStateVars.addAll(Arrays.asList(variables));
        this.stateVars.push(newStateVars);
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
            if (!visited.contains(node) && !node.isFixed()) {
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



    /**
     * Prints the independent subgraphs of the constraint graph.
     *
     * The method retrieves all independent subgraphs using the {@code findIndependentSubgraphs} method.
     * Each subgraph is represented as a set of {@code IntVar} nodes. For each subgraph, the nodes are converted
     * to their hash code representation.
     * The formatted string representation of all subgraphs is then printed to the console.
     */
    public void PrintSubgraph(){
        List<Set<IntVar>> subgraphs = findIndependentSubgraphs();
        System.out.println(
                subgraphs.stream()
                        .map(set -> set.stream()
                                .map(obj -> String.valueOf(obj.hashCode()))
                                .collect(Collectors.joining(", ", "[", "]")))
                        .collect(Collectors.joining(", ", "[", "]"))
        );
    }

    /**
     * Removes the specified node and its associated edges from the constraint graph.
     * This operation removes the node from the adjacency list and
     * also removes it from the neighbor sets of other nodes.
     *
     * @param nodeToRemove the variable representing the node to be removed from the graph
     */
    public void RemoveNode(IntVar nodeToRemove) {
        adjacencyList.remove(nodeToRemove);
        for (Set<IntVar> neighbors : adjacencyList.values()) {
            neighbors.remove(nodeToRemove);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IntVar, Set<IntVar>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey().hashCode()).append(" : ").append(entry.getKey()).append(" -> ");
            if (entry.getValue().isEmpty()) {
                sb.append(" / ");
            } else {
                sb.append(entry.getValue().stream()
                        .map(Object::hashCode)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
