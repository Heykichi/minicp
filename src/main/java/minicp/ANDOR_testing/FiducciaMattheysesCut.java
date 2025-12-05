package minicp.ANDOR_testing;

import minicp.ANDOR_engine.ConstraintGraph;
import minicp.engine.core.IntVar;

import java.util.*;

/**
 * Utility class to find "cut vertices" for balanced graph partitioning
 * in a ConstraintGraph using unweighted, undirected edges.
 *
 * Requires: ConstraintGraph, IntVar from MiniCP.
 */
public class FiducciaMattheysesCut {
    // Fiduccia–Mattheyses algorithm
    public static Set<IntVar> fiducciaMattheysesCut(ConstraintGraph graph) {
        Set<IntVar> nodes = new HashSet<>(graph.getUnfixedVariables());
        if (nodes.size() <= 1) return Collections.emptySet();
        List<IntVar> nodeList = new ArrayList<>(nodes);

        // 1. Initial balanced partition
        Set<IntVar> partA = new HashSet<>();
        Set<IntVar> partB = new HashSet<>();
        for (int i = 0; i < nodeList.size(); i++) {
            if (i < nodeList.size() / 2) partA.add(nodeList.get(i));
            else partB.add(nodeList.get(i));
        }

        Map<IntVar, Integer> gainA = new HashMap<>();
        Map<IntVar, Integer> gainB = new HashMap<>();

        boolean improvement = true;
        while (improvement) {
            improvement = false;
            // Calculate gain for each node
            gainA.clear();
            gainB.clear();

            addGain(graph, partB, partA, gainA);
            addGain(graph, partA, partB, gainB);
            // Find max gain node in A and B
            IntVar bestA = argMaxByValue(gainA);
            IntVar bestB = argMaxByValue(gainB);

            int bestGainA = (bestA == null) ? Integer.MIN_VALUE : gainA.get(bestA);
            int bestGainB = (bestB == null) ? Integer.MIN_VALUE : gainB.get(bestB);

            if (bestGainA <= 0 && bestGainB <= 0) {
                break;
            }

            // Move node with highest gain that maintains balance
            if (bestA != null && (partA.size() > partB.size() || partA.size() == partB.size())) {
                int gain = gainA.get(bestA);
                if (gain > 0) {
                    partA.remove(bestA);
                    partB.add(bestA);
                    improvement = true;
                }
            }
            if (!improvement && bestB != null && (partB.size() > partA.size() || partA.size() == partB.size())) {
                int gain = gainB.get(bestB);
                if (gain > 0) {
                    partB.remove(bestB);
                    partA.add(bestB);
                    improvement = true;
                }
            }
        }
        // Nodes to cut = nodes with edges crossing partitions
        Set<IntVar> cutNodes = new HashSet<>();
        for (IntVar node : nodes) {
            for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                if ((partA.contains(node) && partB.contains(neighbor)) ||
                        (partB.contains(node) && partA.contains(neighbor))) {
                    cutNodes.add(node);
                    break;
                }
            }
        }
        return cutNodes;
    }

    private static void addGain(ConstraintGraph graph, Set<IntVar> partA, Set<IntVar> partB, Map<IntVar, Integer> gainB) {
        for (IntVar node : partB) {
            int ext = 0, inter = 0;
            for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                if (partA.contains(neighbor)) ext++;
                else if (partB.contains(neighbor)) inter++;
            }
            gainB.put(node, ext - inter);
        }
    }

    private static IntVar argMaxByValue(Map<IntVar, Integer> map) {
        IntVar bestKey = null;
        int bestValue = Integer.MIN_VALUE;

        for (Map.Entry<IntVar, Integer> e : map.entrySet()) {
            int value = e.getValue();
            if (value > bestValue) {
                bestValue = value;
                bestKey = e.getKey();
            }
        }
        return bestKey;
    }
}
