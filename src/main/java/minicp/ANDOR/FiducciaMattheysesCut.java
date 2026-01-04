package minicp.ANDOR;

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

            addGain(graph, partA, partB, gainA);
            addGain(graph, partB, partA, gainB);
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
        if (cutNodes.isEmpty()) return nodes;
        return cutNodes;
    }

    private static void addGain(ConstraintGraph graph, Set<IntVar> from, Set<IntVar> to,  Map<IntVar, Integer> gain) {
        gain.clear();
        for (IntVar node : from) {
            int ext = 0, inter = 0;
            for (IntVar neighbor : graph.getUnfixedNeighbors(node)) {
                if (to.contains(neighbor)) ext++;
                else if (from.contains(neighbor)) inter++;
            }
            gain.put(node, ext - inter);
        }
    }

    private static IntVar argMaxByValue(Map<IntVar, Integer> scores) {
        return scores.entrySet().stream()
                .max(Comparator.<Map.Entry<IntVar,Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparingInt(e -> -e.getKey().getId())) // ou +getId() selon tie-break
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
