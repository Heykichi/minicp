package minicp.ANDOR_engine;

import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.state.StateStack;

import java.util.*;
import java.util.stream.Collectors;


// TO DO
// REMOVE
// CUT


public class ConstraintGraph {

    private final Solver cp;
    private Map<IntVar, Set<IntVar>> adjacencyList = new HashMap<>();
    private StateStack<Set<IntVar>> stateVars;
    private Set<IntVar> removedNodes = new HashSet<>();

    public ConstraintGraph(Solver cp) {
        this.cp = cp;
        this.stateVars = new StateStack<Set<IntVar>>(cp.getStateManager());
        this.stateVars.push(adjacencyList.keySet());
    }

    public boolean solutionFound() {
        for (IntVar var : this.stateVars.getLastElement()) {
            if (!var.isFixed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the list of unfixed neighbors for the given variable.
     *
     * @param key the variable whose unfixed neighbors are to be retrieved
     * @return a list of variables representing the unfixed neighbors of the given variable
     */
    public Set<IntVar> getUnfixedNeighbors(IntVar key) {
        return adjacencyList.get(key).stream()
                .filter(var -> !var.isFixed() && this.stateVars.getLastElement().contains(var) && !this.removedNodes.contains(var))
                .collect(Collectors.toSet());
    }

    public Set<IntVar> getUnfixedVariables() {
        return this.stateVars.getLastElement().stream()
                .filter(var -> !var.isFixed() && !this.removedNodes.contains(var))
                .collect(Collectors.toSet());
    }

    public void newState(){
        Set<IntVar> newStateValue = new HashSet<>(this.stateVars.getLastElement());
        this.stateVars.push(newStateValue);
        this.removedNodes.clear();
    }

    public void newState(Set<IntVar> Variables){
        this.stateVars.push(Variables);
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
        this.stateVars.push(new HashSet<IntVar>(getUnfixedVariables()));
    }


    /**
     * Finds all independent subgraphs in the constraint graph.
     *
     * @return a list of sets, where each set contains the nodes of an independent subgraph
     */
    public List<Set<IntVar>> findConnectedComponents() {
        List<Set<IntVar>> subgraphs = new ArrayList<>();
        Set<IntVar> visited = new HashSet<>();

        for (IntVar node : this.stateVars.getLastElement()) {
            if (!visited.contains(node) && !node.isFixed() && !this.removedNodes.contains(node)) {
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
    public void printSubgraph(){
        List<Set<IntVar>> subgraphs = findConnectedComponents();
        System.out.println(
                subgraphs.stream()
                        .map(set -> set.stream()
                                .map(obj -> String.valueOf(obj.getId()))
                                .collect(Collectors.joining(", ", "[", "]")))
                        .collect(Collectors.joining(", ", "[", "]"))
        );
    }

    public List<SubBranch> splitGraph(int sizeToFix){
        List<Set<IntVar>> subgraphs = this.findConnectedComponents();
        List<SubBranch> subBranches = new ArrayList<>();
        if (subgraphs.size() > 1 ){
            for (Set<IntVar> s : subgraphs){
                subBranches.add(new SubBranch(s,s.size() <= sizeToFix ));
            }
            return subBranches;
        }
        return null;
    }


    public void removeNode(IntVar nodeToRemove) {
        this.removedNodes.add(nodeToRemove);
    }

    public void removeNode(IntVar[] nodeToRemove) {
        this.removedNodes.addAll(Arrays.asList(nodeToRemove));
    }

    public void removeNode(Set<IntVar> nodeToRemove) {
        this.removedNodes.addAll(nodeToRemove);
    }

    public void restoreNode(IntVar nodeToRestore) {
        this.removedNodes.remove(nodeToRestore);
    }

    public void restoreNode(IntVar[] nodeToRestore) {
        Arrays.asList(nodeToRestore).forEach(this.removedNodes::remove);
    }
    public void restoreNode(Set<IntVar> nodeToRestore) {
        this.removedNodes.removeAll(nodeToRestore);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IntVar, Set<IntVar>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey().getId()).append(" : ").append(entry.getKey()).append(" -> ");
            if (entry.getValue().isEmpty()) {
                sb.append(" / ");
            } else {
                sb.append(entry.getValue().stream()
                        .map(IntVar::getId)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
