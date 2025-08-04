package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;

import java.util.*;

public class GreedyPartitioning {

    public static Set<IntVar>[] findBalancedSeparator(ConstraintGraph graph) {
        // Get all active nodes: unfixed and not removed
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
        return new Set[]{separator, groupA, groupB};
    }
}
