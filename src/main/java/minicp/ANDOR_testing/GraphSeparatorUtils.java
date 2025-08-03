package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;

import java.util.*;

public class GraphSeparatorUtils {

    public static class SeparatorResult {
        public final Set<IntVar> groupA;
        public final Set<IntVar> groupB;
        public final Set<IntVar> separator;
        public SeparatorResult(Set<IntVar> groupA, Set<IntVar> groupB, Set<IntVar> separator) {
            this.groupA = groupA;
            this.groupB = groupB;
            this.separator = separator;
        }
    }

    public static SeparatorResult findBalancedSeparator(Solver cp) {
        // Get all active nodes: unfixed and not removed
        ConstraintGraph graph = cp.getGraphWithStart();
        List<IntVar> allNodes = new ArrayList<>(graph.getUnfixedVariables());

        // Sort by id for deterministic split
        allNodes.sort(Comparator.comparingInt(IntVar::getId));

        int n = allNodes.size();
        Set<IntVar> groupA = new HashSet<>(allNodes.subList(0, n/2));
        Set<IntVar> groupB = new HashSet<>(allNodes.subList(n/2, n));

        // Step 1: Initial separator set (vertices linking both groups)
        Set<IntVar> separator = new HashSet<>();
        for (IntVar v : allNodes) {
            boolean inA = groupA.contains(v);
            boolean inB = groupB.contains(v);
            for (IntVar nbr : graph.getUnfixedNeighbors(v)) {
                if ((inA && groupB.contains(nbr))) {
                    separator.add(v);
                    groupA.remove(v);
                    break;
                }
                if (inB && groupA.contains(nbr)){
                    separator.add(v);
                    groupB.remove(v);
                    break;
                }
            }
        }

        // Step 2: Prune redundant separators (optional, but improves minimality)
        //separator = pruneSeparator(graph, groupA, groupB, separator, allNodes);

        return new SeparatorResult(groupA, groupB, separator);
    }

    private static Set<IntVar> pruneSeparator(ConstraintGraph graph, Set<IntVar> groupA, Set<IntVar> groupB,
                                              Set<IntVar> separator, List<IntVar> allNodes) {
        Set<IntVar> minimalSeparator = new HashSet<>(separator);

        for (IntVar v : separator) {
            Set<IntVar> tempSeparator = new HashSet<>(minimalSeparator);
            tempSeparator.remove(v);

            if (!isSeparated(graph, groupA, groupB, tempSeparator, allNodes)) {
                // Removing v still disconnects A and B, so v is redundant
                minimalSeparator.remove(v);
            }
        }
        return minimalSeparator;
    }

    // Checks if groupA and groupB are separated after removing separator nodes
    private static boolean isSeparated(ConstraintGraph graph, Set<IntVar> groupA, Set<IntVar> groupB,
                                       Set<IntVar> separator, List<IntVar> allNodes) {
        // BFS from any node in groupA (not in separator)
        Queue<IntVar> queue = new LinkedList<>();
        Set<IntVar> visited = new HashSet<>();
        IntVar start = null;
        for (IntVar v : groupA) {
            if (!separator.contains(v)) {
                start = v;
                break;
            }
        }
        if (start == null) return true; // nothing to search

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            IntVar curr = queue.poll();
            for (IntVar nbr : graph.getUnfixedNeighbors(curr)) {
                if (separator.contains(nbr) || visited.contains(nbr))
                    continue;
                visited.add(nbr);
                queue.add(nbr);
            }
        }

        // If any node in groupB is visited, A and B are still connected
        for (IntVar v : groupB) {
            if (visited.contains(v)) return false;
        }
        return true;
    }
}
